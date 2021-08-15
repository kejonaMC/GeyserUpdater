package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.Task;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.projectg.geyserupdater.common.update.age.DownloadResult;
import com.projectg.geyserupdater.common.util.WebUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

    private final UpdateManager updateManager;
    private final UpdaterScheduler scheduler;
    private final int downloadTimeLimit;

    private final List<Updatable> queue = new LinkedList<>();

    // Used for making sure one download is ever running
    private boolean isDownloading = false;

    // Used by the hang checker to check if the current download is the same as when it was scheduled
    @Nullable private Updatable currentUpdate = null;

    // Used by the hang checker to cancel the download if necessary
    @Nullable private Task downloader = null;

    public DownloadManager(UpdateManager updateManager, UpdaterScheduler scheduler, int downloadTimeLimit) {
        this.updateManager = updateManager;
        this.scheduler = scheduler;
        this.downloadTimeLimit = downloadTimeLimit;
    }

    public void queue(Updatable updatable) {
        queue.add(updatable);
        if (!isDownloading) {
            downloadAll();
        }
    }

    private void downloadAll() {
        isDownloading = true;

        // Run the download on a new thread
        this.downloader = scheduler.run(() -> {

            for (int i = 0; ; i++) {
                Updatable updatable = queue.get(i);
                if (updatable == null) {
                    break;
                }
                currentUpdate = updatable;

                // Create a timer to stop this download from running too long. Either the hang checker is cancelled or the hang checker cancels this.
                Task hangChecker = scheduleHangChecker(updatable);

                try {
                    WebUtils.downloadFile(updatable.downloadUrl, updatable.outputFile);
                } catch (IOException e) {
                    UpdaterLogger.getLogger().error("Failed to download file at location " + updatable.outputFile + " with URL: " + updatable.downloadUrl);
                    e.printStackTrace();
                    updateManager.finish(updatable, DownloadResult.UNKNOWN_FAIL);
                    continue;
                }

                hangChecker.cancel();
                updateManager.finish(updatable, DownloadResult.SUCCESS);
            }

            // Revert everything while having it locked so that the state is always correctly read by a different thread
            synchronized (this) {
                // Clear the queue here so that the for loop above doesn't get messed up
                queue.clear();
                isDownloading = false;
                currentUpdate = null;
                downloader = null;
            }

            // Everything should be downloaded now unless the queue was added to after the above for loop was finished
            // But the synchronized block was not entered
        }, true);
    }

    private Task scheduleHangChecker(Updatable updatable) {
        // The time to allow the download to take, in seconds

        return scheduler.runDelayed(() -> {
            if (!isDownloading || downloader == null) {
                throw new AssertionError("HangChecker should not execute while nothing is downloading.");
            }

            if (updatable == currentUpdate) {
                // Revert everything while having it locked so that the state is always correctly read by a different thread
                synchronized (this) {
                    queue.clear();
                    isDownloading = false;
                    currentUpdate = null;
                    downloader.cancel();
                    downloader = null;
                }

                updateManager.finish(updatable, DownloadResult.TIMEOUT);

                UpdaterLogger logger = UpdaterLogger.getLogger();
                logger.error("The download queue has been stopped and cleared because the download for " + updatable + " took longer than " + downloadTimeLimit +
                        " seconds. Increase the download-time-limit in the config if you have a slow internet connection.");

                try {
                    boolean deletedFailedFile = Files.deleteIfExists(updatable.outputFile);
                    logger.debug("Failed download for " + updatable + " had a file?: " + deletedFailedFile);
                } catch (IOException e) {
                    logger.error("Failed to delete failed download file of " + updatable);
                    e.printStackTrace();
                }
            }
        }, true, downloadTimeLimit, TimeUnit.SECONDS);
    }
}

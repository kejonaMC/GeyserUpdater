package dev.projectg.geyserupdater.common.update;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.scheduler.Task;
import dev.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import dev.projectg.geyserupdater.common.util.WebUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

            while (!queue.isEmpty()) {
                currentUpdate = Objects.requireNonNull(queue.get(0));

                // Create a timer to stop this download from running too long. Either the hang checker is cancelled or the hang checker cancels this.
                Task hangChecker = scheduleHangChecker(currentUpdate);

                try {
                    Files.createDirectories(currentUpdate.outputFile.getParent());
                    WebUtils.downloadFile(currentUpdate.downloadUrl, currentUpdate.outputFile);
                } catch (IOException e) {
                    UpdaterLogger.getLogger().error("Caught exception while downloading file " + currentUpdate.outputFile + " with URL: " + currentUpdate.downloadUrl);
                    e.printStackTrace();
                    updateManager.finish(currentUpdate, DownloadResult.UNKNOWN_FAIL);
                    continue;
                }

                hangChecker.cancel();
                queue.remove(0);
                updateManager.finish(currentUpdate, DownloadResult.SUCCESS);
            }

            // Revert everything while having it locked so that the state is always correctly read by a different thread
            synchronized (this) {
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

                UpdaterLogger.getLogger().error("The download queue has been stopped and cleared because the download for " + updatable + " took longer than " + downloadTimeLimit +
                        " seconds. Increase the download-time-limit in the config if you have a slow internet connection.");

                updateManager.finish(updatable, DownloadResult.TIMEOUT);
            }
        }, true, downloadTimeLimit, TimeUnit.SECONDS);
    }

    private void shutdown() {
        //todo finish this
    }
}

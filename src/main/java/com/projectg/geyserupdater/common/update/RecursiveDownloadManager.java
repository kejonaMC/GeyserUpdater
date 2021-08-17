package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.GeyserUpdater;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.Task;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.projectg.geyserupdater.common.util.WebUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused") // Keeping this to compare to DownloadManager
public class RecursiveDownloadManager {

    private final GeyserUpdater updater;

    private final List<Updatable> queue = new LinkedList<>();

    // Used for making sure one download is ever running
    private boolean isDownloading = false;

    // Used by the hang checker to check if the current download is the same as when it was scheduled
    @Nullable private Updatable currentUpdate = null;

    // Used by the hang checker to cancel the download if necessary
    @Nullable private Task downloader = null;

    // maybe refactor this to use a for loop instead of being recursive? i dunno

    public RecursiveDownloadManager(GeyserUpdater updater) {
        this.updater = updater;
    }

    public void queue(Updatable updatable) {
        queue.add(updatable);
        downloadIfPossible();
    }

    private void download(Updatable updatable) {
        isDownloading = true;
        currentUpdate = updatable;

        UpdaterScheduler scheduler = updater.getScheduler();

        // Run the download on a new thread
        this.downloader = scheduler.run(() -> {

            // Create a timer to stop this download from running too long. Either the hang checker is cancelled or the hang checker cancels this.
            Task hangChecker = scheduleHangChecker(updater, updatable);

            try {
                WebUtils.downloadFile(updatable.downloadUrl, updatable.outputFile);
            } catch (IOException ioException) {
                UpdaterLogger.getLogger().error("Failed to download file: " + updatable.downloadUrl + " for " + updatable);
                ioException.printStackTrace();
            }
            hangChecker.cancel();

            // Revert everything while having it locked so that the state is always correctly read by original thread
            synchronized (this) {
                this.queue.remove(updatable);
                this.isDownloading = false;
                this.currentUpdate = null;
                this.downloader = null;
            }

            // Initiate another download if necessary
            downloadIfPossible();
        }, true);
    }

    private void downloadIfPossible() {
        if (!queue.isEmpty() && !isDownloading) {
            download(queue.get(0));
        }
    }

    private Task scheduleHangChecker(GeyserUpdater updater, Updatable updatable) {
        // The time to allow the download to take, in seconds
        int downloadTimeLimit = updater.getConfig().getDownloadTimeLimit();

        return updater.getScheduler().runDelayed(() -> {
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

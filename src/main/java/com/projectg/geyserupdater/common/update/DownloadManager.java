package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.GeyserUpdater;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.Task;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.projectg.geyserupdater.common.util.WebUtils;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

    private final GeyserUpdater updater;
    private final Path outputDirectory;

    private final List<Updatable> queue = new LinkedList<>();

    // Used for making sure one download is ever running
    private boolean isDownloading = false;

    // Used by the hang checker to check if the current download is the same as when it was scheduled
    @Nullable private Updatable currentUpdate = null;

    // Used by the hang checker to cancel the download if necessary
    @Nullable private Task downloader = null;

    // todo: maybe refactor this to use a for loop instead of being recursive? i dunno

    public DownloadManager(GeyserUpdater updater, Path outputDirectory) {
        //todo: move outputDirectory to Updatable
        this.updater = updater;
        this.outputDirectory = outputDirectory;
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

            WebUtils.downloadFile(updatable.downloadUrl, outputDirectory.resolve(updatable.outputFileName));
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
            if (isDownloading && downloader != null && this.currentUpdate == updatable) {
                // Revert everything while having it locked so that the state is always correctly read by original thread
                synchronized (this) {
                    isDownloading = false;
                    currentUpdate = null;
                    downloader.cancel();
                    downloader = null;
                }

                UpdaterLogger.getLogger().error("The download queue has been stopped because the download for " + updatable + " took longer than " + downloadTimeLimit +
                        " seconds. Increase the download-time-limit in the config if you have a slow internet connection.");
            }
        }, true, downloadTimeLimit, TimeUnit.SECONDS);
    }
}

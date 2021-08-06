package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.util.WebUtils;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DownloadManager {

    private final List<Updatable> queue = new LinkedList<>();
    private final Path outputDirectory;

    private boolean isDownloading = false;

    public DownloadManager(@Nonnull Path outputDirectory) {
        this.outputDirectory = Objects.requireNonNull(outputDirectory);
    }

    public void queue(Updatable updatable) {
        queue.add(updatable);
        check();
    }

    private void download(Updatable updatable) {
        isDownloading = true;
        WebUtils.downloadFile(updatable.downloadUrl, outputDirectory.resolve(updatable.outputFileName));
        queue.remove(updatable);
        isDownloading = false;
        check();
    }

    private void check() {
        if (!queue.isEmpty() && !isDownloading) {
            download(queue.get(0));
        }
    }
}

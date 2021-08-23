package dev.projectg.geyserupdater.common.update;

public enum UpdateStatus {
    /**
     * The Updatable has not been checked, or the last time it was checked it was not outdated. Another check may find that it is outdated.
     */
    UNKNOWN,
    /**
     * The Updatable is outdated but is not being downloaded.
     */
    OUTDATED,
    /**
     * The Updatable is outdated and is being downloaded.
     */
    DOWNLOADING,
    /**
     * The Updatable is outdated but it has been downloaded.
     */
    DOWNLOADED
}

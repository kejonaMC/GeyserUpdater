package com.projectg.geyserupdater.common.update;

public enum Status {
    /**
     * The Updatable has not been checked, or the last time it was checked it was not outdated. Another check may find that it is outdated.
     */
    CURRENT,
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
    DOWNLOADED;
}

package com.projectg.geyserupdater.common.util;

public class Constants {
    public static final String GEYSER_BASE_URL = "https://download.geysermc.org";
    public static final String GEYSER_LATEST_MASTER_ENDPOINT = "/v2/projects/geyser/versions/latest/builds/latest";
    public static final String GEYSER_DOWNLOAD_LINK = "/v2/projects/geyser/versions/latest/builds/latest/downloads/";
    public static final String CHECK_START = "Checking for updates to Geyser...";
    public static final String LATEST = "You are using the latest build of Geyser!";
    public static final String OUTDATED = "A newer build of Geyser is available! Attempting to download the latest build now...";
    public static final String FAIL_CHECK = "Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.";
}

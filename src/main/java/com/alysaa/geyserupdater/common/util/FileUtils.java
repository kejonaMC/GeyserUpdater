package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    /**
     * Epoch time at which the last direct build file check occurred. Returns a value of 0 if the check file method has never been called.
     */
    private static long callTime = 0;

    /**
     * Returns a cached result of the check file method. Returns null if the method has never been called.
     */
    private static boolean cachedResult;

    public static boolean checkFile(String path, boolean allowCached) {
        if (allowCached) {
            long elapsedTime = System.currentTimeMillis() - callTime;
            if (elapsedTime < 30 * 60 * 1000) {
                return cachedResult;
            }
        }
        callTime = System.currentTimeMillis();
        Path p = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        boolean exists = Files.exists(p);
        if (exists) {
            BungeeUpdater.plugin.getLogger().info("New Geyser build has been downloaded! BungeeCord restart is required!");
            cachedResult = true;
            return true;
        }
        cachedResult = false;
        return false;
    }
}


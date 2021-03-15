package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.velocity.VelocityUpdater;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildFileChecker {

    // Epoch time at which the last direct build file check occurred.
    // Set to 0 first so a direct check always occurs first.
    private static long callTime = 0;

    private static boolean cachedResult;

    public static boolean checkVelocityFile(boolean forPlayer) {
        if (forPlayer) {
            long elapsedTime = System.currentTimeMillis() - callTime;
            if (elapsedTime < 30 * 60 * 1000) {
                return cachedResult;
            }
        }
        callTime = System.currentTimeMillis();
        Path p = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar");
        boolean exists = Files.exists(p);
        if (exists) {
            VelocityUpdater.logger.warn("New Geyser build has been downloaded! Velocity restart is required!");
            cachedResult = true;
            return true;
        }
        cachedResult = false;
        return false;
    }

}

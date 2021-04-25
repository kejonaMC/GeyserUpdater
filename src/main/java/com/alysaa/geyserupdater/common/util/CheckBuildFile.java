package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckBuildFile {


    // Epoch time at which the last direct build file check occurred.
    // Set to 0 first so a direct check always occurs first.
    private static long callTime = 0;

    private static boolean cachedResult;

    public static boolean checkBungeeFile(boolean forPlayer) {
        if (forPlayer) {
            long elapsedTime = System.currentTimeMillis() - callTime;
            if (elapsedTime < 30 * 60 * 1000) {
                return cachedResult;
            }
        }
        callTime = System.currentTimeMillis();
        Path p = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        boolean exists = Files.exists(p);
        if (exists) {
            BungeeUpdater.plugin.getLogger().info("A new Geyser build has been downloaded! Please restart BungeeCord in order to use the updated build!");
            cachedResult = true;
            return true;
        }
        cachedResult = false;
        return false;
    }
    public static boolean checkSpigotFile(boolean forPlayer) {
        if (forPlayer) {
            long elapsedTime = System.currentTimeMillis() - callTime;
            if (elapsedTime < 30 * 60 * 1000) {
                return cachedResult;
            }
        }
        callTime = System.currentTimeMillis();
        Path p = Paths.get("plugins/update/Geyser-Spigot.jar");
        boolean exists = Files.exists(p);
        if (exists) {
            SpigotUpdater.plugin.getLogger().info("A new Geyser build has been downloaded! Please restart the server in order to use the updated build!");
            cachedResult = true;
            return true;
        }
        cachedResult = false;
        return false;
    }
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
            VelocityUpdater.logger.warn("A new Geyser build has been downloaded! Please restart Velocity in order to use the updated build!");
            cachedResult = true;
            return true;
        }
        cachedResult = false;
        return false;
    }
}


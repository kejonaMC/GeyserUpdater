package com.geyserupdater.common.Util;

import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckBuildFile extends BukkitRunnable {
    @Override
    public void run() {
        // Check if updated build is present
        Path p = Paths.get("plugins/update/Geyser-Spigot.jar");
        boolean exists = Files.exists(p);
        boolean notExists = Files.notExists(p);

        if (exists) {
            System.out.println("[GeyserUpdater] New update is available! Server needs to be restarted before the updated build loads!");
        } else if (notExists) {
            System.out.println("[GeyserUpdater] No new update is available in the update folder! ");
        } else {
            System.out.println("[GeyserUpdater] Oops something went wrong in the Build folder in the Geyser-Updater!");
        }
    }
}
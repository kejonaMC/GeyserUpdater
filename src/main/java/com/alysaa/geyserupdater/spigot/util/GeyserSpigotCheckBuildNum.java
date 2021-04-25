package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GeyserSpigotCheckBuildNum {
	public static void checkBuildNumberSpigot() {
        // Compare build numbers.
        if (CheckBuildNum.getCurrentGeyserBuildNumber() >= CheckBuildNum.getLatestGeyserBuildNumber()) {
            SpigotUpdater.plugin.getLogger().info("Geyser is on the latest build!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("gupdater.geyserupdate")) {
                    player.sendMessage("[GeyserUpdater] Geyser is on the latest build!");
                }
            }
        } else {
            SpigotUpdater.plugin.getLogger().info("Current running Geyser build is outdated, attempting to download latest!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("gupdater.geyserupdate")) {
                    player.sendMessage("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!");
                }
            }
            GeyserSpigotDownload.downloadGeyser();
        }
    }
}

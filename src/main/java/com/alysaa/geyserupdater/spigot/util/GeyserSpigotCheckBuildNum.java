package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GeyserSpigotCheckBuildNum {
	public static void checkBuildNumberSpigot() {
        // Compare build numbers.
        if (CheckBuildNum.getCurrentGeyserBuildNumber() >= CheckBuildNum.getLatestGeyserBuildNumber()) {
            SpigotUpdater.plugin.getLogger().info("You are using the latest build of Geyser!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("gupdater.geyserupdate")) {
                    player.sendMessage("[GeyserUpdater] You are using the latest build of Geyser!");
                }
            }
        } else {
            SpigotUpdater.plugin.getLogger().info("The current build of Geyser on this server is outdated! Attempting to download the latest build...");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("gupdater.geyserupdate")) {
                    player.sendMessage("[GeyserUpdater] The current build of Geyser on this server is outdated! Attempting to download the latest build...");
                }
            }
            GeyserSpigotDownload.downloadGeyser();
        }
    }
}

package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.logging.Logger;

public class GeyserSpigotDownloader {

    private static boolean downloadSuccess;

    /**
     * Download the most recent geyser. If enabled in the config, the server will also attempt to restart.
     *
     * @return true if the download was successful
     */
    public static boolean updateGeyser() {
        SpigotUpdater plugin = SpigotUpdater.getPlugin();
        Logger logger = plugin.getLogger();

        // Download the file
        new BukkitRunnable() {

            @Override
            public void run() {
                String fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/spigot/target/Geyser-Spigot.jar";
                String outputPath = "plugins/update/Geyser-Spigot.jar";
                try {
                    FileUtils.downloadFile(fileUrl, outputPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadSuccess = false;
                    return;
                }

                // Check if the file was downloaded successfully
                downloadSuccess = FileUtils.checkFile(outputPath, false);
            }
        }.runTaskAsynchronously(plugin);

        if (!downloadSuccess) {
            logger.info("Failed to download a newer version of Geyser!");
            return false;
        }

        // Restart the server if the option is enabled
        if (plugin.getConfig().getBoolean("Auto-Restart-Server")) {
            plugin.getLogger().info("A new version of Geyser has been downloaded, the server will restart in 10 Seconds!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Restart-Message-Players")));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.spigot().restart();
                }
            }.runTaskLater(plugin, 200);
        }
        return true;
    }
}
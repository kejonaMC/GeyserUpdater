package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class GeyserSpigotDownloader {
    private static boolean downloadSuccess;

    /**
     * Downloads the most recent Geyser build. If enabled in the config, the server will also attempt to restart.
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
                String fileUrl = null;
                try {
                    fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/" + GeyserProperties.getGeyserGitPropertiesValueForPropertyKey("git.branch") + "/lastSuccessfulBuild/artifact/bootstrap/spigot/target/Geyser-Spigot.jar";
                } catch (IOException e) {
                    logger.severe("Failed to get the current Geyser build's Git branch!");
                    e.printStackTrace();
                }
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
            logger.severe("Failed to download the latest build of Geyser!");
            return false;
        }

        // Restart the server if the option is enabled
        if (plugin.getConfig().getBoolean("Auto-Restart-Server")) {
            plugin.getLogger().info("A new version of Geyser has been downloaded. The server will be restarting in 10 seconds!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Restart-Message-Players")));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Object spigotServer = null;
                        try {
                            spigotServer = SpigotUpdater.getPlugin().getServer().getClass().getMethod("spigot").invoke(SpigotUpdater.getPlugin().getServer());
                        } catch (NoSuchMethodException e) {
                            SpigotUpdater.getPlugin().getLogger().severe("You are not running Spigot (or a fork of it, such as Paper)! GeyserUpdater cannot automatically restart your server!");
                            e.printStackTrace();
                            return;
                        }
                        Method restartMethod = spigotServer.getClass().getMethod("restart");
                        restartMethod.setAccessible(true);
                        restartMethod.invoke(spigotServer);
                    } catch (NoSuchMethodException e) {
                        SpigotUpdater.getPlugin().getLogger().severe("Your server version is too old to be able to be automatically restarted!");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(plugin, 200); // 200 ticks is around 10 seconds (at 20 TPS)
        }
        return true;
    }
}
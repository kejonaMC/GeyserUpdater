package com.projectg.geyserupdater.spigot.util;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.Constants;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.GeyserDownloadApi;
import com.projectg.geyserupdater.common.util.ServerPlatform;
import com.projectg.geyserupdater.spigot.SpigotUpdater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GeyserSpigotDownloader {
    private static SpigotUpdater plugin;
    private static UpdaterLogger logger;

    /**
     * Download the latest build of Geyser from Jenkins CI for the currently used branch.
     * If enabled in the config, the server will also attempt to restart.
     */
    public static void updateGeyser() {
        plugin = SpigotUpdater.getPlugin();
        logger = UpdaterLogger.getLogger();

        UpdaterLogger.getLogger().debug("Attempting to download a new build of Geyser.");

        boolean doRestart = plugin.getConfig().getBoolean("Auto-Restart-Server");

        // Start the process async
        new BukkitRunnable() {
            @Override
            public void run() {
                // Download the newest build and store the success state
                boolean downloadSuccess = downloadGeyser();
                // No additional code should be run after the following BukkitRunnable
                // Run it synchronously because it isn't thread-safe
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (downloadSuccess) {
                            String successMsg = "The latest build of Geyser has been downloaded! A restart must occur in order for changes to take effect.";
                            logger.info(successMsg);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.hasPermission("gupdater.geyserupdate")) {
                                    player.sendMessage(ChatColor.GREEN + successMsg);
                                }
                            }
                            if (doRestart) {
                                restartServer();
                            }
                        } else {
                            // fail messages are already sent to the logger in downloadGeyser()
                            String failMsg = "A error(); error occurred when download a new build of Geyser. Please check the server console for further information!";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.hasPermission("gupdater.geyserupdate")) {
                                    player.sendMessage(ChatColor.RED + failMsg);
                                }
                            }
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Internal code for downloading the latest build of Geyser from Jenkins CI for the currently used branch.
     *
     * @return true if the download was successful, false if not.
     */
    private static boolean downloadGeyser() {
        String fileUrl = Constants.GEYSER_BASE_URL + Constants.GEYSER_DOWNLOAD_LINK + ServerPlatform.SPIGOT.getUrlComponent();
        // todo: make sure we use the update folder defined in bukkit.yml (it can be changed)
        String outputPath = "plugins/update/Geyser-Spigot.jar";
        try {
            String expectedHash = new GeyserDownloadApi().data().downloads().spigot().sha256();
            FileUtils.downloadFile(fileUrl, outputPath, expectedHash);
        } catch (Exception e) {
            logger.error("Failed to download the newest build of Geyser", e);
            return false;
        }

        if (!FileUtils.checkFile(outputPath, false)) {
            logger.error("Failed to find the downloaded Geyser build!");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Attempt to restart the server
     */
    private static void restartServer() {
        long restartTimer = plugin.getConfig().getLong("Auto-Restart-Timer");
        logger.warn("The server will be restarting in %d seconds!".formatted(restartTimer));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Restart-Message-Players").formatted(restartTimer)));
        }
        // Attempt to restart the server 10 seconds after the message
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Object spigotServer;
                    try {
                        spigotServer = SpigotUpdater.getPlugin().getServer().getClass().getMethod("spigot").invoke(SpigotUpdater.getPlugin().getServer());
                    } catch (NoSuchMethodException e) {
                        logger.error("You are not running Spigot (or a fork of it, such as Paper)! GeyserUpdater cannot automatically restart your server!", e);
                        return;
                    }
                    Method restartMethod = spigotServer.getClass().getMethod("restart");
                    restartMethod.setAccessible(true);
                    restartMethod.invoke(spigotServer);
                } catch (NoSuchMethodException e) {
                    logger.error("Your server version is too old to be able to be automatically restarted!", e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("Failed to restart the server!", e);
                }
            }
        }.runTaskLater(plugin, restartTimer * 20); // 200 ticks is around 10 seconds (at 20 TPS)
    }
}
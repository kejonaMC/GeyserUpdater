package com.projectg.geyserupdater.spigot.util;

import com.projectg.geyserupdater.common.config.Configurate;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.Constants;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.spigot.SpigotUpdater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GeyserSpigotDownloader {
    private static SpigotUpdater plugin;
    private static UpdaterLogger logger;
    private static Configurate config;
    private static final String platformName = "spigot";

    /**
     * Download the latest build of Geyser from Jenkins CI for the currently used branch.
     * If enabled in the config, the server will also attempt to restart.
     */
    public static void updateGeyser() {
        plugin = SpigotUpdater.getPlugin();
        logger = UpdaterLogger.getLogger();

        UpdaterLogger.getLogger().debug("Attempting to download a new build of Geyser.");

        boolean doRestart = config.getAutoRestartServer();

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
        String fileUrl = Constants.GEYSER_BASE_URL + Constants.GEYSER_DOWNLOAD_LINK + platformName;
        // todo: make sure we use the update folder defined in bukkit.yml (it can be changed)
        String outputPath = "plugins/update/Geyser-Spigot.jar";
        try {
            FileUtils.downloadFile(fileUrl, outputPath, platformName);
        } catch (IOException e) {
            logger.error("Failed to download the newest build of Geyser" + e.getMessage());
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
        int restartTimer = config.getRestartTimer();

        // Calculate the delay for the restart after all warnings
        int restartDelay = restartTimer * 20; // Convert restartTimer to ticks (assuming 20 TPS)

        // Send Warning-Low message every minute
        int warningLowInterval = 60;
        for (int i = restartTimer; i > warningLowInterval; i -= warningLowInterval) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    String warningLowMessage = ChatColor.translateAlternateColorCodes('&', config.getRestartMessagePlayers().getWarningLow());
                    logger.warn(warningLowMessage);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(warningLowMessage);
                    }
                }
            }.runTaskLater(plugin, (restartTimer - i) * 20L);
        }

        // Send Warning-Middle message during the last minute
        int warningMiddleInterval = 60;
        int warningMiddleTimer = restartTimer - warningMiddleInterval;
        new BukkitRunnable() {
            @Override
            public void run() {
                String warningMiddleMessage = ChatColor.translateAlternateColorCodes('&', config.getRestartMessagePlayers().getWarningMiddle());
                logger.warn(warningMiddleMessage);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(warningMiddleMessage);
                }
            }
        }.runTaskLater(plugin, warningMiddleTimer * 20L);

        // Send Warning-High message during the last 10 seconds
        int warningHighTimer = restartTimer - 10;
        new BukkitRunnable() {
            @Override
            public void run() {
                String warningHighMessage = ChatColor.translateAlternateColorCodes('&', config.getRestartMessagePlayers().getWarningHigh());
                logger.warn(warningHighMessage);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(warningHighMessage);
                }
            }
        }.runTaskLater(plugin, warningHighTimer * 20L);

        // Attempt to restart the server after the specified restart timer
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Object spigotServer = SpigotUpdater.getPlugin().getServer().getClass().getMethod("spigot").invoke(SpigotUpdater.getPlugin().getServer());
                    Method restartMethod = spigotServer.getClass().getMethod("restart");
                    restartMethod.setAccessible(true);
                    restartMethod.invoke(spigotServer);
                } catch (NoSuchMethodException e) {
                    logger.error("Your server version is too old to be able to be automatically restarted!");
                    e.printStackTrace();
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("Failed to restart the server!");
                    e.printStackTrace();
                }
            }
        }.runTaskLater(plugin, restartDelay);
    }
}
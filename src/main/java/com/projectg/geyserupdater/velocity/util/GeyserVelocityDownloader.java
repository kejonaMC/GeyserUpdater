package com.projectg.geyserupdater.velocity.util;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.velocity.VelocityUpdater;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeyserVelocityDownloader {
    private static VelocityUpdater plugin;
    private static ProxyServer server;
    private static UpdaterLogger logger;

    /**
     * Download the latest build of Geyser from Jenkins CI for the currently used branch.
     * If enabled in the config, the server will also attempt to restart.
     */
    public static void updateGeyser() {
        plugin = VelocityUpdater.getPlugin();
        server = plugin.getProxyServer();
        logger = UpdaterLogger.getLogger();

        UpdaterLogger.getLogger().debug("Attempting to download a new build of Geyser.");

        // New task so that we don't block the main thread. All new tasks on velocity are async.
        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            // Download the newest geyser build
            // todo: do the colour codes for the Adventure text formatting work?
            if (downloadGeyser()) {
                String successMsg = "The latest build of Geyser has been downloaded! A restart must occur in order for changes to take effect.";
                logger.info(successMsg);
                for (Player player : server.getAllPlayers()) {
                    if (player.hasPermission("gupdater.geyserupdate")) {
                        player.sendMessage(Component.text(successMsg).color(TextColor.fromHexString("55FF55")));
                    }
                }
                if (plugin.getConfig().isRestartServer()) {
                    restartServer();
                }
            } else {
                // fail messages are already sent to the logger in downloadGeyser()
                String failMsg = "A severe error occurred when download a new build of Geyser. Please check the server console for further information!";
                for (Player player : server.getAllPlayers()) {
                    if (player.hasPermission("gupdater.geyserupdate")) {
                        player.sendMessage(Component.text(failMsg).color(TextColor.fromHexString("AA0000")));
                    }
                }
            }
        }).schedule();
    }

    /**
     * Internal code for downloading the latest build of Geyser from Jenkins CI for the currently used branch.
     *
     * @return true if the download was successful, false if not.
     */
    private static boolean downloadGeyser() {
        String fileUrl;
        try {
            fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/" + GeyserProperties.getGeyserGitPropertiesValue("git.branch") + "/lastSuccessfulBuild/artifact/bootstrap/velocity/target/Geyser-Velocity.jar";
        } catch (IOException e) {
            logger.error("Failed to get the current Geyser branch when attempting to download a new build of Geyser!");
            e.printStackTrace();
            return false;
        }
        String outputPath = "plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar";
        try {
            FileUtils.downloadFile(fileUrl, outputPath);
        } catch (IOException e) {
            logger.error("Failed to download the newest build of Geyser");
            e.printStackTrace();
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
        logger.warn("The server will be restarting in 10 seconds!");
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(Component.text(plugin.getConfig().getRestartMessage()));
        }
        server.getScheduler()
                .buildTask(plugin, server::shutdown)
                .delay(10L, TimeUnit.SECONDS)
                .schedule();
    }
}


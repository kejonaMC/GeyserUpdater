package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeyserVelocityDownloader {

    private static boolean downloadSuccess;

    /**
     * Downloads the most recent Geyser build. If enabled in the config, the server will also attempt to restart.
     *
     * @return true if the download was successful
     */
    public static boolean updateGeyser() {
        VelocityUpdater plugin = VelocityUpdater.getPlugin();
        ProxyServer server = plugin.getProxyServer();
        Logger logger = plugin.getLogger();

        // Download the file
        server.getScheduler().buildTask(plugin, () -> {
            String fileUrl = null;
            try {
                fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/" + GeyserProperties.getGeyserGitPropertiesValueForPropertyKey("git.branch") + "/lastSuccessfulBuild/artifact/bootstrap/velocity/target/Geyser-Velocity.jar";
            } catch (IOException e) {
                logger.error("Failed to get the current Geyser build's Git branch!");
                e.printStackTrace();
            }
            String outputPath = "plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar";
            try {
                FileUtils.downloadFile(fileUrl, outputPath);
            } catch (IOException e) {
                e.printStackTrace();
                downloadSuccess = false;
                return;
            }

            // Check if the file was downloaded successfully
            downloadSuccess = FileUtils.checkFile(outputPath, false);
        }).schedule();

        if (!downloadSuccess) {
            logger.error("Failed to download the latest build of Geyser!");
            return false;
        }

        // Restart the server if the option is enabled
        if (plugin.getConfig().getBoolean("Auto-Restart-Server")) {
            logger.info("A new version of Geyser has been downloaded. The server will be restarting in 10 seconds!");
            for (Player player : server.getAllPlayers()) {
                player.sendMessage(Component.text(plugin.getConfig().getString("Restart-Message-Players")));
            }
            server.getScheduler()
                    .buildTask(plugin, server::shutdown)
                    .delay(10L, TimeUnit.SECONDS)
                    .schedule();
        }
        return true;
    }
}


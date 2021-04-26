package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.common.util.FileUtils;
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
     * Download the most recent geyser. If enabled in the config, the server will also attempt to restart.
     *
     * @return true if the download was successful
     */
    public static boolean updateGeyser() {
        VelocityUpdater plugin = VelocityUpdater.getPlugin();
        ProxyServer server = plugin.getProxyServer();
        Logger logger = plugin.getLogger();

        // Download the file
        server.getScheduler()
                .buildTask(plugin, () -> {
                    String fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/velocity/target/Geyser-Velocity.jar";
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
                })
                .schedule();

        if (!downloadSuccess) {
            logger.info("Failed to download a newer version of Geyser!");
            return false;
        }

        // Restart the server if the option is enabled
        if (plugin.getConfig().getBoolean("Auto-Restart-Server")) {
            logger.warn("A new version of Geyser has been downloaded, the server will restart in 10 Seconds!");
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


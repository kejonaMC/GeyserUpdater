package com.alysaa.geyserupdater.bungee.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.common.util.FileUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GeyserBungeeDownload {


    private static boolean downloadSuccess;

    /**
     * Download the most recent geyser. If enabled in the config, the server will also attempt to restart.
     *
     * @return true if the download was successful
     */
    public static boolean updateGeyser() {
        BungeeUpdater plugin = BungeeUpdater.getPlugin();
        Logger logger = plugin.getLogger();

        // Download the file
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            String fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/bungeecord/target/Geyser-BungeeCord.jar";
            String outputPath = "plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar";
            try {
                FileUtils.downloadFile(fileUrl, outputPath);
            } catch (IOException e) {
                e.printStackTrace();
                downloadSuccess = false;
                return;
            }

            // Check if the file was downloaded successfully
            downloadSuccess = FileUtils.checkFile(outputPath, false);
        });

        if (!downloadSuccess) {
            logger.info("Failed to download a newer version of Geyser!");
            return false;
        }

        // Restart the server if the option is enabled
        if (plugin.getConfiguration().getBoolean("Auto-Restart-Server")) {
            plugin.getLogger().info("A new version of Geyser has been downloaded, the server will restart in 10 Seconds!");
            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getConfiguration().getString("Restart-Message-Players"))));
            }
            plugin.getProxy().getScheduler().schedule(plugin, () -> plugin.getProxy().stop(), 10L, TimeUnit.SECONDS);
        }
        return true;
    }
}
package com.alysaa.geyserupdater.bungee.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.common.util.CheckBuildNum;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GeyserBungeeCheckBuildNum {
	public static void checkBuildNumberBungee() {
        // Compare build numbers.
        if (CheckBuildNum.getCurrentGeyserBuildNumber() >= CheckBuildNum.getLatestGeyserBuildNumber()) {
            BungeeUpdater.plugin.getLogger().info("[GeyserUpdater] You are using the latest build of Geyser!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent("[GeyserUpdater] You are using the latest build of Geyser!"));
                }
            }
        } else {
            BungeeUpdater.plugin.getLogger().info("[GeyserUpdater] The current build of Geyser on this server is outdated! Attempting to download the latest build...");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent("[GeyserUpdater] The current build of Geyser on this server is outdated! Attempting to download the latest build..."));
                }
            }
            GeyserBungeeDownload.downloadGeyser();
        }
    }
}

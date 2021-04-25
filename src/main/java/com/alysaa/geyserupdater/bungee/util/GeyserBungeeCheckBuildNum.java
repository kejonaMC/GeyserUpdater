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
            BungeeUpdater.plugin.getLogger().info("[GeyserUpdater] Geyser is on the latest build!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent("[GeyserUpdater] Geyser is on the latest build!"));
                }
            }
        } else {
            BungeeUpdater.plugin.getLogger().info("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!"));
                }
            }
            GeyserBungeeDownload.downloadGeyser();
        }
    }
}

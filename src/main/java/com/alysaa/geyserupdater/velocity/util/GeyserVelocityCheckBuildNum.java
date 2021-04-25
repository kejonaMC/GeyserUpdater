package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;

import net.kyori.adventure.text.Component;

public class GeyserVelocityCheckBuildNum {
	public static void checkBuildNumberVelocity() {
        // Compare build numbers.
        if (CheckBuildNum.getCurrentGeyserBuildNumber() >= CheckBuildNum.getLatestGeyserBuildNumber()) {
            VelocityUpdater.logger.warn("Geyser is on the latest build!");
            for (com.velocitypowered.api.proxy.Player all : VelocityUpdater.server.getAllPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(Component.text("[GeyserUpdater] Geyser is on the latest build!"));
                }
            }
        } else {
            VelocityUpdater.logger.warn("Current running Geyser build is outdated, attempting to download latest!");
            for (com.velocitypowered.api.proxy.Player all : VelocityUpdater.server.getAllPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(Component.text("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!"));
                }
            }
            GeyserVelocityDownload.downloadGeyser();
        }
    }
}

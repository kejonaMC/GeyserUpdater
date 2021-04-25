package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;

import net.kyori.adventure.text.Component;

public class GeyserVelocityCheckBuildNum {
	public static void checkBuildNumberVelocity() {
        // Compare build numbers.
        if (CheckBuildNum.getCurrentGeyserBuildNumber() >= CheckBuildNum.getLatestGeyserBuildNumber()) {
            VelocityUpdater.logger.warn("You are using the latest build of Geyser!");
            for (com.velocitypowered.api.proxy.Player all : VelocityUpdater.server.getAllPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(Component.text("[GeyserUpdater] You are using the latest build of Geyser!"));
                }
            }
        } else {
            VelocityUpdater.logger.warn("The current build of Geyser on this server is outdated! Attempting to download the latest build...");
            for (com.velocitypowered.api.proxy.Player all : VelocityUpdater.server.getAllPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(Component.text("[GeyserUpdater] The current build of Geyser on this server is outdated! Attempting to download the latest build..."));
                }
            }
            GeyserVelocityDownload.downloadGeyser();
        }
    }
}

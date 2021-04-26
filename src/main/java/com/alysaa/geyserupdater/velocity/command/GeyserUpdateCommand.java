package com.alysaa.geyserupdater.velocity.command;

import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;
import com.alysaa.geyserupdater.velocity.util.GeyserVelocityDownloader;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;

import net.kyori.adventure.text.Component;

import org.slf4j.Logger;

import java.io.IOException;

public class GeyserUpdateCommand implements RawCommand {
    // TODO make sure command blocks can't run this command
    @Override
    public void execute(final Invocation invocation) {

        String checkMsg = "Checking for updates to Geyser...";
        String latestMsg = "You are using the latest build of Geyser!";
        String outdatedMsg = "A newer build of Geyser is available! Attempting to download the latest build now...";
        String failUpdateCheckMsg = "Failed to check for updates to Geyser!";
        String failDownloadMsg = "Failed to download the latest build of Geyser!";

        CommandSource source = invocation.source();
        Logger logger = VelocityUpdater.getPlugin().getLogger();

        try {
            source.sendMessage(Component.text(checkMsg));
            boolean isLatest = GeyserProperties.isLatestBuild();
            if (isLatest) {
                source.sendMessage(Component.text(latestMsg));
            } else {
                source.sendMessage(Component.text(outdatedMsg));
                if (!GeyserVelocityDownloader.updateGeyser()) {
                    // TODO: This currently sends a double message
                    source.sendMessage(Component.text(failDownloadMsg));
                }
            }
        } catch (IOException e) {
            source.sendMessage(Component.text(failUpdateCheckMsg));
            logger.error(failUpdateCheckMsg);
            e.printStackTrace();
        }
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("gupdater.geyserupdate");
    }
}


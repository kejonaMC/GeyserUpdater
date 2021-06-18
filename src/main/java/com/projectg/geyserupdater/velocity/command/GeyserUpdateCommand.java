package com.projectg.geyserupdater.velocity.command;

import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.velocity.util.GeyserVelocityDownloader;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;

import net.kyori.adventure.text.Component;

import java.io.IOException;

public class GeyserUpdateCommand implements RawCommand {

    @Override
    public void execute(final Invocation invocation) {

        String checkMsg = "Checking for updates to Geyser...";
        String latestMsg = "You are using the latest build of Geyser!";
        String outdatedMsg = "A newer build of Geyser is available! Attempting to download the latest build now...";
        String failUpdateCheckMsg = "Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.";

        CommandSource source = invocation.source();

        try {
            source.sendMessage(Component.text(checkMsg));
            boolean isLatest = GeyserProperties.isLatestBuild();
            if (isLatest) {
                source.sendMessage(Component.text(latestMsg));
            } else {
                source.sendMessage(Component.text(outdatedMsg));
                GeyserVelocityDownloader.updateGeyser();
            }
        } catch (IOException e) {
            source.sendMessage(Component.text(failUpdateCheckMsg));
            e.printStackTrace();
        }
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("gupdater.geyserupdate");
    }
}


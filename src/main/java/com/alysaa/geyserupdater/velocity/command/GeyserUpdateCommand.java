package com.alysaa.geyserupdater.velocity.command;

import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;
import com.alysaa.geyserupdater.velocity.util.GeyserVeloDownloader;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.IOException;

public class GeyserUpdateCommand implements RawCommand {
    // TODO make sure command blocks can't run this command
    @Override
    public void execute(final Invocation invocation) {

        String checkMsg = "Checking current Geyser version!";
        String latestMsg = "Geyser is on the latest build!";
        String outdatedMsg = "A newer version of Geyser is available. Downloading now...";
        String failMsg = "Failed to check if Geyser is outdated!";

        CommandSource source = invocation.source();
        Logger logger = VelocityUpdater.logger;

        try {
            source.sendMessage(Component.text(checkMsg));
            boolean isLatest = GeyserProperties.isLatestBuild();
            if (isLatest) {
                source.sendMessage(Component.text(latestMsg));
            } else {
                source.sendMessage(Component.text(outdatedMsg));
                if (!GeyserVeloDownloader.updateGeyser()) {
                    // todo this currently sends a double message
                    source.sendMessage(Component.text("Failed to download a newer version of Geyser!"));
                }
            }
        } catch (IOException e) {
            source.sendMessage(Component.text(failMsg));
            logger.error(failMsg);
            e.printStackTrace();
        }
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("gupdater.geyserupdate");
    }
}


package com.projectg.geyserupdater.velocity.command;

import com.projectg.geyserupdater.common.util.Constants;
import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.velocity.util.GeyserVelocityDownloader;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class GeyserUpdateCommand implements RawCommand {

    @Override
    public void execute(final @NotNull Invocation invocation) {
        CommandSource source = invocation.source();

        try {
            source.sendMessage(Component.text(Constants.CHECK_START));
            boolean isLatest = GeyserProperties.isLatestBuild();
            if (isLatest) {
                source.sendMessage(Component.text(Constants.LATEST));
            } else {
                source.sendMessage(Component.text(Constants.OUTDATED));
                GeyserVelocityDownloader.updateGeyser();
            }
        } catch (Exception e) {
            source.sendMessage(Component.text(Constants.FAIL_CHECK));
            e.printStackTrace();
        }
    }
    @Override
    public boolean hasPermission(final @NotNull Invocation invocation) {
        return invocation.source().hasPermission("gupdater.geyserupdate");
    }
}

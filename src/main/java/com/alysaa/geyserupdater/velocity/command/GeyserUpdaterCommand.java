package com.alysaa.geyserupdater.velocity.command;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class GeyserUpdaterCommand implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        invocation.source().sendMessage(Component.text("[GeyserUpdater] Checking current Geyser version!"));
        try {
            CheckBuildNum.checkBuildNumberVelocity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("gupdater.geyserupdate");
    }
}


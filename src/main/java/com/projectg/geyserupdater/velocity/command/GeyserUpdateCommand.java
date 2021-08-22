package com.projectg.geyserupdater.velocity.command;

import com.velocitypowered.api.command.RawCommand;

public class GeyserUpdateCommand implements RawCommand {

    @Override
    public void execute(final Invocation invocation) {

        //todo: velocity command

        /*
        CommandSource source = invocation.source();

        try {
            source.sendMessage(Component.text(Messages.Command.CHECK_START));
            boolean isLatest = GeyserProperties.isLatestBuild();
            if (isLatest) {
                source.sendMessage(Component.text(Messages.Command.LATEST));
            } else {
                source.sendMessage(Component.text(Messages.Command.OUTDATED));
                GeyserVelocityDownloader.updateGeyser();
            }
        } catch (IOException e) {
            source.sendMessage(Component.text(Messages.Command.FAIL_CHECK));
            e.printStackTrace();
        }
         */
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("gupdater.geyserupdate");
    }
}


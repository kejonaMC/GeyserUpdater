package com.alysaa.geyserupdater.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.sql.rowset.spi.SyncFactoryException;

public class GeyserCommand extends Command {
    public GeyserCommand() {
        super("geyserupdate","gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            try {
                AutoUpdateGeyser.checkUpdate(player);
            } catch (SyncFactoryException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
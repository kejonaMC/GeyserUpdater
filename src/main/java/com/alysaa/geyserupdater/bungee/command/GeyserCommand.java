package com.alysaa.geyserupdater.bungee.command;

import com.alysaa.geyserupdater.bungee.util.AutoUpdateGeyser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GeyserCommand extends Command {
    public GeyserCommand() {
        super("geyserupdate","gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            AutoUpdateGeyser.checkUpdate(player);
        }
    }
}
package com.alysaa.geyserupdater.bungee.command;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeCheckBuildNum;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class GeyserCommand extends Command {

    public GeyserCommand() {
        super("geyserupdate", "gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            player.sendMessage(new TextComponent(ChatColor.WHITE + "[GeyserUpdater] Checking for updates to Geyser..."));
            GeyserBungeeCheckBuildNum.checkBuildNumberBungee();
        } else {
            BungeeUpdater.plugin.getLogger().info("Checking for updates to Geyser...");
            GeyserBungeeCheckBuildNum.checkBuildNumberBungee();
        }
    }
}
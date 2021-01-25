package com.alysaa.geyserupdater.bungee.command;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;


public class GeyserCommand extends Command {
    public GeyserCommand() {
        super("geyserupdate", "gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            try {
                player.sendMessage(new TextComponent(ChatColor.WHITE + "[GeyserUpdater] Checking current Geyser version"));
                CheckBuildNum.CheckBuildNumberBungee();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
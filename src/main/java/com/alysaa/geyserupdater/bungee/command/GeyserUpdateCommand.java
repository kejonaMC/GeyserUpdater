package com.alysaa.geyserupdater.bungee.command;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownloader;
import com.alysaa.geyserupdater.common.util.GeyserProperties;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.logging.Logger;


public class GeyserUpdateCommand extends Command {

    public GeyserUpdateCommand() {
        super("geyserupdate", "gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {

        String checkMsg = "Checking for updates to Geyser...";
        String latestMsg = "You are using the latest build of Geyser!";
        String outdatedMsg = "A newer build of Geyser is available! Attempting to download the latest build now...";
        String failUpdateCheckMsg = "Failed to check for updates to Geyser!";

        Logger logger = BungeeUpdater.getPlugin().getLogger();

        // TODO: compress this. near duplication seems unnecessary just to send to different commandSenders.

        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            try {
                player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + checkMsg));
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + latestMsg));
                } else {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + outdatedMsg));
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (IOException e) {
                player.sendMessage(new TextComponent(ChatColor.RED + "[GeyserUpdater] " + failUpdateCheckMsg));
                e.printStackTrace();
            }
        } else {
            // TODO: filter this against command blocks
            try {
                logger.info(checkMsg);
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(latestMsg);
                } else {
                    logger.info(outdatedMsg);
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (IOException e) {
                logger.severe(failUpdateCheckMsg);
                e.printStackTrace();
            }
        }
    }
}
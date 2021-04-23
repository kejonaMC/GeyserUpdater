package com.alysaa.geyserupdater.bungee.command;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownload;
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

        String checkMsg = "Checking current Geyser version!";
        String latestMsg = "Geyser is on the latest build!";
        String outdatedMsg = "A newer version of Geyser is available. Downloading now...";
        String failMsg = "Failed to check if Geyser is outdated!";

        Logger logger = BungeeUpdater.plugin.getLogger();

        // TODO compress this. near duplication seems unnecessary just to send to different commandSenders.

        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            try {
                player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + checkMsg));
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + latestMsg));
                } else {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + outdatedMsg));
                    if (!GeyserBungeeDownload.updateGeyser()) {
                        player.sendMessage(new TextComponent(ChatColor.RED + "[GeyserUpdater] Failed to download a newer version of Geyser!"));
                    }
                }
            } catch (IOException e) {
                player.sendMessage(new TextComponent(ChatColor.RED + "[GeyserUpdater] " + failMsg));
                e.printStackTrace();
            }
        } else {
            // TODO filter this against command blocks
            try {
                logger.info(checkMsg);
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(latestMsg);
                } else {
                    logger.info(outdatedMsg);
                    GeyserBungeeDownload.updateGeyser();
                }
            } catch (IOException e) {
                logger.severe(failMsg);
                e.printStackTrace();
            }
        }
    }
}
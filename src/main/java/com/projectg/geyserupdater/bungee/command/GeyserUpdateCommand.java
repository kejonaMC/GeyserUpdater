package com.projectg.geyserupdater.bungee.command;

import com.projectg.geyserupdater.bungee.util.GeyserBungeeDownloader;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.GeyserProperties;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;


public class GeyserUpdateCommand extends Command {

    public GeyserUpdateCommand() {
        super("geyserupdate", "gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {

        String checkMsg = "Checking for updates to Geyser...";
        String latestMsg = "You are using the latest build of Geyser!";
        String outdatedMsg = "A newer build of Geyser is available! Attempting to download the latest build now...";
        String failUpdateCheckMsg = "Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.";

        UpdaterLogger logger = UpdaterLogger.getLogger();

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
                logger.error(failUpdateCheckMsg);
                e.printStackTrace();
            }
        }
    }
}
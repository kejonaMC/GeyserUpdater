package com.alysaa.geyserupdater.spigot.command;

import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownload;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Logger;

public class GeyserUpdateCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String checkMsg = "Checking current Geyser version!";
        String latestMsg = "Geyser is on the latest build!";
        String outdatedMsg = "A newer version of Geyser is available. Downloading now...";
        String failMsg = "Failed to check if Geyser is outdated!";

        Logger logger = SpigotUpdater.plugin.getLogger();

        // I guess this could be thrown into a different method
        // But we wouldn't have as much control over the messages


        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                try {
                    sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + checkMsg);
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (isLatest) {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + latestMsg);
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + outdatedMsg);
                        GeyserSpigotDownload.downloadGeyser();
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "[GeyserUpdater] " + failMsg);
                    logger.severe(failMsg);
                    e.printStackTrace();
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            try {
                logger.info(checkMsg);
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(latestMsg);
                } else {
                    logger.info(outdatedMsg);
                    GeyserSpigotDownload.downloadGeyser();
                }
            } catch (IOException e) {
                logger.severe(failMsg);
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return true;
    }
}
package com.projectg.geyserupdater.spigot.command;

import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.spigot.SpigotUpdater;
import com.projectg.geyserupdater.spigot.util.GeyserSpigotDownloader;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Logger;

public class GeyserUpdateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        String checkMsg = "Checking for updates to Geyser...";
        String latestMsg = "You are using the latest build of Geyser!";
        String outdatedMsg = "A newer build of Geyser is available! Attempting to download the latest build now...";
        String failUpdateCheckMsg = "Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.";

        Logger logger = SpigotUpdater.getPlugin().getLogger();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + checkMsg);
                try {
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (isLatest) {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + latestMsg);
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + outdatedMsg);
                        GeyserSpigotDownloader.updateGeyser();
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "[GeyserUpdater] " + failUpdateCheckMsg);
                    logger.severe(failUpdateCheckMsg);
                    e.printStackTrace();
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            logger.info(checkMsg);
            try {
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(latestMsg);
                } else {
                    logger.info(outdatedMsg);
                    GeyserSpigotDownloader.updateGeyser();
                }
            } catch (IOException e) {
                logger.severe(failUpdateCheckMsg);
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return true;
    }
}
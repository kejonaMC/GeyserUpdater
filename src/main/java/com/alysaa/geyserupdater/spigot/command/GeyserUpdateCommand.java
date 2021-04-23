package com.alysaa.geyserupdater.spigot.command;

import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownloader;
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

        String checkMsg = "Checking current Geyser version!";
        String latestMsg = "Geyser is on the latest build!";
        String outdatedMsg = "A newer version of Geyser is available. Downloading now...";
        String failMsg = "Failed to check if Geyser is outdated!";

        Logger logger = SpigotUpdater.getPlugin().getLogger();

        // I guess this could be thrown into a different method
        // But we wouldn't have as much control over the messages

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
                        if (!GeyserSpigotDownloader.updateGeyser()) {
                            sender.sendMessage(ChatColor.RED + "[GeyserUpdater] Failed to download the newest Geyser!");
                        }
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "[GeyserUpdater] " + failMsg);
                    logger.severe(failMsg);
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
                logger.severe(failMsg);
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return true;
    }
}
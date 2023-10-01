package com.projectg.geyserupdater.spigot.command;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.Constants;
import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.spigot.util.GeyserSpigotDownloader;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class GeyserUpdateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        UpdaterLogger logger = UpdaterLogger.getLogger();

        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + Constants.CHECK_START);
                try {
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (isLatest) {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + Constants.LATEST);
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + Constants.OUTDATED);
                        GeyserSpigotDownloader.updateGeyser();
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "[GeyserUpdater] " + Constants.FAIL_CHECK);
                    logger.error(Constants.FAIL_CHECK, e);
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            logger.info(Constants.CHECK_START);
            try {
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(Constants.LATEST);
                } else {
                    logger.info(Constants.OUTDATED);
                    GeyserSpigotDownloader.updateGeyser();
                }
            } catch (Exception e) {
                logger.error(Constants.FAIL_CHECK, e);
            }
        } else {
            return false;
        }
        return true;
    }
}
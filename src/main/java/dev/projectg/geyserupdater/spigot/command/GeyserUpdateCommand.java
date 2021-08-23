package dev.projectg.geyserupdater.spigot.command;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class GeyserUpdateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        UpdaterLogger logger = UpdaterLogger.getLogger();

        //todo: spigot command

        /*

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + Messages.Command.CHECK_START);
                try {
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (isLatest) {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + Messages.Command.LATEST);
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] " + Messages.Command.OUTDATED);
                        GeyserSpigotDownloader.updateGeyser();
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "[GeyserUpdater] " + Messages.Command.FAIL_CHECK);
                    logger.error(Messages.Command.FAIL_CHECK);
                    e.printStackTrace();
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            logger.info(Messages.Command.CHECK_START);
            try {
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(Messages.Command.LATEST);
                } else {
                    logger.info(Messages.Command.OUTDATED);
                    GeyserSpigotDownloader.updateGeyser();
                }
            } catch (IOException e) {
                logger.error(Messages.Command.FAIL_CHECK);
                e.printStackTrace();
            }
        } else {
            return false;
        }
         */
        return true;
    }
}
package com.projectg.geyserupdater.bungee.command;

import com.projectg.geyserupdater.bungee.util.GeyserBungeeDownloader;
import com.projectg.geyserupdater.common.Messages;
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
        UpdaterLogger logger = UpdaterLogger.getLogger();

        if (commandSender instanceof ProxiedPlayer player) {
            try {
                player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + Messages.Command.CHECK_START));
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + Messages.Command.LATEST));
                } else {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + Messages.Command.OUTDATED));
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (IOException e) {
                player.sendMessage(new TextComponent(ChatColor.RED + "[GeyserUpdater] " + Messages.Command.FAIL_CHECK));
                logger.error(Messages.Command.FAIL_CHECK);
                e.printStackTrace();
            }
        } else {
            // TODO: filter this against command blocks
            try {
                logger.info(Messages.Command.CHECK_START);
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(Messages.Command.LATEST);
                } else {
                    logger.info(Messages.Command.OUTDATED);
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (IOException e) {
                logger.error(Messages.Command.FAIL_CHECK);
                e.printStackTrace();
            }
        }
    }
}
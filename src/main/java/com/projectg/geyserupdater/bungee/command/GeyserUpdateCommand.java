package com.projectg.geyserupdater.bungee.command;

import com.projectg.geyserupdater.bungee.util.GeyserBungeeDownloader;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.Constants;
import com.projectg.geyserupdater.common.util.GeyserProperties;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GeyserUpdateCommand extends Command {

    public GeyserUpdateCommand() {
        super("geyserupdate", "gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {
        UpdaterLogger logger = UpdaterLogger.getLogger();

        if (commandSender instanceof ProxiedPlayer player) {
            try {
                player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + Constants.CHECK_START));
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + Constants.LATEST));
                } else {
                    player.sendMessage(new TextComponent(ChatColor.GOLD + "[GeyserUpdater] " + Constants.OUTDATED));
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (Exception e) {
                player.sendMessage(new TextComponent(ChatColor.RED + "[GeyserUpdater] " + Constants.FAIL_CHECK));
                logger.error(Constants.FAIL_CHECK, e);
            }
        } else {
            // TODO: filter this against command blocks
            try {
                logger.info(Constants.CHECK_START);
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (isLatest) {
                    logger.info(Constants.LATEST);
                } else {
                    logger.info(Constants.OUTDATED);
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (Exception e) {
                logger.error(Constants.FAIL_CHECK, e);
            }
        }
    }
}
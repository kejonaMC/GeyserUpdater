package dev.projectg.geyserupdater.bungee.command;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;


public class GeyserUpdateCommand extends Command {

    public GeyserUpdateCommand() {
        super("geyserupdate", "gupdater.geyserupdate");
    }

    public void execute(CommandSender commandSender, String[] args) {
        UpdaterLogger logger = UpdaterLogger.getLogger();

        //todo: bungee command

        /*

        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
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

         */
    }
}
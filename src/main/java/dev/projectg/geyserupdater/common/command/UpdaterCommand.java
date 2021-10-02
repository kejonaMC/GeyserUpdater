package dev.projectg.geyserupdater.common.command;

public interface UpdaterCommand {

    boolean process(CommandSender sender, String cmd, String[] args);
}

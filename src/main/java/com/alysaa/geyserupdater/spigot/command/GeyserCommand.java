package com.alysaa.geyserupdater.spigot.command;

import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotCheckBuildNum;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GeyserCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                sender.sendMessage(ChatColor.WHITE + "[GeyserUpdater] Checking current Geyser version!");
                GeyserSpigotCheckBuildNum.checkBuildNumberSpigot();
            }
        } else if (sender instanceof ConsoleCommandSender) {
            SpigotUpdater.plugin.getLogger().info("Checking current Geyser version!");
            GeyserSpigotCheckBuildNum.checkBuildNumberSpigot();
        }
        return false;
    }
}
package com.alysaa.geyserupdater.spigot.command;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class GeyserCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                try {
                    sender.sendMessage(ChatColor.WHITE + "[GeyserUpdater] Checking current Geyser version!");
                    CheckBuildNum.checkBuildNumberSpigot();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            SpigotUpdater.plugin.getLogger().info("Checking current Geyser version!");
            try {
                CheckBuildNum.checkBuildNumberSpigot();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
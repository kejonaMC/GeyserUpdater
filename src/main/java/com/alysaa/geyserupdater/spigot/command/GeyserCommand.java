package com.alysaa.geyserupdater.spigot.command;

import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;

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
        }
        return false;
    }
}
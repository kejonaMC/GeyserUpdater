package com.alysaa.geyserupdater.common.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckBuildFile {
    public static void checkBungeeFile() {
        Path p = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        boolean exists = Files.exists(p);
        boolean notExists = Files.notExists(p);
        if (exists) {
            System.out.println("[GeyserUpdater]  New Geyser build has been downloaded! BungeeCord restart is required!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) ;
                {
                    all.sendMessage("[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!");
                }
            }
        } else if (notExists) {
            System.out.println("[GeyserUpdater] There is no updated Geyser build yet in the update folder!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) ;
                {
                    all.sendMessage("[GeyserUpdater] There is no updated Geyser build yet in the update folder!");
                }
            }
        } else {
            System.out.println("[GeyserUpdater] Oops?!? something went wrong in the build update folder!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate"));
                {
                    all.sendMessage("[GeyserUpdater] Oops?!? something went wrong in the build update folder!");
                }
            }
        }
    }
    public static void CheckSpigotFile() {
        Path p = Paths.get("plugins/update/Geyser-Spigot.jar");
        boolean exists = Files.exists(p);
        boolean notExists = Files.notExists(p);
        if (exists) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp())
                    player.sendMessage("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
            }
            System.out.println("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
        } else if (notExists) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp())
                    player.sendMessage("[GeyserUpdater] There is no updated Geyser build yet in the Update folder!");
            }
            System.out.println("[GeyserUpdater] There is no updated Geyser build yet in the Update folder!");
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp())
                    player.sendMessage("[GeyserUpdater] Oops?!? something went wrong in the update folder!");
            }
            System.out.println("[GeyserUpdater] Oops?!? something went wrong in the update folder!");
        }
    }
}
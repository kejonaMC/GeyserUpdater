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
        if (exists) {
            System.out.println("[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) ;
                {
                    all.sendMessage("[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!");
                }
            }
        }
    }
    public static void CheckSpigotFile() {
        Path p = Paths.get("plugins/update/Geyser-Spigot.jar");
        boolean exists = Files.exists(p);
        if (exists) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp())
                    player.sendMessage("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
            }
            System.out.println("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
        }
    }
}


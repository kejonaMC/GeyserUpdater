package com.alysaa.geyserupdater.spigot.util;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpigotJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                event.getPlayer().sendMessage("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
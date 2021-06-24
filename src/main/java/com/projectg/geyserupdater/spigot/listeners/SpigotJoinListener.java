package com.projectg.geyserupdater.spigot.listeners;

import com.projectg.geyserupdater.common.util.FileUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpigotJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // We allow a cached result of maximum age 30 minutes to be used
        if (FileUtils.checkFile("plugins/update/Geyser-Spigot.jar", true)) {
            if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                event.getPlayer().sendMessage("[GeyserUpdater] A new Geyser build has been downloaded! Please restart the server in order to use the updated build!");
            }
        }
    }
}
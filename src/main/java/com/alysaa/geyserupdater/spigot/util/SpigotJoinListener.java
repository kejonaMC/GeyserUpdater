package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpigotJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - CheckBuildFile.callTime;
        if (elapsedTime > 30 * 60 * 1000)  {
            // If the elapsedTime is greater than 30 minutes, the build file is checked directly.
            if (CheckBuildFile.checkBungeeFile()) {
                if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                    event.getPlayer().sendMessage("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
                }
            }
        } else if (CheckBuildFile.cachedResult) {
            // The only circumstance in which there is no cachedResult is when a build check hasn't occurred et.
            // In such case, the callTime would be 0, so the elapsed time would be greater than 30 minutes.
            if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                event.getPlayer().sendMessage("[GeyserUpdater] New Geyser build has been downloaded! Server restart is required!");
            }
        }
    }
}
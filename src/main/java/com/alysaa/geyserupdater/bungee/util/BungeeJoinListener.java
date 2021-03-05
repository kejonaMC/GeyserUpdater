package com.alysaa.geyserupdater.bungee.util;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeJoinListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - CheckBuildFile.callTime;
        if (elapsedTime > 30 * 60 * 1000)  {
            // If the elapsedTime is greater than 30 minutes, the build file is checked directly.
            if (CheckBuildFile.checkBungeeFile()) {
                if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                    event.getPlayer().sendMessage(new TextComponent("[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!"));
                }
            }
        } else if (CheckBuildFile.cachedResult) {
            // The only circumstance in which there is no cachedResult is when a build check hasn't occurred et.
            // In such case, the callTime would be 0, so the elapsed time would be greater than 30 minutes.
            if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                event.getPlayer().sendMessage(new TextComponent("[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!"));
            }
        }
    }
}
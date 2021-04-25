package com.alysaa.geyserupdater.bungee.util;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeJoinListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        // We allow a cached result of maximum age 30 minutes to be used
        if (CheckBuildFile.checkBungeeFile(true)) {
            if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                event.getPlayer().sendMessage(new TextComponent("[GeyserUpdater] A new Geyser build has been downloaded! Please restart BungeeCord in order to use the updated build!"));
            }
        }
    }
}
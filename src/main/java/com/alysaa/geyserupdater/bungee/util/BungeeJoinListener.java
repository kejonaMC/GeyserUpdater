package com.alysaa.geyserupdater.bungee.util;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeJoinListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
            event.getPlayer().sendMessage(new TextComponent("[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!"));
        }
    }
}
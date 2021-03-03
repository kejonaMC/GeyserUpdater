package com.alysaa.geyserupdater.bungee.util;


import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeJoinListener extends Plugin implements Listener {
    public void onPostLogin(PostLoginEvent event) {
        try {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent(event.getPlayer() + "[GeyserUpdater] New Geyser build has been downloaded! BungeeCord restart is required!"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
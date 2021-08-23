package dev.projectg.geyserupdater.bungee;

import dev.projectg.geyserupdater.common.PlayerHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BungeePlayerHandler implements PlayerHandler {

    @Override
    public @NotNull List<UUID> getOnlinePlayers() {
        List<UUID> uuidList = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            uuidList.add(player.getUniqueId());
        }
        return uuidList;
    }

    @Override
    public void sendMessage(@NotNull UUID uuid, @NotNull String message) {
        ProxyServer.getInstance().getPlayer(uuid).sendMessage(new TextComponent(message));
    }
}

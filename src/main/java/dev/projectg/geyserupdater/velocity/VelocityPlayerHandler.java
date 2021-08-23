package dev.projectg.geyserupdater.velocity;

import dev.projectg.geyserupdater.common.PlayerHandler;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VelocityPlayerHandler implements PlayerHandler {

    private final ProxyServer proxyServer;

    public VelocityPlayerHandler(@Nonnull ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public @NotNull List<UUID> getOnlinePlayers() {
        List<UUID> uuidList = new ArrayList<>();
        for (Player player : proxyServer.getAllPlayers()) {
            uuidList.add(player.getUniqueId());
        }
        return uuidList;
    }

    @Override
    public void sendMessage(@NotNull UUID uuid, @NotNull String message) {

    }
}

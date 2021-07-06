package com.projectg.geyserupdater.spigot;

import com.projectg.geyserupdater.common.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotPlayerHandler implements PlayerHandler {

    @Override
    public @NotNull List<UUID> getOnlinePlayers() {
        List<UUID> uuidList = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            uuidList.add(player.getUniqueId());
        }
        return uuidList;
    }

    @Override
    public void sendMessage(@NotNull UUID uuid, @NotNull String message) {
        Bukkit.getServer().getPlayer(uuid).sendMessage(message);
    }
}

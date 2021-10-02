package dev.projectg.geyserupdater.common;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface PlayerManager {

    @Nonnull
    List<UUID> getOnlinePlayers();

    void sendMessage(@Nonnull UUID uuid, @NotNull String message);

}

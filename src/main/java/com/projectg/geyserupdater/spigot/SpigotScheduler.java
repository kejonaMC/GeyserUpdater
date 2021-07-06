package com.projectg.geyserupdater.spigot;

import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SpigotScheduler implements UpdaterScheduler {

    private final JavaPlugin plugin;

    public SpigotScheduler(@Nonnull JavaPlugin plugin) {
        Objects.requireNonNull(plugin);
        this.plugin = plugin;
    }

    @Override
    public void schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat) {
        Objects.requireNonNull(runnable);
        if (async) {
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay * 50, repeat * 50); // multiply by 50 for milliseconds -> ticks
        } else {
            plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay * 50, repeat * 50);
        }
    }
}

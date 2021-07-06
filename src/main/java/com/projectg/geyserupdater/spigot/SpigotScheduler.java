package com.projectg.geyserupdater.spigot;

import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SpigotScheduler implements UpdaterScheduler {

    private final JavaPlugin plugin;

    public SpigotScheduler(@Nonnull JavaPlugin plugin) {
        Objects.requireNonNull(plugin);
        this.plugin = plugin;
    }

    @Override
    public void schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat, TimeUnit unit) {
        Objects.requireNonNull(runnable);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        if (repeat <= 0) {
            if (async) {
                scheduler.runTaskLaterAsynchronously(plugin, runnable, unit.toSeconds(delay) * 20); // 20 ticks in a second
            } else {
                scheduler.runTaskLater(plugin, runnable, unit.toSeconds(delay) * 20);
            }
        } else {
            if (async) {
                scheduler.runTaskTimerAsynchronously(plugin, runnable, unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
            } else {
                scheduler.runTaskTimer(plugin, runnable, unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
            }
        }
    }
}

package com.projectg.geyserupdater.bungee;

import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BungeeScheduler implements UpdaterScheduler {

    private final Plugin plugin;

    public BungeeScheduler(@Nonnull Plugin plugin) {
        Objects.requireNonNull(plugin);
        this.plugin = plugin;
    }

    @Override
    public void schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat) {
        Objects.requireNonNull(runnable);
        plugin.getProxy().getScheduler().schedule(plugin, runnable, delay, repeat, TimeUnit.MILLISECONDS);
    }
}

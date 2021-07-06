package com.projectg.geyserupdater.velocity;

import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VelocityScheduler implements UpdaterScheduler {

    private final VelocityUpdater plugin;

    public VelocityScheduler(@Nonnull VelocityUpdater plugin) {
        Objects.requireNonNull(plugin);
        this.plugin = plugin;
    }

    @Override
    public void schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat, TimeUnit unit) {
        Objects.requireNonNull(runnable);
        this.plugin.getProxyServer().getScheduler().buildTask(plugin, runnable)
                .delay(delay, unit)
                .repeat(repeat, unit)
                .schedule();
    }
}

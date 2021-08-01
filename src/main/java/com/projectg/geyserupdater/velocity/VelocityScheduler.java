package com.projectg.geyserupdater.velocity;

import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;

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
    public void schedule(@Nonnull Runnable runnable, boolean async, long delay, long repeat, @Nonnull TimeUnit unit) {
        // https://github.com/VelocityPowered/Velocity/blob/dev/3.0.0/proxy/src/main/java/com/velocitypowered/proxy/scheduler/VelocityScheduler.java

        Objects.requireNonNull(runnable);
        Objects.requireNonNull(unit);

        this.plugin.getProxyServer().getScheduler().buildTask(plugin, runnable)
                .delay(delay, unit)
                .repeat(repeat, unit)
                .schedule();
    }
}

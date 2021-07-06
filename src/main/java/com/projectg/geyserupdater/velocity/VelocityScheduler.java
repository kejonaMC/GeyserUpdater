package com.projectg.geyserupdater.velocity;

import com.projectg.geyserupdater.common.UpdaterScheduler;
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
    public void schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat) {
        Objects.requireNonNull(runnable);
        this.plugin.getProxyServer().getScheduler().buildTask(plugin, runnable)
                .delay(delay, TimeUnit.MILLISECONDS)
                .repeat(repeat, TimeUnit.MILLISECONDS)
                .schedule();
    }
}

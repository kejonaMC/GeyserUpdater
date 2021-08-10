package com.projectg.geyserupdater.velocity;

import com.projectg.geyserupdater.common.scheduler.Task;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.velocitypowered.api.scheduler.ScheduledTask;

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
    public Task schedule(@Nonnull Runnable runnable, boolean async, long delay, long repeat, @Nonnull TimeUnit unit) {
        // https://github.com/VelocityPowered/Velocity/blob/dev/3.0.0/proxy/src/main/java/com/velocitypowered/proxy/scheduler/VelocityScheduler.java

        Objects.requireNonNull(runnable);
        Objects.requireNonNull(unit);

        return new VelocityTask(plugin.getProxyServer().getScheduler().buildTask(plugin, runnable)
                .delay(delay, unit)
                .repeat(repeat, unit)
                .schedule());
    }

    private static class VelocityTask implements Task {
        private final ScheduledTask task;

        private VelocityTask(ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}

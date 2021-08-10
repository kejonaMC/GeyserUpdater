package com.projectg.geyserupdater.bungee;

import com.projectg.geyserupdater.common.scheduler.Task;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
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
    public Task schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat, TimeUnit unit) {
        // https://github.com/SpigotMC/BungeeCord/blob/master/proxy/src/main/java/net/md_5/bungee/scheduler/BungeeTask.java

        Objects.requireNonNull(runnable);

        return new BungeeTask(plugin.getProxy().getScheduler().schedule(plugin, runnable, delay, repeat, unit));
    }

    private static class BungeeTask implements Task {
        private final ScheduledTask task;

        public BungeeTask(ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}

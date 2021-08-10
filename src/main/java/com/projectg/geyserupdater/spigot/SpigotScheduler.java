package com.projectg.geyserupdater.spigot;

import com.projectg.geyserupdater.common.scheduler.Task;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
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
    public Task schedule(@NotNull Runnable runnable, boolean async, long delay, long repeat, TimeUnit unit) {
        // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/scheduler/CraftScheduler.java
        // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/scheduler/CraftTask.java

        Objects.requireNonNull(runnable);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        BukkitTask bukkitTask;
        if (repeat <= 0) {
            if (async) {
                bukkitTask = scheduler.runTaskLaterAsynchronously(plugin, runnable, unit.toSeconds(delay) * 20); // 20 ticks in a second
            } else {
                bukkitTask = scheduler.runTaskLater(plugin, runnable, unit.toSeconds(delay) * 20);
            }
        } else {
            if (async) {
                bukkitTask = scheduler.runTaskTimerAsynchronously(plugin, runnable, unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
            } else {
                bukkitTask = scheduler.runTaskTimer(plugin, runnable, unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
            }
        }
        return new SpigotTask(bukkitTask);
    }

    private static class SpigotTask implements Task {
        private final BukkitTask task;

        private SpigotTask(BukkitTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}

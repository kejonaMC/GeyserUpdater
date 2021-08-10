package com.projectg.geyserupdater.common.scheduler;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface UpdaterScheduler {

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     */
    default Task run(@Nonnull Runnable runnable, boolean async) {
        Objects.requireNonNull(runnable);
        return schedule(runnable, async, 0, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param delay The delay in milliseconds. A value less than zero should be considered unsafe.
     */
    default Task runDelayed(@Nonnull Runnable runnable, boolean async, long delay, TimeUnit unit) {
        Objects.requireNonNull(runnable);
        return schedule(runnable, async, delay, 0, unit);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param repeat The repeat period, in milliseconds. A value of 0 or less will only run the Runnable once. A value less than zero should be considered unsafe.
     */
    default Task runTimer(@Nonnull Runnable runnable, boolean async, long repeat, TimeUnit unit) {
        Objects.requireNonNull(runnable);
        return schedule(runnable, async, 0, repeat, unit);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param delay The delay in milliseconds. A value less than zero should be considered unsafe.
     * @param repeat The repeat period, in milliseconds. A value of 0 or less will only run the Runnable once. A value less than zero should be considered unsafe.
     */
    Task schedule(@Nonnull Runnable runnable, boolean async, long delay, long repeat, TimeUnit unit);
}



package com.projectg.geyserupdater.common.scheduler;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface UpdaterScheduler {

    default void run(@Nonnull Runnable runnable, boolean async) {
        Objects.requireNonNull(runnable);
        schedule(runnable, async, 0, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param delay The delay in milliseconds
     */
    default void runDelayed(@Nonnull Runnable runnable, boolean async, long delay, TimeUnit unit) {
        Objects.requireNonNull(runnable);
        schedule(runnable, async, delay, 0, unit);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param repeat The repeat period, in milliseconds. A value of 0 will only run the Runnable once.
     */
    default void runTimer(@Nonnull Runnable runnable, boolean async, long repeat, TimeUnit unit) {
        Objects.requireNonNull(runnable);
        schedule(runnable, async, 0, repeat, unit);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param delay The delay in milliseconds
     * @param repeat The repeat period, in milliseconds. A value of 0 will only run the Runnable once.
     */
    void schedule(@Nonnull Runnable runnable, boolean async, long delay, long repeat, TimeUnit unit);
    // todo: make sure that repeat of 0 is handled properly on all platforms

}



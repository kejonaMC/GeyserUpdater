package com.projectg.geyserupdater.common.scheduler;

import javax.annotation.Nonnull;
import java.util.Objects;

public interface UpdaterScheduler {

    default void run(@Nonnull Runnable runnable, boolean async) {
        Objects.requireNonNull(runnable);
        schedule(runnable, async, 0, 0);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param delay The delay in milliseconds
     */
    default void runDelayed(@Nonnull Runnable runnable, boolean async, long delay) {
        Objects.requireNonNull(runnable);
        schedule(runnable, async, delay, 0);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param repeat The repeat period, in milliseconds. A value of 0 will only run the Runnable once.
     */
    default void runTimer(@Nonnull Runnable runnable, boolean async, long repeat) {
        Objects.requireNonNull(runnable);
        schedule(runnable, async, 0, repeat);
    }

    /**
     * Schedule a Runnable to be run.
     * @param runnable The Runnable
     * @param async True to run the task async (Disregarded for BungeeCord, Velocity)
     * @param delay The delay in milliseconds
     * @param repeat The repeat period, in milliseconds. A value of 0 will only run the Runnable once.
     */
    void schedule(@Nonnull Runnable runnable, boolean async, long delay, long repeat);
    // todo: make sure that repeat of 0 is handled properly on all platforms

}



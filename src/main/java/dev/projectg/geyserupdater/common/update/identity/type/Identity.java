package dev.projectg.geyserupdater.common.update.identity.type;

import javax.annotation.Nonnull;

/**
 * Provides implementation for something that can be quantified as an age of something.
 * @param <T> The Type of the age.
 */
public interface Identity<T> {

    @Nonnull
    T value();
}

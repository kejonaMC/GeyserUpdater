package com.projectg.geyserupdater.common.update.age.type;

/**
 * Provides implementation for something that can be quantified as an age of something.
 * @param <T> The Type of the age.
 */
public interface Identity<T> {

    T value();
}

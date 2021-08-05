package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.update.age.AgeComparer;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Updatable {

    private final AgeComparer<?, ?> ageComparer;

    public Updatable(@Nonnull AgeComparer<?, ?> ageComparer) {
        Objects.requireNonNull(ageComparer);
        this.ageComparer = ageComparer;
    }
}

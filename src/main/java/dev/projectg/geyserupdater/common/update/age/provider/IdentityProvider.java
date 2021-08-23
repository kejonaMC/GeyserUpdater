package dev.projectg.geyserupdater.common.update.age.provider;

import dev.projectg.geyserupdater.common.update.age.type.Identity;

import javax.annotation.Nullable;

public interface IdentityProvider<S extends Identity<?>> {

    @Nullable
    S getValue();
}

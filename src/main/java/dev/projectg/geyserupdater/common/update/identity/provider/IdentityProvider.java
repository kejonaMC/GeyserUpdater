package dev.projectg.geyserupdater.common.update.identity.provider;

import dev.projectg.geyserupdater.common.update.identity.type.Identity;

import javax.annotation.Nullable;

public interface IdentityProvider<S extends Identity<?>> {

    @Nullable
    S getValue();
}

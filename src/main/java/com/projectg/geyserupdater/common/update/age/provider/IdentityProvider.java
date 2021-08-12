package com.projectg.geyserupdater.common.update.age.provider;

import com.projectg.geyserupdater.common.update.age.type.Identity;

public interface IdentityProvider<S extends Identity<?>> {

    S getValue();
}

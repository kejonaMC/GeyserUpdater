package com.projectg.geyserupdater.common.update.age.provider;

import com.projectg.geyserupdater.common.update.age.type.Age;

public interface AgeProvider<S extends Age<?>> {

    S getAge();
}

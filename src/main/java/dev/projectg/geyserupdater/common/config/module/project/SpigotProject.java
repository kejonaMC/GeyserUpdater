package dev.projectg.geyserupdater.common.config.module.project;

import space.arim.dazzleconf.annote.ConfKey;

public interface SpigotProject {
    @ConfKey("resource")
    int resourceId();
}

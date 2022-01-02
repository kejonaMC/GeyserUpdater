package dev.projectg.geyserupdater.common.config.module.project;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;
import space.arim.dazzleconf.serialiser.URLValueSerialiser;

import java.net.URL;

@ConfSerialisers(URLValueSerialiser.class)
public interface JenkinsProject {
    @ConfKey("project")
    String projectLink();

    @ConfKey("download")
    URL downloadLink();
}

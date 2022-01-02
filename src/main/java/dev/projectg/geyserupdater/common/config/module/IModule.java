package dev.projectg.geyserupdater.common.config.module;

import dev.projectg.geyserupdater.common.config.module.project.Project;

public interface IModule {

    boolean enable();

    boolean autoCheck();

    boolean autoUpdate();

    Project project();
}

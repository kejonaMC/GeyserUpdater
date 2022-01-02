package dev.projectg.geyserupdater.common.config.module;

import dev.projectg.geyserupdater.common.config.module.project.Project;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
@Getter
public class DefinedModule extends PresetModule implements IModule {

    @Setting("enable")
    private boolean enable = true;

    @Setting("auto-check")
    private boolean autoCheck = true;

    @Setting("auto-update")
    private boolean autoUpdate = false;

    private Project project = null;
}

package dev.projectg.geyserupdater.common.config.module;

import dev.projectg.geyserupdater.common.config.module.project.Project;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
@Getter
public class PresetModule implements IModule {

    @NodeKey
    private String preset;

    @Setting("enable")
    private boolean enable = true;

    @Setting("auto-check")
    private boolean autoCheck = true;

    @Setting("auto-update")
    private boolean autoUpdate = false;

    @Override
    public Project project() {

    }
}

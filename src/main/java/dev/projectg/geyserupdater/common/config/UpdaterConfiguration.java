package dev.projectg.geyserupdater.common.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class UpdaterConfiguration {

    public static int DEFAULT_VERSION = 3;

    @Setting("auto-check-interval")
    private int autoUpdateInterval = 24;

    @Setting("delete-on-fail")
    private boolean deleteOnFail = true;

    @Setting("restart-server")
    private boolean restartServer = false;

    @Setting("restart-script")
    private boolean generateRestartScript = false;

    @Setting("restart-message")
    private String restartMessage = "§2This server will be restarting in 10 seconds!";

    @Setting("download-time-limit")
    private int downloadTimeLimit = 180;

    @Setting("debug")
    private boolean enableDebug = false;

    @Required
    @Setting("config-version")
    private int version = 3;

}

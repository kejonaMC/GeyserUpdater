package com.projectg.geyserupdater.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdaterConfiguration {

    public static int DEFAULT_CONFIG_VERSION = 2;

    @JsonProperty(value = "Auto-Update-Geyser", required = true)
    private boolean autoUpdate = false;
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    @JsonProperty(value = "Auto-Update-Interval", required = true)
    private int autoUpdateInterval = 24;
    public int getAutoUpdateInterval() {
        return autoUpdateInterval;
    }

    @JsonProperty(value = "Auto-Restart-Server", required = true)
    private boolean restartServer = false;
    public boolean isRestartServer() {
        return restartServer;
    }

    @JsonProperty(value = "Auto-Script-Generating", required = true)
    private boolean generateRestartScript = false;
    public boolean isGenerateRestartScript() {
        return generateRestartScript;
    }

    @JsonProperty(value = "Restart-Message-Players", required = true)
    private String restartMessage = "§2This server will be restarting in 10 seconds!";
    public String getRestartMessage() {
        return restartMessage;
    }

    @JsonProperty(value = "Enable-Debug", required = true)
    private boolean enableDebug = false;
    public boolean isEnableDebug() {
        return enableDebug;
    }

    @JsonProperty(value = "Config-Version", required = true)
    private int configVersion = 2;
    public int getConfigVersion() {
        return configVersion;
    }
}

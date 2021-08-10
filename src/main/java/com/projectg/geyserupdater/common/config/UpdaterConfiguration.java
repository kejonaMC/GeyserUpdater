package com.projectg.geyserupdater.common.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
@SuppressWarnings("FieldMayBeFinal") // Must be non-final for Jackson to work
public class UpdaterConfiguration {

    public static int DEFAULT_CONFIG_VERSION = 2;

    @JsonProperty(value = "Auto-Update-Geyser")
    private boolean autoUpdateGeyser = false;
    public boolean isAutoUpdateGeyser() {
        return autoUpdateGeyser;
    }

    @JsonProperty(value = "Auto-Update-Floodgate")
    private boolean autoUpdateFloodgate = false;
    public boolean isAutoUpdateFloodgate() {
        return autoUpdateFloodgate;
    }

    @JsonProperty(value = "Auto-Update-Interval")
    private int autoUpdateInterval = 24;
    public int getAutoUpdateInterval() {
        return autoUpdateInterval;
    }

    @JsonProperty(value = "Auto-Restart-Server")
    private boolean restartServer = false;
    public boolean isRestartServer() {
        return restartServer;
    }

    @JsonProperty(value = "Auto-Script-Generating")
    private boolean generateRestartScript = false;
    public boolean isGenerateRestartScript() {
        return generateRestartScript;
    }
    public void setGenerateRestartScript(boolean generate) {
        generateRestartScript = generate;
    }

    @JsonProperty(value = "Restart-Message-Players")
    private String restartMessage = "§2This server will be restarting in 10 seconds!";
    public String getRestartMessage() {
        return restartMessage;
    }

    @JsonProperty(value = "download-time-limit")
    private int downloadTimeLimit = 180;
    public int getDownloadTimeLimit() {
        return downloadTimeLimit;
    }

    @JsonProperty(value = "Enable-Debug")
    private boolean enableDebug = false;
    public boolean isEnableDebug() {
        return enableDebug;
    }

    @JsonProperty(value = "Config-Version", required = true)
    private int configVersion = 2;
    public int getConfigVersion() {
        return configVersion;
    }

    public boolean isIncorrectVersion() {
        return getConfigVersion() != DEFAULT_CONFIG_VERSION;
    }
}

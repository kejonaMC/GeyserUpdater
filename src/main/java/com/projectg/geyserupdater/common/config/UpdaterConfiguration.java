package com.projectg.geyserupdater.common.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
@SuppressWarnings("FieldMayBeFinal") // Must be non-final for Jackson to work
public class UpdaterConfiguration {

    public static int DEFAULT_CONFIG_VERSION = 3;

    @JsonProperty(value = "default-updates")
    private Map<String, DefaultUpdate> defaultUpdates;
    public Map<String, DefaultUpdate> getDefaultUpdates() {
        return defaultUpdates;
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
    private int configVersion = 3;
    public int getConfigVersion() {
        return configVersion;
    }

    public boolean isIncorrectVersion() {
        return getConfigVersion() != DEFAULT_CONFIG_VERSION;
    }

    public static final class DefaultUpdate {

        @JsonProperty(value = "enable", required = true)
        private boolean enable = false;
        public boolean isEnable() {
            return enable;
        }

        @JsonProperty(value = "auto-check", required = true)
        private boolean autoCheck = false;
        public boolean isAutoCheck() {
            return autoCheck;
        }

        @JsonProperty(value = "auto-update", required = true)
        private boolean autoUpdate = false;
        public boolean isAutoUpdate() {
            return autoUpdate;
        }
    }
}

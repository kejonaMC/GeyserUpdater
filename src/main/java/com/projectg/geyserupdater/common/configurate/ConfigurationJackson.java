package com.projectg.geyserupdater.common.configurate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigurationJackson {

    // Booleans
    @JsonProperty("Auto-Update-Geyser")
    private boolean autoUpdateGeyser;
    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdateGeyser = autoUpdate;
    }
    public Boolean getAutoUpdateGeyser() {
        return autoUpdateGeyser;
    }

    @JsonProperty("Auto-Restart-Server")
    private boolean autoRestartGeyser;
    public void setAutoRestart(Boolean autoRestart) {
        this.autoRestartGeyser = autoRestart;
    }
    public Boolean getAutoRestartGeyser() {
        return autoRestartGeyser;
    }

    @JsonProperty("Auto-Script-Generating")
    private boolean autoScriptGenerating;
    public void setAutoScript(Boolean autoScript) {
        this.autoScriptGenerating = autoScript;
    }
    public Boolean getAutoScriptGenerating() {
        return autoScriptGenerating;
    }

    @JsonProperty("Enable-Debug")
    private boolean enableDebug;
    public void setEnableDebug(Boolean enableDebug) {
        this.enableDebug = enableDebug;
    }
    public Boolean getEnableDebug() {
        return enableDebug;
    }

    // Strings
    @JsonProperty("Restart-Message-Players")
    private String restartMessagePlayers;
    public void setRestartMessagePlayers(String restartMessage) {
        this.restartMessagePlayers = restartMessage;
    }
    public String getRestartMessagePlayers() {
        return restartMessagePlayers;
    }

    // Ints
    @JsonProperty("Auto-Update-Interval")
    private Integer autoUpdateInterval;
    public void setAutoUpdateInterval(int updateInterval) {
        this.autoUpdateInterval = updateInterval;
    }
    public int getAutoUpdateInterval() {
        return autoUpdateInterval;
    }

    @JsonProperty("Config-Version")
    private Integer configVersion;
    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }
    public int getConfigVersion() {
        return configVersion;
    }
}

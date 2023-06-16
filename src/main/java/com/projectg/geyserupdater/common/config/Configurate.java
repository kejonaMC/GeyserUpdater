package com.projectg.geyserupdater.common.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configurate {

    /**
     * Load config
     *
     * @param dataDirectory The config's directory
     */
    public static Configurate configuration(@NotNull Path dataDirectory) throws IOException {

        File folder = dataDirectory.toFile();
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream input = Configurate.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException ignored) {
            }
        }

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(dataDirectory.resolve("config.yml").toFile(), Configurate.class);
    }

    @JsonProperty("Auto-Update-Geyser")
    private boolean autoUpdateGeyser;

    public boolean getAutoUpdateGeyser() {
        return autoUpdateGeyser;
    }

    @JsonProperty("Auto-Update-Interval")
    private long autoUpdateInterval;

    public long getAutoUpdateInterval() {
        return autoUpdateInterval;
    }

    @JsonProperty("Auto-Restart-Server")
    private boolean autoRestartServer;

    public boolean getAutoRestartServer() {
        return autoRestartServer;
    }

    @JsonProperty("Auto-Script-Generating")
    private boolean autoScriptGenerating;

    public boolean getAutoScriptGenerating() {
        return autoScriptGenerating;
    }

    @JsonProperty("Restart-Message-Players")
    private RestartMessagePlayers restartMessagePlayers;

    public RestartMessagePlayers getRestartMessagePlayers() {
        return restartMessagePlayers;
    }

    @JsonProperty("Restart-Timer")
    private int restartTimer;

    public int getRestartTimer() {
        return restartTimer;
    }

    @JsonProperty("Enable-Debug")
    private boolean enableDebug;

    public boolean getEnableDebug() {
        return enableDebug;
    }

    @JsonProperty("Config-Version")
    private int configVersion;

    public int getConfigVersion() {
        return configVersion;
    }

    public static class RestartMessagePlayers {
        @JsonProperty("Warning-Low")
        private String warningLow;

        public String getWarningLow() {
            return warningLow;
        }

        @JsonProperty("Warning-Middle")
        private String warningMiddle;

        public String getWarningMiddle() {
            return warningMiddle;
        }

        @JsonProperty("Warning-High")
        private String warningHigh;

        public String getWarningHigh() {
            return warningHigh;
        }
    }
}
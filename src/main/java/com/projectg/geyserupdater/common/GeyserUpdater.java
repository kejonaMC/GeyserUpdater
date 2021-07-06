package com.projectg.geyserupdater.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


public class GeyserUpdater {

    private final UpdaterLogger logger;
    private final UpdaterScheduler scheduler;
    private final PlayerHandler playerHandler;

    private UpdaterConfiguration config;

    public GeyserUpdater(Path dataFolder, UpdaterLogger logger, UpdaterScheduler scheduler, PlayerHandler playerHandler,
                         boolean loopRestartScript) throws IOException {
        this.logger = logger;
        this.scheduler  = scheduler;
        this.playerHandler = playerHandler;

        this.loadConfig(dataFolder.resolve("config.yml"));





        if (config.isEnableDebug()) {
            logger.enableDebug();
        }

    }

    private void loadConfig(Path userConfig) throws IOException {
        if (!Files.exists(userConfig)) {
            //Files.createDirectories(userConfig.getParent()); todo: necessary?
            try (InputStream inputStream = this.getClass().getResourceAsStream("/config.yml")) {
                Objects.requireNonNull(inputStream);
                Files.copy(inputStream, userConfig);
            }
        }

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        UpdaterConfiguration config = yamlMapper.readValue(userConfig.toFile(), UpdaterConfiguration.class);

        int localVersion = config.getConfigVersion();
        int defaultVersion = UpdaterConfiguration.DEFAULT_CONFIG_VERSION;
        if (localVersion == defaultVersion) {
            this.config = config;
        } else {
            logger.error("Your copy of config.yml is outdated (your version: " + localVersion + ", latest version: " + defaultVersion + "). Please delete it and let a fresh copy of config.yml be regenerated!");
        }
    }


    public UpdaterConfiguration getConfig() {
        return config;
    }
    public UpdaterScheduler getScheduler() {
        return scheduler;
    }
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}

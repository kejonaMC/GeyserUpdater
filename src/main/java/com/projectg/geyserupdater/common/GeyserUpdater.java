package com.projectg.geyserupdater.common;

import com.projectg.geyserupdater.common.config.UpdaterConfiguration;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.projectg.geyserupdater.common.update.DownloadManager;
import com.projectg.geyserupdater.common.update.PluginId;
import com.projectg.geyserupdater.common.update.UpdateManager;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.ScriptCreator;
import com.projectg.geyserupdater.common.util.SpigotResourceUpdateChecker;

import java.io.*;
import java.nio.file.Path;


public class GeyserUpdater {

    private static GeyserUpdater INSTANCE = null;

    private final UpdaterLogger logger;
    private final UpdaterScheduler scheduler;
    private final PlayerHandler playerHandler;
    public final String version;

    private final UpdaterConfiguration config;

    public GeyserUpdater(Path dataFolder,
                         Path downloadFolder,
                         UpdaterLogger logger,
                         UpdaterScheduler scheduler,
                         PlayerHandler playerHandler,
                         boolean ignoreRestartScriptOption,
                         boolean loopRestartScript,
                         String version,
                         String geyserArtifact,
                         String floodgateArtifact) throws IOException {
        this.logger = logger;
        this.scheduler  = scheduler;
        this.playerHandler = playerHandler;
        this.version = version;

        INSTANCE = this;

        // Meta version checking
        scheduler.run(() -> {
            String latestVersion = SpigotResourceUpdateChecker.getVersion(88555);
            if (latestVersion == null || latestVersion.isEmpty()) {
                logger.error("Failed to determine the latest GeyserUpdater version!");
            } else {
                if (latestVersion.equals(version)) {
                    logger.info("You are using the latest version of GeyserUpdater!");
                } else {
                    logger.info("Your version: " + version + ". Latest version: "  + latestVersion + ". Download the newer version at https://www.spigotmc.org/resources/geyserupdater.88555/.");
                }
            }
        }, true);

        // Load the config
        logger.debug("Loading config");
        config = FileUtils.loadConfig(dataFolder.resolve("config.yml"));
        if (config.isIncorrectVersion()) {
            logger.error("Your copy of config.yml is outdated (your version: " + config.getConfigVersion() + ", latest version: " + UpdaterConfiguration.DEFAULT_CONFIG_VERSION + "). Please delete it and let a fresh copy of config.yml be regenerated!");
            return;
        }
        if (config.isEnableDebug()) {
            logger.enableDebug();
        }
        if (ignoreRestartScriptOption) {
            // This is basically just for spigot, so that we don't generate a script if the one defined in spigot.yml exists.
            config.setGenerateRestartScript(false);
        }

        // Make startup script if enabled
        if (config.isGenerateRestartScript()) {
            try {
                logger.debug("Attempting to create restart script");
                ScriptCreator.createRestartScript(loopRestartScript);
            } catch (IOException e) {
                logger.error("Error while creating restart script:");
                e.printStackTrace();
            }
        }

        // Set the correct download links for geyser and floodgate
        PluginId.GEYSER.setArtifact(geyserArtifact);
        PluginId.FLOODGATE.setArtifact(floodgateArtifact);

        // Manager for updating plugins
        UpdateManager updateManager = new UpdateManager(new DownloadManager(downloadFolder));
    }

    public static GeyserUpdater getInstance() {
        return INSTANCE;
    }
    public UpdaterConfiguration getConfig() {
        return config;
    }
    public UpdaterLogger getLogger() {
        return logger;
    }
    public UpdaterScheduler getScheduler() {
        return scheduler;
    }
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}

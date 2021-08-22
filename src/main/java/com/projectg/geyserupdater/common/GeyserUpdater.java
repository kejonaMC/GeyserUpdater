package com.projectg.geyserupdater.common;

import com.projectg.geyserupdater.common.config.UpdaterConfiguration;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.projectg.geyserupdater.common.update.PluginId;
import com.projectg.geyserupdater.common.update.UpdateManager;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.SpigotResourceUpdateChecker;
import org.geysermc.connector.GeyserConnector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public class GeyserUpdater {

    private static GeyserUpdater INSTANCE = null;

    public final String version;

    private final Path downloadFolder;
    private final Path installFolder;
    private final UpdaterLogger logger;
    private final UpdaterScheduler scheduler;
    private final PlayerHandler playerHandler;
    private final UpdateManager updateManager;
    private final UpdaterConfiguration config;

    public GeyserUpdater(Path dataFolder,
                         Path downloadFolder,
                         Path installFolder,
                         UpdaterBootstrap bootstrap,
                         UpdaterLogger logger,
                         UpdaterScheduler scheduler,
                         PlayerHandler playerHandler,
                         String version,
                         String geyserArtifact,
                         String floodgateArtifact) throws IOException {
        this.downloadFolder = downloadFolder;
        this.installFolder = installFolder;
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
        config = FileUtils.loadConfig(dataFolder.resolve("config.yml"));
        if (config.isIncorrectVersion()) {
            throw new IllegalStateException("Your copy of config.yml is outdated (your version: " + config.getConfigVersion() + ", latest version: " + UpdaterConfiguration.DEFAULT_CONFIG_VERSION + "). Please delete it and let a fresh copy of config.yml be regenerated!");
        }
        if (config.isEnableDebug()) {
            logger.enableDebug();
        }

        // Make startup script if enabled
        if (config.isGenerateRestartScript()) {
            try {
                logger.debug("Attempting to create restart script");
                bootstrap.createRestartScript();
            } catch (IOException e) {
                logger.error("Error while creating restart script:");
                e.printStackTrace();
            }
        }

        // Set the enable/autoCheck/autoUpdate values
        PluginId.loadSettings(config);

        // Set the correct download links for geyser and floodgate
        PluginId.GEYSER.setArtifact(geyserArtifact);
        PluginId.FLOODGATE.setArtifact(floodgateArtifact);

        // Manager for updating plugins
        this.updateManager = new UpdateManager(downloadFolder, scheduler, config);
    }

    /**
     * Installs all updates to the correct folder, if necessary. Will do nothing if the downloadFolder is the same file as the installFolder.
     * @throws IOException If there was a failure moving ALL updates.
     */
    public void shutdown() throws IOException {
        try {
            if (Files.isSameFile(installFolder, downloadFolder)) {
                // We don't need to copy anything around
                return;
            }
        } catch (IOException e) {
            logger.error("Failed to check if the installFolder is the same as the downloadFolder. Attempting to move files from the downloadFolder to the installFolder anyway...");
            e.printStackTrace();
        }

        // todo: find a way to make sure we are shutdown last

        // This test isn't ideal but it'll work for now
        if (!GeyserConnector.getInstance().getBedrockServer().isClosed()) {
            throw new UnsupportedOperationException("Cannot replace Geyser before Geyser has shutdown! No updates will be applied.");
        }

        UpdaterLogger.getLogger().debug("Installing plugins from the cache.");
        Files.walk(downloadFolder, 1).forEach((file) -> {
            try {
                Files.move(file, installFolder, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                UpdaterLogger.getLogger().error("Failed to copy update " + file + " to the plugins folder.");
                e.printStackTrace();
            }
        });
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
}

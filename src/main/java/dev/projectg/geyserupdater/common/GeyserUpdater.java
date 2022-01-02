package dev.projectg.geyserupdater.common;

import dev.projectg.geyserupdater.common.config.Configurator;
import dev.projectg.geyserupdater.common.config.UpdaterConfiguration;
import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import dev.projectg.geyserupdater.common.update.PluginId;
import dev.projectg.geyserupdater.common.update.Updatable;
import dev.projectg.geyserupdater.common.update.UpdateManager;
import dev.projectg.geyserupdater.common.util.SpigotUtils;
import space.arim.dazzleconf.error.InvalidConfigException;

import java.io.IOException;
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
    private final PlayerManager playerManager;
    private final UpdateManager updateManager;
    private final UpdaterConfiguration config;

    /**
     * @param dataFolder The data folder for GeyserUpdater
     * @param downloadFolder The default directory to download updates to. Updatables may override it.
     * @param installFolder The default directory to move downloads to whenever GeyserUpdater is shown down. Updatables may override it.
     * @param bootstrap The {@link UpdaterBootstrap} platform implemenetation
     * @param logger The {@link UpdaterLogger} platform implemenetation
     * @param scheduler The {@link UpdaterScheduler} platform implemenetation
     * @param playerManager The {@link PlayerManager} platform implemenetation
     * @param version The version of GeyserUpdater
     * @param geyserArtifact The artifact link for Geyser. For example: "bootstrap/velocity/target/Geyser-Velocity.jar"
     * @param floodgateArtifact The artifact link for Floodgate. For example: "bootstrap/velocity/target/floodgate-velocity.jar"
     * @throws IOException If there was an exception loading the config. {@link this#shutdown()} Should not be called if this exception is thrown.
     */
    public GeyserUpdater(Path dataFolder,
                         Path downloadFolder,
                         Path installFolder,
                         UpdaterBootstrap bootstrap,
                         UpdaterLogger logger,
                         UpdaterScheduler scheduler,
                         PlayerManager playerManager,
                         String version,
                         String geyserArtifact,
                         String floodgateArtifact) throws IOException, InvalidConfigException {
        this.downloadFolder = downloadFolder;
        this.installFolder = installFolder;
        this.logger = logger;
        this.scheduler  = scheduler;
        this.playerManager = playerManager;
        this.version = version;

        INSTANCE = this;

        // Meta version checking
        scheduler.run(() -> {
            String latestVersion = SpigotUtils.getVersion(88555);
            if (latestVersion == null || latestVersion.isEmpty()) {
                logger.error("Failed to determine the latest GeyserUpdater version!");
            } else {
                if (latestVersion.equals(version)) {
                    logger.info("You are using the latest version of GeyserUpdater!");
                } else {
                    logger.warn("Your version: " + version + ". Latest version: "  + latestVersion + ". Download the newer version at https://www.spigotmc.org/resources/geyserupdater.88555/");
                }
            }
        }, true);

        // Load the config
        config = Configurator.loadConfig(dataFolder.resolve("config.yml"));
        if (UpdaterConfiguration.DEFAULT_VERSION != config.version()) {
            throw new IllegalStateException("Your copy of config.yml is outdated (your version: " + config.version() + ", latest version: " + UpdaterConfiguration.DEFAULT_VERSION + "). Please delete it and let a fresh copy of config.yml be regenerated!");
        }
        if (config.enableDebug()) {
            logger.enableDebug();
        }

        // Make startup script if enabled
        if (config.generateRestartScript()) {
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

        // todo: restart after updates
        // todo: better message sending, to players too
    }

    /**
     * Installs all updates to the correct folder, if necessary. Will do nothing if the downloadFolder is the same file as the installFolder.
     * @throws IOException If there was a failure moving ALL updates.
     */
    public void shutdown() throws IOException {
        updateManager.shutdown(); //fixme: wait for the last download to finish, or cancel it and delete the unfinished file before copying files

        UpdaterLogger.getLogger().debug("Installing plugins from the cache.");
        Files.createDirectories(installFolder);

        // Only move files that we have tracked
        for (Updatable updatable : updateManager.getTrackedUpdatables()) {
            Path update = updatable.outputFile;
            if (Files.exists(update)) {
                try {
                    if (Files.isSameFile(update.getParent(), installFolder)) {
                        // it is already where it should be, don't move it
                        continue;
                    }
                } catch (IOException e) {
                    logger.warn("Failed to check if the install folder is the same as the download folder for " + updatable + ". Attempting to move files from the downloadFolder to the installFolder anyway...");
                    if (logger.isDebug()) {
                        e.printStackTrace();
                    }
                }

                try {
                    Files.move(update, installFolder.resolve(update.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    logger.debug("Moved " + updatable.outputFile + " to " + installFolder);
                } catch (IOException e) {
                    UpdaterLogger.getLogger().error("Failed to copy update file " + updatable + " to directory " + installFolder);
                    e.printStackTrace();
                }
            }
        }
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

    public PlayerManager getPlayerHandler() {
        return playerManager;
    }
}

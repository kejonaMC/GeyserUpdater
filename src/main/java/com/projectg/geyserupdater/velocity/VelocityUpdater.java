package com.projectg.geyserupdater.velocity;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.common.util.ScriptCreator;
import com.projectg.geyserupdater.velocity.command.GeyserUpdateCommand;
import com.projectg.geyserupdater.velocity.listeners.VelocityJoinListener;
import com.projectg.geyserupdater.velocity.logger.Slf4jUpdaterLogger;
import com.projectg.geyserupdater.velocity.util.GeyserVelocityDownloader;
import com.projectg.geyserupdater.velocity.util.bstats.Metrics;

import com.google.inject.Inject;

import com.moandjiezana.toml.Toml;

import org.geysermc.connector.GeyserConnector;
import org.slf4j.Logger;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Plugin(id = "geyserupdater", name = "GeyserUpdater", version = "1.5.0", description = "Automatically or manually downloads new builds of Geyser and applies them on server restart.", authors = {"Jens"},
        dependencies = {@Dependency(id = "geyser")})
public class VelocityUpdater {

    private static VelocityUpdater plugin;
    private final ProxyServer server;
    private final Logger baseLogger;
    private final Path dataDirectory;
    private final Toml config;
    private final Metrics.Factory metricsFactory;

    @Inject
    public VelocityUpdater(ProxyServer server, Logger baseLogger, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
        VelocityUpdater.plugin = this;
        this.server  = server;
        this.baseLogger = baseLogger;
        this.dataDirectory = folder;
        this.config = loadConfig(dataDirectory);
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        metricsFactory.make(this, 10673);
        new Slf4jUpdaterLogger(baseLogger);

        if (getConfig().getBoolean("Enable-Debug", false)) {
            UpdaterLogger.getLogger().info("Trying to enable debug logging.");
            UpdaterLogger.getLogger().enableDebug();
        }

        checkConfigVersion();
        // todo: meta version checking

        // Register our only command
        server.getCommandManager().register("geyserupdate", new GeyserUpdateCommand());
        // Player alert if a restart is required when they join
        server.getEventManager().register(this, new VelocityJoinListener());

        // Make startup script if enabled
        if (config.getBoolean("Auto-Script-Generating")) {
            try {
                ScriptCreator.createRestartScript(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Auto update Geyser if enabled in the config
        if (config.getBoolean("Auto-Update-Geyser")) {
            scheduleAutoUpdate();
        }
        // Check if downloaded Geyser file exists periodically
        server.getScheduler()
                .buildTask(this, () -> {
                    FileUtils.checkFile("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar", true);
                    UpdaterLogger.getLogger().info("A new Geyser build has been downloaded! Please restart Velocity in order to use the updated build!");
                })
                .delay(30L, TimeUnit.MINUTES)
                .repeat(12L, TimeUnit.HOURS)
                .schedule();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onShutdown(ProxyShutdownEvent event) {
        // This test isn't ideal but it'll work for now
        if (!GeyserConnector.getInstance().getBedrockServer().isClosed()) {
            throw new UnsupportedOperationException("Cannot shutdown GeyserUpdater before Geyser has shutdown! No updates will be applied.");
        }
        try {
            moveGeyserJar();
            for (int i = 0; i <= 2; i++) {
                try {
                    deleteGeyserJar();
                    break;
                } catch (IOException ioException) {
                    UpdaterLogger.getLogger().warn("An I/O error occurred while attempting to delete an unnecessary Geyser jar! Trying again " + (2 - i) + " more times.");
                    ioException.printStackTrace();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException interruptException) {
                        UpdaterLogger.getLogger().error("Failed to delay an additional attempt!");
                        interruptException.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            UpdaterLogger.getLogger().error("An I/O error occurred while attempting to replace the current Geyser jar with the new one!");
            e.printStackTrace();
        }
    }

    /**
     * Load GeyserUpdater's config
     *
     * @param path The config's directory
     * @return The configuration
     */
    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        return new Toml().read(file);
    }

    /**
     * Check the config version of GeyserUpdater
     */
    public void checkConfigVersion() {
        //Change version number only when editing config.yml!
        if (getConfig().getLong("Config-Version", 0L).compareTo(2L) != 0) {
            UpdaterLogger.getLogger().warn("Your copy of config.yml is outdated. Please delete it and let a fresh copy of config.yml be regenerated!");
        }
    }

    /**
     * Check for a newer version of Geyser every 24hrs
     */
    public void scheduleAutoUpdate() {
        UpdaterLogger.getLogger().debug("Scheduling auto updater");
        // Checking for the build numbers of current build.
        // todo: build this in different way so that we don't repeat it if the Auto-Update-Interval is zero or -1 or something
        server.getScheduler()
                .buildTask(this, () -> {
                    UpdaterLogger.getLogger().debug("Checking if a new build of Geyser exists.");
                    try {
                        boolean isLatest = GeyserProperties.isLatestBuild();
                        if (!isLatest) {
                            UpdaterLogger.getLogger().info("A newer build of Geyser is available! Attempting to download the latest build now...");
                            GeyserVelocityDownloader.updateGeyser();
                        }
                    } catch (IOException e) {
                        UpdaterLogger.getLogger().error("Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.");
                        e.printStackTrace();
                    }
                })
                .delay(1L, TimeUnit.MINUTES)
                .repeat(getConfig().getLong("Auto-Update-Interval", 24L), TimeUnit.HOURS)
                .schedule();
    }

    /**
     * Replace the Geyser jar in the plugin folder with the one in GeyserUpdater/BuildUpdate
     * Should only be called once Geyser has been disabled
     *
     * @throws IOException if there was an IO failure
     */
    public void moveGeyserJar() throws IOException {
        // Moving Geyser Jar to Plugins folder "Overwriting".
        File fileToCopy = new File("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar");
        if (fileToCopy.exists()) {
            UpdaterLogger.getLogger().debug("Moving the new Geyser jar to the plugins folder.");
            FileInputStream input = new FileInputStream(fileToCopy);
            File newFile = new File("plugins/Geyser-Velocity.jar");
            FileOutputStream output = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            input.close();
            output.close();
        } else {
            UpdaterLogger.getLogger().debug("Found no new Geyser jar to copy to the plugins folder.");
        }
    }

    /**
     * Delete the Geyser jar in GeyserUpdater/BuildUpdate
     *
     * @throws IOException if it failed to delete
     */
    private void deleteGeyserJar() throws IOException {
        UpdaterLogger.getLogger().debug("Deleting the Geyser jar in the BuildUpdate folder if it exists");
        Path file = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar");
        Files.deleteIfExists(file);
    }

    public static VelocityUpdater getPlugin() {
        return plugin;
    }
    public ProxyServer getProxyServer() {
        return server;
    }
    public Path getDataDirectory() {
        return dataDirectory;
    }
    public Toml getConfig() {
        return config;
    }
}







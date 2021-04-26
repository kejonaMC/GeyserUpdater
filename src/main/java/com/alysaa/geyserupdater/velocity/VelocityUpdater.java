package com.alysaa.geyserupdater.velocity;

import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.common.util.ScriptCreator;
import com.alysaa.geyserupdater.velocity.command.GeyserUpdateCommand;
import com.alysaa.geyserupdater.velocity.listeners.VelocityJoinListener;
import com.alysaa.geyserupdater.velocity.util.GeyserVelocityDownloader;
import com.alysaa.geyserupdater.velocity.util.bstats.Metrics;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Plugin(id = "geyserupdater", name = "GeyserUpdater", version = "1.4.0-SNAPSHOT", description = "Updating Geyser with ease", authors = {"Jens"},
        dependencies = {@Dependency(id = "geyser")})
public class VelocityUpdater {

    private static VelocityUpdater plugin;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Toml config;
    private final Metrics.Factory metricsFactory;

    @Inject
    public VelocityUpdater(ProxyServer server, Logger logger, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
        VelocityUpdater.plugin = this;
        this.server  = server;
        this.logger = logger;
        this.dataDirectory = folder;
        this.config = loadConfig(dataDirectory);
        this.metricsFactory = metricsFactory;
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Create folder for storing new Geyser jar
        createUpdateFolder();
        // Make startup script
        makeScriptFile();
        // Register our only command
        server.getCommandManager().register("geyserupdate", new GeyserUpdateCommand());
        // Player alert if a restart is required when they join
        server.getEventManager().register(this, new VelocityJoinListener());
        // Auto update Geyser if enabled in the config
        startAutoUpdate();
        // Check if downloaded Geyser file exists periodically
        server.getScheduler()
                .buildTask(this, () -> {
                    FileUtils.checkFile("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar", true);
                    logger.info("New Geyser build has been downloaded! Velocity restart is required!");
                })
                .delay(30L, TimeUnit.MINUTES)
                .repeat(12L, TimeUnit.HOURS)
                .schedule();

        Metrics metrics = metricsFactory.make(this, 10673);
    }
    @Subscribe(order = PostOrder.LAST)
    public void onShutdown(ProxyShutdownEvent event) {
        try {
            this.moveGeyser();
        } catch (IOException e) {
            logger.warn("No updates have been implemented.");
        }
        try {
            this.deleteBuild();
        } catch (Exception ignored) {
        }
    }

    public void createUpdateFolder() {
        // Creating BuildUpdate folder
        File updateDir = new File("plugins/GeyserUpdater/BuildUpdate");
        if (!updateDir.exists()) {
            try {
                updateDir.mkdirs();
            } catch (Exception ignored) { }
        }
    }
    private void makeScriptFile() {
        if (config.getBoolean("Auto-Script-Generating")) {
            try {
                ScriptCreator.createRestartScript(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void startAutoUpdate() {
        if (config.getBoolean("Auto-Update-Geyser")) {
            // Checking for the build numbers of current build.
            server.getScheduler()
                    .buildTask(this, () -> {
                        try {
                            boolean isLatest = GeyserProperties.isLatestBuild();
                            if (!isLatest) {
                                logger.info("A newer version of Geyser is available. Downloading now...");
                                GeyserVelocityDownloader.updateGeyser();
                            }
                        } catch (IOException e) {
                            logger.error("Failed to check if Geyser is outdated!");
                            e.printStackTrace();
                        }
                    })
                    .repeat(24L, TimeUnit.HOURS)
                    .schedule();
        }
    }
    public void moveGeyser() throws IOException {
        // Moving Geyser Jar to Plugins folder "Overwriting".
        File fileToCopy = new File("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar");
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
    }
    private void deleteBuild() throws IOException {
        Path file = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar");
        Files.delete(file);
    }
    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
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

    public static VelocityUpdater getPlugin() {
        return plugin;
    }
    public ProxyServer getProxyServer() {
        return server;
    }
    public Logger getLogger() {
        return logger;
    }
    public Path getDataDirectory() {
        return dataDirectory;
    }
    public Toml getConfig() {
        return config;
    }
}







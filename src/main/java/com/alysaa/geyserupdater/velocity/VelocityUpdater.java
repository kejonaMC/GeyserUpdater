package com.alysaa.geyserupdater.velocity;

import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.common.util.ScriptCreator;
import com.alysaa.geyserupdater.velocity.command.GeyserUpdateCommand;
import com.alysaa.geyserupdater.common.util.OSUtils;
import com.alysaa.geyserupdater.velocity.listeners.VelocityJoinListener;
import com.alysaa.geyserupdater.velocity.util.GeyserVeloDownloader;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Plugin(id = "geyserupdater", name = "GeyserUpdater", version = "1.4.0-SNAPSHOT", description = "Updating Geyser with ease", authors = {"Jens"},
        dependencies = {@Dependency(id = "geyser")})
public class VelocityUpdater {

    public static ProxyServer server;
    private static VelocityUpdater plugin;
    public static Logger logger;
    public Path dataDirectory;
    private static Toml configf;
    private final Metrics.Factory metricsFactory;

    @Inject
    public VelocityUpdater(ProxyServer server, Logger logger, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
        VelocityUpdater.server  = server;
        VelocityUpdater.plugin = this;
        VelocityUpdater.logger = logger;
        configf = loadConfig(folder);
        this.metricsFactory = metricsFactory;
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("GeyserUpdater v1.4.0 has been enabled");
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
        if (configf.getBoolean("Auto-Script-Generating")) {
            if (OSUtils.isWindows() || OSUtils.isLinux() || OSUtils.isMac()) {
                try {
                    ScriptCreator.createRestartScript(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.warn("Your OS is not supported! We support Linux, Mac, and Windows for automatic script creation!");
            }
        }
    }
    public void startAutoUpdate() {
        if (configf.getBoolean("Auto-Update-Geyser")) {
            // Checking for the build numbers of current build.
            server.getScheduler()
                    .buildTask(this, () -> {
                        try {
                            boolean isLatest = GeyserProperties.isLatestBuild();
                            if (!isLatest) {
                                logger.info("A newer version of Geyser is available. Downloading now...");
                                GeyserVeloDownloader.updateGeyser();
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

    public Toml getConfig() {
        return configf;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getProxyServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}







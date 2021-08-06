package com.projectg.geyserupdater.velocity;

import com.google.inject.Inject;
import com.projectg.geyserupdater.common.GeyserUpdater;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.velocity.command.GeyserUpdateCommand;
import com.projectg.geyserupdater.velocity.listeners.VelocityJoinListener;
import com.projectg.geyserupdater.velocity.logger.Slf4jUpdaterLogger;
import com.projectg.geyserupdater.velocity.util.bstats.Metrics;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.geysermc.connector.GeyserConnector;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(id = "geyserupdater", name = "GeyserUpdater", version = VelocityUpdater.VERSION, description = "Automatically or manually downloads new builds of Geyser and applies them on server restart.", authors = {"Jens"},
        dependencies = {@Dependency(id = "geyser")})
public class VelocityUpdater {

    public static final String VERSION = "1.6.0";
    private static VelocityUpdater PLUGIN;

    private final ProxyServer server;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    @Inject
    public VelocityUpdater(ProxyServer server, Logger baseLogger, @DataDirectory final Path folder, Metrics.Factory metricsFactory) throws IOException {
        VelocityUpdater.PLUGIN = this;
        this.server  = server;
        this.dataDirectory = folder;
        this.metricsFactory = metricsFactory;

        new GeyserUpdater(
                dataDirectory,
                dataDirectory.resolve("BuildUpdate"),
                new Slf4jUpdaterLogger(baseLogger),
                new VelocityScheduler(this),
                new VelocityPlayerHandler(server),
                false,
                true,
                VERSION,
                "/artifact/bootstrap/velocity/target/Geyser-Velocity.jar",
                "/artifact/velocity/target/floodgate-velocity.jar"
        );
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        metricsFactory.make(this, 10673);

        // Register our only command
        server.getCommandManager().register("geyserupdate", new GeyserUpdateCommand());
        // Player alert if a restart is required when they join
        server.getEventManager().register(this, new VelocityJoinListener());

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
     * Replace the Geyser jar in the plugin folder with the one in GeyserUpdater/BuildUpdate
     * Should only be called once Geyser has been disabled
     *
     * @throws IOException if there was an IO failure
     */
    public void moveGeyserJar() throws IOException {
        // Moving Geyser Jar to Plugins folder "Overwriting".
        File fileToCopy = new File("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar"); //todo: improve
        if (fileToCopy.exists()) {
            UpdaterLogger.getLogger().debug("Moving the new Geyser jar to the plugins folder.");
            FileInputStream input = new FileInputStream(fileToCopy);
            File newFile = new File("plugins/Geyser-Velocity.jar"); //todo: improve
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
        Path file = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar"); //todo: improve
        Files.deleteIfExists(file);
    }

    public static VelocityUpdater getPlugin() {
        return PLUGIN;
    }
    public ProxyServer getProxyServer() {
        return server;
    }
}







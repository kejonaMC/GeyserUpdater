package com.projectg.geyserupdater.bungee;

import com.projectg.geyserupdater.bungee.command.GeyserUpdateCommand;
import com.projectg.geyserupdater.bungee.listeners.BungeeJoinListener;
import com.projectg.geyserupdater.bungee.util.GeyserBungeeDownloader;
import com.projectg.geyserupdater.bungee.util.bstats.Metrics;
import com.projectg.geyserupdater.common.logger.JavaUtilUpdaterLogger;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.common.util.ScriptCreator;

import com.projectg.geyserupdater.common.util.SpigotResourceUpdateChecker;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public final class BungeeUpdater extends Plugin {

    private static BungeeUpdater plugin;
    private Configuration configuration;
    private UpdaterLogger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = new JavaUtilUpdaterLogger(getLogger());
        new Metrics(this, 10203);

        this.loadConfig();
        if (getConfig().getBoolean("Enable-Debug", false)) {
            UpdaterLogger.getLogger().info("Trying to enable debug logging.");
            UpdaterLogger.getLogger().enableDebug();
        }

        this.checkConfigVersion();
        // Check GeyserUpdater version
        this.checkUpdaterVersion();

        this.getProxy().getPluginManager().registerCommand(this, new GeyserUpdateCommand());
        // Player alert if a restart is required when they join
        getProxy().getPluginManager().registerListener(this, new BungeeJoinListener());

        // Make startup script
        if (configuration.getBoolean("Auto-Script-Generating")) {
            try {
                // Tell the createScript method that a loop is necessary because bungee has no restart system.
                ScriptCreator.createRestartScript(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Auto update Geyser if enabled
        if (configuration.getBoolean("Auto-Update-Geyser")) {
            scheduleAutoUpdate();
        }
        // Check if downloaded Geyser file exists periodically
        getProxy().getScheduler().schedule(this, () -> {
            if (FileUtils.checkFile("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar", true)) {
                logger.info("A new Geyser build has been downloaded! Please restart BungeeCord in order to use the updated build!");
            }
        }, 30, 720, TimeUnit.MINUTES);

    }

    @Override
    public void onDisable() {
        // Force Geyser to disable so we can modify the jar in the plugins folder without issue
        logger.debug("Forcing Geyser to disable first...");
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            moveGeyserJar();
            deleteGeyserJar();
        } catch (IOException e) {
            logger.error("An I/O error occurred while attempting to update Geyser!");
            e.printStackTrace();
        }
    }

    /**
     * Load GeyserUpdater's config, create it if it doesn't exist
     */
    public void loadConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(Config.startConfig(this, "config.yml"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Check the config version of GeyserUpdater
     */
    public void checkConfigVersion(){
        //Change version number only when editing config.yml!
         if (configuration.getInt("Config-Version", 0) != 2){
            logger.error("Your copy of config.yml is outdated. Please delete it and let a fresh copy of config.yml be regenerated!");
         }
    }

    /**
     * Check the version of GeyserUpdater against the spigot resource page
     */
    public void checkUpdaterVersion() {
        getProxy().getScheduler().runAsync(this, () -> {
            String pluginVersion = getDescription().getVersion();
            String latestVersion = SpigotResourceUpdateChecker.getVersion();
            if (latestVersion == null || latestVersion.length() == 0) {
                logger.error("Failed to determine the latest GeyserUpdater version!");
            } else {
                if (latestVersion.equals(pluginVersion)) {
                    logger.info("You are using the latest version of GeyserUpdater!");
                } else {
                    logger.info("Your version: " + pluginVersion + ". Latest version: "  + latestVersion + ". Download the newer version at https://www.spigotmc.org/resources/geyserupdater.88555/.");
                }
            }

        });
    }

    /**
     * Check for a newer version of Geyser every 24hrs
     */
    public void scheduleAutoUpdate() {
        // todo: build this in different way so that we don't repeat it if the Auto-Update-Interval is zero or -1 or something
        getProxy().getScheduler().schedule(this, () -> {
            logger.debug("Checking if a new build of Geyser exists.");
            try {
                // Checking for the build numbers of current build.
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (!isLatest) {
                    logger.info("A newer build of Geyser is available! Attempting to download the latest build now...");
                    GeyserBungeeDownloader.updateGeyser();
                }
            } catch (IOException e) {
                logger.error("Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.");
                e.printStackTrace();
            }
        }, 1, getConfig().getLong("Auto-Update-Interval", 24L) * 60, TimeUnit.MINUTES);
    }

    /**
     * Replace the Geyser jar in the plugin folder with the one in GeyserUpdater/BuildUpdate
     * Should only be called once Geyser has been disabled
     *
     * @throws IOException if there was an IO failure
     */
    public void moveGeyserJar() throws IOException {
        // Moving Geyser Jar to Plugins folder "Overwriting".
        File fileToCopy = new File("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        if (fileToCopy.exists()) {
            logger.debug("Moving the new Geyser jar to the plugins folder.");
            FileInputStream input = new FileInputStream(fileToCopy);
            File newFile = new File("plugins/Geyser-BungeeCord.jar");
            FileOutputStream output = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            input.close();
            output.close();
        } else {
            logger.debug("Found no new Geyser jar to copy to the plugins folder.");
        }
    }

    /**
     * Delete the Geyser jar in GeyserUpdater/BuildUpdate
     *
     * @throws IOException If it failed to delete
     */
    private void deleteGeyserJar() throws IOException {
        UpdaterLogger.getLogger().debug("Deleting the Geyser jar in the BuildUpdate folder if it exists");
        Path file = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        Files.deleteIfExists(file);
    }
    public static BungeeUpdater getPlugin() {
        return plugin;
    }
    public Configuration getConfig() {
        return configuration;
    }
}

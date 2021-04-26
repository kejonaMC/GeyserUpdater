package com.alysaa.geyserupdater.bungee;

import com.alysaa.geyserupdater.bungee.command.GeyserUpdateCommand;
import com.alysaa.geyserupdater.bungee.listeners.BungeeJoinListener;
import com.alysaa.geyserupdater.bungee.util.BungeeResourceUpdateChecker;
import com.alysaa.geyserupdater.bungee.util.Config;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownload;
import com.alysaa.geyserupdater.bungee.util.bstats.Metrics;
import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.common.util.ScriptCreator;

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
import java.util.logging.Logger;

public final class BungeeUpdater extends Plugin {

    private static BungeeUpdater plugin;
    private Configuration configuration;
    private Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = plugin.getLogger();
        new Metrics(this, 10203);
        this.getProxy().getPluginManager().registerCommand(this, new GeyserUpdateCommand());
        this.onConfig();
        this.createUpdateFolder();
        try {
            this.startAutoUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.checkConfigVer();
        // Player alert if a restart is required when they join
        getProxy().getPluginManager().registerListener(this, new BungeeJoinListener());
        // Check if downloaded Geyser file exists periodically
        getProxy().getScheduler().schedule(this, () -> {
            if (FileUtils.checkFile("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar", true)) {
                logger.info("A new Geyser build has been downloaded! Please restart BungeeCord in order to use the updated build!");
            }
        }, 30, 720, TimeUnit.MINUTES);
        // Check GeyserUpdater version periodically
        getProxy().getScheduler().schedule(this, this::versionCheck, 0, 24, TimeUnit.HOURS);
        // Make startup script
        makeScriptFile();
    }

    private void makeScriptFile() {
        if (configuration.getBoolean("Auto-Script-Generating")) {
            try {
                // Tell the createScript method that a loop is necessary because bungee has no restart system.
                ScriptCreator.createRestartScript(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onDisable() {
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            this.moveGeyser();
        } catch (IOException e) {
            logger.severe("An I/O error occurred while attempting to update Geyser!");
        }
        try {
            this.deleteBuild();
        } catch (Exception ignored) { }
    }
    public void checkConfigVer(){
        //Change version number only when editing config.yml!
         if (!(configuration.getInt("version") == 1)){
            logger.warning("Your copy of config.yml is outdated. Please delete it and let a fresh copy of config.yml be regenerated!");
         }
    }
    public void versionCheck() {
        getProxy().getScheduler().runAsync(this, () -> {
            String pluginVersion = getDescription().getVersion();
            String version = BungeeResourceUpdateChecker.getVersion(plugin);
            if (version == null || version.length() == 0) {
                logger.severe("Failed to determine the current GeyserUpdater version!");
            } else {
                if (version.equals(pluginVersion)) {
                    logger.info("You are using the latest version of GeyserUpdater!");
                } else {
                    logger.info("There is a new update available for GeyserUpdater! Download it now at https://www.spigotmc.org/resources/geyserupdater.88555/.");
                }
            }

        });
    }

    public void onConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(Config.startConfig(this, "config.yml"));
        } catch (IOException exception) {
            exception.printStackTrace();
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
    public void startAutoUpdate() throws IOException {
        if (configuration.getBoolean("Auto-Update-Geyser")) {
            getProxy().getScheduler().schedule(this, () -> {
                try {
                    // Checking for the build numbers of current build.
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (!isLatest) {
                        logger.info("A newer build of Geyser is available! Attempting to download the latest build now...");
                        GeyserBungeeDownload.updateGeyser();
                    }
                } catch (IOException e) {
                    logger.severe("Failed to check for updates to Geyser!");
                    e.printStackTrace();
                }
            }, 0, 24, TimeUnit.HOURS);
        }
    }
    public void moveGeyser() throws IOException {
        // Moving Geyser Jar to Plugins folder "Overwriting".
        File fileToCopy = new File("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
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
    }
    private void deleteBuild() throws IOException {
        Path file = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        Files.delete(file);
    }
    public static BungeeUpdater getPlugin() {
        return plugin;
    }
    public Configuration getConfiguration() {
        return configuration;
    }
}

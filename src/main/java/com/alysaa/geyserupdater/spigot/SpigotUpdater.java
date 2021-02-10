package com.alysaa.geyserupdater.spigot;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.common.util.ResourceUpdaterSpigot;
import com.alysaa.geyserupdater.spigot.command.GeyserCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.alysaa.geyserupdater.spigot.util.bstats.Metrics;

public class SpigotUpdater extends JavaPlugin {
    public static SpigotUpdater plugin;
    private FileConfiguration config;

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        new Metrics(this, 10202);
        getLogger().info("Has been Enabled");
        this.getCommand("geyserupdate").setExecutor(new GeyserCommand());
        createFiles();
        plugin = this;
        // If true start auto updating
        if (getConfig().getBoolean("EnableAutoUpdateGeyser")) {
            try {
                Timer StartAutoUpdate;
                StartAutoUpdate = new Timer();
                StartAutoUpdate.schedule(new StartUpdate(),0,100*60*14400);
                // Auto Update Cycle on Startup and each 24h after startup
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Enable File Checking here
        Timer StartFileCheck;
        StartFileCheck = new Timer();
        StartFileCheck.schedule(new StartTimer(),100*60*300,100*60*300);
        // File Checking Each 30min after server startup.
        // Logger for check update on geyserupdater
            Logger logger = this.getLogger();

            new ResourceUpdaterSpigot(this, 88555).getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    logger.info("There is not a new update available.");
                } else {
                    logger.info("There is a new update available.");
                }
            });
    }
    public void onDisable() {
        getLogger().info("Plugin has been disabled");
    }
    private void createFiles() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        File updateDir = new File("plugins/update");
        if (!updateDir.exists()) {
            try {
                updateDir.mkdirs();
            } catch (Exception ignored) {
            }
        }
    }
    private class StartTimer extends TimerTask {
        @Override
        public void run() {
            CheckBuildFile.CheckSpigotFile();
        }
    }
    private class StartUpdate extends TimerTask {
        @Override
        public void run() {
            try {
                CheckBuildNum.CheckBuildNumberSpigotAuto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

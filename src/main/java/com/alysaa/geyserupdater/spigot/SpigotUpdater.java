package com.alysaa.geyserupdater.spigot;

import com.alysaa.geyserupdater.common.util.OSUtils;
import com.alysaa.geyserupdater.spigot.command.GeyserUpdateCommand;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownload;
import com.alysaa.geyserupdater.spigot.util.SpigotJoinListener;
import com.alysaa.geyserupdater.spigot.util.SpigotResourceUpdateChecker;
import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.spigot.util.CheckSpigotRestart;
import org.bukkit.Bukkit;
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
    public Logger logger;
    private FileConfiguration config;

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        new Metrics(this, 10202);
        getLogger().info("GeyserUpdater v1.4.0 has been enabled");
        getCommand("geyserupdate").setExecutor(new GeyserUpdateCommand());
        createFiles();
        checkConfigVer();
        // If true start auto updating
        if (getConfig().getBoolean("Auto-Update-Geyser")) {
            try {
                Timer StartAutoUpdate;
                StartAutoUpdate = new Timer();
                StartAutoUpdate.schedule(new StartUpdate(), 0, 1000 * 60 * 1440);
                // Auto Update Cycle on Startup and each 24h after startup
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Enable File Checking here
        Timer StartFileCheck;
        StartFileCheck = new Timer();
        // File Checking every 12h after 30min after server start
        StartFileCheck.schedule(new StartTimer(), 1000 * 60 * 30, 1000 * 60 * 720);
        // Check our version
        versionCheck();
        // Player alert if a restart is required when they join
        Bukkit.getServer().getPluginManager().registerEvents(new SpigotJoinListener(), this);
        // Check if a restart script already exists
        // We create one if it doesn't
        if (getConfig().getBoolean("Auto-Script-Generating")) {
            if (OSUtils.isWindows() || OSUtils.isLinux() || OSUtils.isMac()) {
                try {
                    CheckSpigotRestart.checkYml();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                getLogger().warning("Your OS is not supported! We support Linux, Mac, and Windows for automatic script creation!");
            }
        }
    }
    public void checkConfigVer(){
        //Change version number only when editing config.yml!
        if (!(getConfig().getInt("version") == 1)){
                logger.info("Config.yml is outdated. please regenerate a new config.yml!");
            }
        }
    public void versionCheck() {
        String pluginVersion = this.getDescription().getVersion();
        String version = SpigotResourceUpdateChecker.getVersion(plugin);
        if (version == null || version.length() == 0) {
            logger.severe("Failed to check version of GeyserUpdater!");
        } else {
            if (version.equals(pluginVersion)) {
                logger.info("There are no new updates for GeyserUpdater available.");
            } else {
                logger.info("There is a new update available for GeyserUpdater! Download it now at https://www.spigotmc.org/resources/geyserupdater.88555/.");
            }
        }
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
            } catch (Exception ignored) {}
        }
    }

    // TODO Spigot probably has a better way of doing timers.

    private class StartTimer extends TimerTask {
        @Override
        public void run() {
            FileUtils.checkFile("plugins/update/Geyser-Spigot.jar", false);
            logger.info("New Geyser build has been downloaded! Restart is required!");
        }
    }
    private class StartUpdate extends TimerTask {
        @Override
        public void run() {
            try {
                boolean isLatest = GeyserProperties.isLatestBuild();
                if (!isLatest) {
                    getLogger().info("A newer version of Geyser is available. Downloading now...");
                    GeyserSpigotDownload.downloadGeyser();
                }
            } catch (IOException e) {
                getLogger().severe("Failed to check if Geyser is outdated!");
                e.printStackTrace();
            }
        }
    }
}

package com.alysaa.geyserupdater.spigot;

import com.alysaa.geyserupdater.common.util.OSUtils;
import com.alysaa.geyserupdater.spigot.command.GeyserCommand;
import com.alysaa.geyserupdater.spigot.util.SpigotResourceUpdateChecker;
import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.spigot.util.CheckSpigotRestart;
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
        getLogger().info("GeyserUpdater v1.2.0 has been enabled");
        this.getCommand("geyserupdate").setExecutor(new GeyserCommand());
        createFiles();
        checkConfigVer();
        plugin = this;
        // If true start auto updating
        if (getConfig().getBoolean("Auto-Update-Geyser")) {
            try {
                Timer StartAutoUpdate;
                StartAutoUpdate = new Timer();
                StartAutoUpdate.schedule(new StartUpdate(), 0, 100 * 60 * 14400);
                // Auto Update Cycle on Startup and each 24h after startup
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Enable File Checking here
        Timer StartFileCheck;
        StartFileCheck = new Timer();
        StartFileCheck.schedule(new StartTimer(), 100 * 60 * 300, 100 * 60 * 300);
        // File Checking Each 30min after server startup.
        // Logger for check update on GeyserUpdater
        versionCheck();
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
                System.out.println("[GeyserUpdater] Your OS is not supported! We support Linux, Mac, and Windows for automatic script creation!");
            }
        }
    }
    public void checkConfigVer(){
        Logger logger = this.getLogger();
        //Change version number only when editing config.yml!
        if (!(getConfig().getInt("version") ==1)){
                logger.info("Config.yml is outdated. please regenerate a new config.yml!");
            }
        }
    public void versionCheck() {
        Logger logger = this.getLogger();
        String pluginVersion = this.getDescription().getVersion();
        SpigotUpdater plugin = this;
        Runnable runnable = () -> {
            String version = SpigotResourceUpdateChecker.getVersion(plugin);
            if (version.equals(pluginVersion)) {
                logger.info("There are no new updates for GeyserUpdater available.");
            } else {
                logger.info("There is a new update available for GeyserUpdater! Download it now at https://www.spigotmc.org/resources/geyserupdater.88555/.");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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
    private class StartTimer extends TimerTask {
        @Override
        public void run() {
            CheckBuildFile.checkSpigotFile();
        }
    }
    private class StartUpdate extends TimerTask {
        @Override
        public void run() {
            try {
                CheckBuildNum.checkBuildNumberSpigotAuto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

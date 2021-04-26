package com.alysaa.geyserupdater.spigot;

import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.spigot.command.GeyserUpdateCommand;
import com.alysaa.geyserupdater.spigot.listeners.SpigotJoinListener;
import com.alysaa.geyserupdater.spigot.util.CheckSpigotRestart;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownloader;
import com.alysaa.geyserupdater.spigot.util.SpigotResourceUpdateChecker;
import com.alysaa.geyserupdater.spigot.util.bstats.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class SpigotUpdater extends JavaPlugin {

    private static SpigotUpdater plugin;
    private Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        new Metrics(this, 10202);
        Objects.requireNonNull(getCommand("geyserupdate")).setExecutor(new GeyserUpdateCommand());
        createFiles();
        checkConfigVer();

        // If true start auto updating
        if (getConfig().getBoolean("Auto-Update-Geyser")) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
                        boolean isLatest = GeyserProperties.isLatestBuild();
                        if (!isLatest) {
                            getLogger().info("A newer version of Geyser is available. Downloading now...");
                            GeyserSpigotDownloader.updateGeyser();
                        }
                    } catch (IOException e) {
                        getLogger().severe("Failed to check if Geyser is outdated!");
                        e.printStackTrace();
                    }
                }
            }.runTaskTimer(this, 30 * 60 * 20, 12 * 60 * 60 * 20);
        }

        // Enable File Checking here. delay of 30 minutes and period of 12 hours (given in ticks)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (FileUtils.checkFile("plugins/update/Geyser-Spigot.jar", false)) {
                    logger.info("New Geyser build has been downloaded! Restart is required!");
                }
            }
        }.runTaskTimerAsynchronously(this, 30 * 60 * 20, 12 * 60 * 60 * 20);

        // Check our version
        versionCheck();
        // Player alert if a restart is required when they join
        Bukkit.getServer().getPluginManager().registerEvents(new SpigotJoinListener(), this);
        // Check if a restart script already exists
        // We create one if it doesn't
        if (getConfig().getBoolean("Auto-Script-Generating")) {
            try {
                CheckSpigotRestart.checkYml();
            } catch (Exception e) {
                e.printStackTrace();
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
        new BukkitRunnable() {

            @Override
            public void run() {
                String pluginVersion = plugin.getDescription().getVersion();
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
        }.runTaskAsynchronously(this);
    }
    private void createFiles() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
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
    public static SpigotUpdater getPlugin() {
        return plugin;
    }
}

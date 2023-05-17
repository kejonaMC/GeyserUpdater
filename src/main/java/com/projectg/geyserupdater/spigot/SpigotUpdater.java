package com.projectg.geyserupdater.spigot;

import com.projectg.geyserupdater.common.config.Configurate;
import com.projectg.geyserupdater.common.logger.JavaUtilUpdaterLogger;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.common.util.GeyserProperties;
import com.projectg.geyserupdater.spigot.command.GeyserUpdateCommand;
import com.projectg.geyserupdater.spigot.listeners.SpigotJoinListener;
import com.projectg.geyserupdater.spigot.util.CheckSpigotRestart;
import com.projectg.geyserupdater.spigot.util.GeyserSpigotDownloader;
import com.projectg.geyserupdater.common.util.SpigotResourceUpdateChecker;
import com.projectg.geyserupdater.spigot.util.bstats.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Objects;

public class SpigotUpdater extends JavaPlugin {
    private static SpigotUpdater plugin;
    private static Configurate config = null;

    @Override
    public void onEnable() {
        plugin = this;
        new JavaUtilUpdaterLogger(getLogger());
        new Metrics(this, 10202);

        try {
            config = Configurate.configuration(this.getDataFolder().toPath());
        } catch (IOException e) {
            UpdaterLogger.getLogger().error("Could not create config.yml! " + e.getMessage());
            onDisable();
        }

        if (config.getEnableDebug()) {
            UpdaterLogger.getLogger().info("Trying to enable debug logging.");
            UpdaterLogger.getLogger().enableDebug();
        }

        checkConfigVersion();
        // Check our version
        checkUpdaterVersion();

        Objects.requireNonNull(getCommand("geyserupdate")).setExecutor(new GeyserUpdateCommand());
        getCommand("geyserupdate").setPermission("gupdater.geyserupdate");
        // Player alert if a restart is required when they join
        Bukkit.getServer().getPluginManager().registerEvents(new SpigotJoinListener(), this);

        // Check if a restart script already exists
        // We create one if it doesn't
        if (config.getAutoScriptGenerating()) {
            try {
                CheckSpigotRestart.checkYml();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // If true, start auto updating now and every 24 hours
        if (config.getAutoUpdateGeyser()) {
            scheduleAutoUpdate();
        }
        // Enable File Checking here. delay of 30 minutes and period of 12 hours (given in ticks)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (FileUtils.checkFile("plugins/update/Geyser-Spigot.jar", false)) {
                    UpdaterLogger.getLogger().info("A new Geyser build has been downloaded! Please restart the server in order to use the updated build!");
                }
            }
        }.runTaskTimerAsynchronously(this, 30 * 60 * 20, 12 * 60 * 60 * 20);
    }

    /**
     * Check the config version of GeyserUpdater
     */
    public void checkConfigVersion() {
        //Change version number only when editing config.yml!
        if (config.getConfigVersion() != 2) {
            UpdaterLogger.getLogger().warn("Your copy of config.yml is outdated. Please delete it and let a fresh copy of config.yml be regenerated!");
        }
    }

    /**
     * Check the version of GeyserUpdater against the spigot resource page
     */
    public void checkUpdaterVersion() {
        UpdaterLogger logger = UpdaterLogger.getLogger();
        String pluginVersion = plugin.getDescription().getVersion();
        new BukkitRunnable() {
            @Override
            public void run() {
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
            }
        }.runTaskAsynchronously(this);
    }

    /**
     * Check for a newer version of Geyser every 24hrs
     */
    public void scheduleAutoUpdate() {
        UpdaterLogger.getLogger().debug("Scheduling auto updater");
        // todo: build this in different way so that we don't repeat it if the Auto-Update-Interval is zero or -1 or something
        new BukkitRunnable() {

            @Override
            public void run() {
                UpdaterLogger.getLogger().debug("Checking if a new build of Geyser exists.");
                try {
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (!isLatest) {
                        UpdaterLogger.getLogger().info("A newer build of Geyser is available! Attempting to download the latest build now...");
                        GeyserSpigotDownloader.updateGeyser();
                    }
                } catch (IOException e) {
                    UpdaterLogger.getLogger().error("Failed to check for updates to Geyser! We were unable to reach the Geyser build server, or your local branch does not exist on it.");
                    e.printStackTrace();
                }
                // Auto-Update-Interval is in hours. We convert it into ticks
            }
        }.runTaskTimer(this, 60 * 20, config.getAutoUpdateInterval() * 60 * 60 * 20);
    }

    public static SpigotUpdater getPlugin() {
        return plugin;
    }
}

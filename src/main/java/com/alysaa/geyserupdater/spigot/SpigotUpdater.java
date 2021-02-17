package com.alysaa.geyserupdater.spigot;

import com.alysaa.geyserupdater.spigot.command.GeyserCommand;
import com.alysaa.geyserupdater.spigot.util.SpigotResourceUpdateChecker;
import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.common.util.ScriptCreator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        getLogger().info("GeyserUpdater v1.1.0 has been enabled");
        this.getCommand("geyserupdate").setExecutor(new GeyserCommand());
        createFiles();
        plugin = this;
        // If true start auto updating
        if (getConfig().getBoolean("EnableAutoUpdateGeyser")) {
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
        // Make startup script
        if (getConfig().getBoolean("EnableAutoScript")) {
            try {
                makeScriptFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makeScriptFile() {
        FileConfiguration spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
        String scriptPath = spigot.getString("settings.restart-script");
        File script = new File(scriptPath);
        if (script.exists()) {
            System.out.println("[GeyserUpdater] Has detected a restart script.");
        } else {
            try {
                //need to add os check on string
                String scriptName = ("./ServerRestartScript.bat");
                spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
                spigot.set("settings.restart-script",scriptName);
                spigot.save("spigot.yml");
                URI fileURI;
                fileURI = new URI(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                File jar = new File(fileURI.getPath());
                // Tell the createScript method the name of the server jar
                // and that a loop is not necessary because spigot has a restart system.
                ScriptCreator.createScript(jar.getName(), false);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
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

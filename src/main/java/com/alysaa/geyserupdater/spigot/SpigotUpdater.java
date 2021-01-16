package com.alysaa.geyserupdater.spigot;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.spigot.command.GeyserCommand;
import com.alysaa.geyserupdater.spigot.util.AutoUpdateGeyser;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SpigotUpdater extends JavaPlugin {
    public static SpigotUpdater plugin;
    private AutoUpdateGeyser AutoUpdate;
    private CheckBuildFile CheckFile;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        getLogger().info("has been enabled");
        this.getCommand("geyserupdate").setExecutor(new GeyserCommand());
        createFiles();
        plugin = this;
        // If true start auto updating
        if (getConfig().getBoolean("EnableAutoUpdateGeyser")) {
            AutoUpdate = new AutoUpdateGeyser(this);
            AutoUpdate.runTaskTimer(this, 0, 1728000);
        }
        // Enable File Checking
        CheckFile = new CheckBuildFile();
        CheckFile.runTaskTimer(this, 0, 36000);
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
}

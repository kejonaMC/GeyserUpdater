package com.geyserupdater.spigot;

import com.geyserupdater.spigot.command.GeyserCommand;
import com.geyserupdater.spigot.Util.AutoUpdateGeyser;
import com.geyserupdater.common.Util.CheckBuildFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SpigotUpdater extends JavaPlugin {
    public static SpigotUpdater plugin;
    AutoUpdateGeyser AutoUpdate;
    CheckBuildFile CheckFile;

    @Override
    public void onEnable() {
        getLogger().info("Plugin has been enabled");
        this.getCommand("geyserupdate").setExecutor(new GeyserCommand());
        createFiles();
        plugin = this;
        // If true start auto updating
        if (getConfig().getBoolean("EnableGeyserAutoUpdating")) {
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
    private File configf;
    private FileConfiguration config;
    private void createFiles() {
        configf = new File(getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        File thedir = new File("plugins/update");
        if (!thedir.exists()) {
            try {
                thedir.mkdirs();
            } catch (Exception e) {
            }
        }
    }
}

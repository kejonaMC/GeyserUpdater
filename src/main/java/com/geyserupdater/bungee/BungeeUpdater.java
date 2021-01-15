package com.geyserupdater.bungee;

import com.geyserupdater.bungee.Util.*;


import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public final class BungeeUpdater extends Plugin {

    public static BungeeUpdater plugin;
    public static Configuration configuration;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("has loaded!");
        this.getProxy().getPluginManager().registerCommand(this, new com.geyserupdater.bungee.command.GeyserCommand());
        this.onConfig();
        this.createUpdateFolder();
        this.startautoupdate();
        this.CheckFile();
    }

    public void onDisable() {
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        System.out.println("[GeyserUpdater] Checking if updated build is present!");
        Path sourcePath = Paths.get("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        Path destinationPath = Paths.get("plugins");

        try {
            Files.copy(sourcePath, destinationPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (FileAlreadyExistsException e) {
            //destination file already exists
        } catch (IOException e) {
            //something else went wrong
            e.printStackTrace();
        }
    }
    public void onConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(Config.startConfig(this, "config.yml"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void createUpdateFolder() {
        File thedir = new File("plugins/GeyserUpdater/BuildUpdate");
        if (!thedir.exists()) {
            try {
                thedir.mkdirs();
            } catch (Exception ignored) { }
        }
    }

    public void CheckFile() {
        getProxy().getScheduler().schedule(this, AutoUpdateGeyser::checkFile, 30, 30, TimeUnit.MINUTES);
    }

    public void startautoupdate() {
        if (this.getConfiguration().getBoolean("EnableAutoUpdateGeyser")) {
            getProxy().getScheduler().schedule(this, () -> AutoUpdateGeyser.checkUpdate(getProxy().getConsole()), 0, 24, TimeUnit.HOURS);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}

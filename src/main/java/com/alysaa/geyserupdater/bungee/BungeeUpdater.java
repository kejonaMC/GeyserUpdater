package com.alysaa.geyserupdater.bungee;

import com.alysaa.geyserupdater.bungee.util.Config;
import com.alysaa.geyserupdater.bungee.command.GeyserCommand;
import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.geysermc.connector.configuration.GeyserConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public final class BungeeUpdater extends Plugin {

    public static BungeeUpdater plugin;
    public static Configuration configuration;

    @Override
    public void onEnable() {
        getLogger().info("| GeyserUpdater   V 0.2.4 By Jens |");
        plugin = this;
        this.getProxy().getPluginManager().registerCommand(this, new GeyserCommand());
        this.onConfig();
        this.createUpdateFolder();
        try {
            this.startAutoUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.checkFile();
    }

    public void onDisable() {
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            this.moveGeyser();
        } catch (IOException e) {
            System.out.print("[GeyserUpdater] No update have been implemented.");
        }
        try {
            this.deleteBuild();
        } catch (Exception ignored) {
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
        // Creating BuildUpdate folder
        File updateDir = new File("plugins/GeyserUpdater/BuildUpdate");
        if (!updateDir.exists()) {
            try {
                updateDir.mkdirs();
            } catch (Exception ignored) {
            }
        }
    }

    public void checkFile() {
        getProxy().getScheduler().schedule(this, CheckBuildFile::checkBungeeFile, 30, 30, TimeUnit.MINUTES);
    }

    public void startAutoUpdate() throws IOException {
        if (this.getConfiguration().getBoolean("EnableAutoUpdateGeyser")) {
            getProxy().getScheduler().schedule(this, () -> {
                try {
                    // Checking for the build numbers of current build.
                    CheckBuildNum.CheckBuildNumberBungeeAuto();
                } catch (IOException e) {
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

    public static Configuration getConfiguration() {
        return configuration;
    }
}

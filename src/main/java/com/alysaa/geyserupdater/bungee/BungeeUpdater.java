package com.alysaa.geyserupdater.bungee;

import com.alysaa.geyserupdater.bungee.util.AutoUpdateGeyser;
import com.alysaa.geyserupdater.bungee.util.Config;
import com.alysaa.geyserupdater.bungee.command.GeyserCommand;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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
        plugin = this;

        this.getProxy().getPluginManager().registerCommand(this, new GeyserCommand());
        this.onConfig();
        this.createUpdateFolder();
        this.startAutoUpdate();
        this.checkFile();

        getLogger().info("has loaded!");
    }

    public void onDisable() {
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            this.moveGeyser();
        } catch (IOException e) {
            System.out.print("[GeyserUpdater] No updates are being implemented.");
        }
        try {
            this.deleteBuild();
        } catch (Exception ignored) { }
        }

    public void onConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(Config.startConfig(this, "config.yml"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void createUpdateFolder() {
        File updateDir = new File("plugins/GeyserUpdater/BuildUpdate");
        if (!updateDir.exists()) {
            try {
                updateDir.mkdirs();
            } catch (Exception ignored) { }
        }
    }

    public void checkFile() {
        getProxy().getScheduler().schedule(this, AutoUpdateGeyser::checkFile, 30, 30, TimeUnit.MINUTES);
    }

    public void startAutoUpdate() {
        if (this.getConfiguration().getBoolean("EnableAutoUpdateGeyser")) {
            getProxy().getScheduler().schedule(this, () -> AutoUpdateGeyser.checkUpdate(getProxy().getConsole()), 0, 24, TimeUnit.HOURS);
        }
    }

    public void moveGeyser() throws IOException {
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

    public Configuration getConfiguration() {
        return configuration;
    }
}

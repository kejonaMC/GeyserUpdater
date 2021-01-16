package com.geyserupdater.bungee;

import com.geyserupdater.bungee.Util.*;


import com.geyserupdater.bungee.command.GeyserCommand;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.LanguageUtils;


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
        getLogger().info("has loaded!");
        this.getProxy().getPluginManager().registerCommand(this, new com.geyserupdater.bungee.command.GeyserCommand());
        this.onConfig();
        this.createUpdateFolder();
        this.startautoupdate();
        this.CheckFile();
    }

    public void onDisable() {
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            this.MoveGeyser();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.DeleteBuild();
        } catch (IOException e) {
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

    public void MoveGeyser() throws IOException {
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

    private void DeleteBuild() throws IOException {
        File file = new File("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar");
        Files.delete(file.toPath());
    }


    public Configuration getConfiguration() {
        return configuration;
    }
}

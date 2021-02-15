package com.alysaa.geyserupdater.bungee;

import com.alysaa.geyserupdater.bungee.util.Config;
import com.alysaa.geyserupdater.bungee.command.GeyserCommand;
import com.alysaa.geyserupdater.bungee.util.bstats.Metrics;
import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.common.util.CheckBuildNum;
import com.alysaa.geyserupdater.bungee.util.BungeeResourceUpdateChecker;
import com.alysaa.geyserupdater.common.util.CheckOSScript;
import com.alysaa.geyserupdater.common.util.CreateScript.MakeWinBat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class BungeeUpdater extends Plugin {

    public BungeeUpdater plugin;
    public static Configuration configuration;

    @Override
    public void onEnable() {
        new Metrics(this, 10203);
        getLogger().info("GeyserUpdater v1.1.0 has been enabled");
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
        this.batFile();
        ProxyServer.getInstance().getScheduler().schedule(this,this::VersionCheck, 0, 30, TimeUnit.MINUTES);
    }

    private void batFile() {
        CheckOSScript.CheckingOs();
    }

    public void onDisable() {
        Logger logger = this.getLogger();
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            this.moveGeyser();
        } catch (IOException e) {
            logger.info("[GeyserUpdater] No updates have been implemented.");
        }
        try {
            this.deleteBuild();
        } catch (Exception ignored) { }
    }
    public void VersionCheck() {
        Logger logger = this.getLogger();
        String pluginVersion = this.getDescription().getVersion();
        BungeeUpdater plugin = this;
        Runnable runnable = () -> {
            String version = BungeeResourceUpdateChecker.getVersion(plugin);
            if (version.equals(pluginVersion)) {
                logger.info("There are no new updates for GeyserUpdater available.");
            } else {
                logger.info("There is a new update available for GeyserUpdater! Download it now at https://www.spigotmc.org/resources/geyserupdater.88555/.");
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
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
            } catch (Exception ignored) { }
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

package com.alysaa.geyserupdater.bungee;

import com.alysaa.geyserupdater.bungee.command.GeyserUpdateCommand;
import com.alysaa.geyserupdater.bungee.util.BungeeJoinListener;
import com.alysaa.geyserupdater.bungee.util.Config;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownload;
import com.alysaa.geyserupdater.bungee.util.bstats.Metrics;
import com.alysaa.geyserupdater.bungee.util.BungeeResourceUpdateChecker;
import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.common.util.GeyserProperties;
import com.alysaa.geyserupdater.common.util.OSUtils;
import com.alysaa.geyserupdater.common.util.ScriptCreator;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class BungeeUpdater extends Plugin {

    public static BungeeUpdater plugin;
    public static Configuration configuration;
    Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        new Metrics(this, 10203);
        getLogger().info("GeyserUpdater v1.4.0 has been enabled");
        plugin = this;
        this.getProxy().getPluginManager().registerCommand(this, new GeyserUpdateCommand());
        this.onConfig();
        this.createUpdateFolder();
        try {
            this.startAutoUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.checkConfigVer();
        // Player alert if a restart is required when they join
        getProxy().getPluginManager().registerListener(this, new BungeeJoinListener());
        // Check if downloaded Geyser file exists periodically
        getProxy().getScheduler().schedule(this, () -> {
            if (FileUtils.checkFile("plugins/GeyserUpdater/BuildUpdate/Geyser-BungeeCord.jar", true)) {
                logger.info("New Geyser build has been downloaded! BungeeCord restart is required!");
            }
        }, 30, 720, TimeUnit.MINUTES);
        // Check GeyserUpdater version periodically
        getProxy().getScheduler().schedule(this, this::versionCheck, 0, 24, TimeUnit.HOURS);
        // Make startup script
        makeScriptFile();
    }

    private void makeScriptFile() {
        if (getConfiguration().getBoolean("Auto-Script-Generating")) {
            if (OSUtils.isWindows() || OSUtils.isLinux() || OSUtils.isMac()) {
                try {
                // Tell the createScript method that a loop is necessary because bungee has no restart system.
                ScriptCreator.createRestartScript(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("[GeyserUpdater] Your OS is not supported! We support Linux, Mac, and Windows for automatic script creation!");
            }
        }
    }
    public void onDisable() {
        getProxy().getPluginManager().getPlugin("Geyser-BungeeCord").onDisable();
        try {
            this.moveGeyser();
        } catch (IOException e) {
            logger.info("No updates have been implemented.");
        }
        try {
            this.deleteBuild();
        } catch (Exception ignored) { }
    }
    public void checkConfigVer(){
        //Change version number only when editing config.yml!
         if (!(getConfiguration().getInt("version") == 1)){
            logger.info("Config.yml is outdated. please regenerate a new config.yml!");
         }
    }
    public void versionCheck() {
        String pluginVersion = this.getDescription().getVersion();
        BungeeUpdater plugin = this;
        Runnable runnable = () -> {
            String version = BungeeResourceUpdateChecker.getVersion(plugin);
            if (version == null || version.length() == 0) {
                logger.severe("Failed to check version of GeyserUpdater!");
            } else {
                if (version.equals(pluginVersion)) {
                    logger.info("There are no new updates for GeyserUpdater available.");
                } else {
                    logger.info("There is a new update available for GeyserUpdater! Download it now at https://www.spigotmc.org/resources/geyserupdater.88555/.");
                }
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
    public void startAutoUpdate() throws IOException {
        if (this.getConfiguration().getBoolean("Auto-Update-Geyser")) {
            getProxy().getScheduler().schedule(this, () -> {
                try {
                    // Checking for the build numbers of current build.
                    boolean isLatest = GeyserProperties.isLatestBuild();
                    if (!isLatest) {
                        logger.info("A newer version of Geyser is available. Downloading now...");
                        GeyserBungeeDownload.downloadGeyser();
                    }
                } catch (IOException e) {
                    logger.severe("Failed to check if Geyser is outdated!");
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

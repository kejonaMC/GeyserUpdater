package com.projectg.geyserupdater.spigot;

import com.projectg.geyserupdater.common.GeyserUpdater;
import com.projectg.geyserupdater.common.UpdaterBootstrap;
import com.projectg.geyserupdater.common.logger.JavaUtilUpdaterLogger;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.spigot.command.GeyserUpdateCommand;
import com.projectg.geyserupdater.spigot.util.CheckSpigotRestart;
import com.projectg.geyserupdater.spigot.util.bstats.Metrics;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class SpigotUpdater extends JavaPlugin implements UpdaterBootstrap {

    private GeyserUpdater updater;


    @Override
    public void onEnable() {
        Server server = this.getServer();
        Path updateFolder = server.getUpdateFolderFile().toPath();
        try {
            updater = new GeyserUpdater(
                    this.getDataFolder().toPath(),
                    updateFolder,
                    updateFolder,
                    this,
                    new JavaUtilUpdaterLogger(getLogger()),
                    new SpigotScheduler(this),
                    new SpigotPlayerHandler(server),
                    this.getDescription().getVersion(),
                    "/artifact/bootstrap/spigot/target/Geyser-Spigot.jar",
                    "/artifact/bootstrap/spigot/target/floodgate-spigot.jar"
                    );
        } catch (IOException e) {
            getLogger().severe("Failed to start GeyserUpdater! Disabling...");
            e.printStackTrace();
        }

        Objects.requireNonNull(getCommand("geyserupdate")).setExecutor(new GeyserUpdateCommand());
        getCommand("geyserupdate").setPermission("gupdater.geyserupdate");
        new Metrics(this, 10202);
    }

    @Override
    public void onDisable() {
        try {
            updater.shutdown();
        } catch (IOException e) {
            UpdaterLogger.getLogger().error("Failed to install ALL updates:");
            e.printStackTrace();
        }
    }

    @Override
    public void createRestartScript() throws IOException {
        CheckSpigotRestart.checkYml();
    }
}

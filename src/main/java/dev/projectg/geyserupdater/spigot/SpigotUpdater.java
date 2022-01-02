package dev.projectg.geyserupdater.spigot;

import dev.projectg.geyserupdater.common.GeyserUpdater;
import dev.projectg.geyserupdater.common.UpdaterBootstrap;
import dev.projectg.geyserupdater.common.logger.JavaUtilUpdaterLogger;
import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.spigot.command.GeyserUpdateCommand;
import dev.projectg.geyserupdater.spigot.util.CheckSpigotRestart;
import dev.projectg.geyserupdater.spigot.util.bstats.Metrics;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.error.InvalidConfigException;

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
                    "bootstrap/spigot/target/Geyser-Spigot.jar",
                    "bootstrap/spigot/target/floodgate-spigot.jar"
                    );
        } catch (IOException | InvalidConfigException e) {
            getLogger().severe("Failed to start GeyserUpdater! Disabling...");
            e.printStackTrace();
            return;
        }

        Objects.requireNonNull(getCommand("geyserupdate")).setExecutor(new GeyserUpdateCommand());
        getCommand("geyserupdate").setPermission("gupdater.geyserupdate");
        new Metrics(this, 10202);
    }

    @Override
    public void onDisable() {
        // bukkit has the native update folder to update jars on startup, which means we don't need to worry about modifying jars in use and when we shutdown
        if (updater != null) {
            try {
                updater.shutdown();
            } catch (IOException e) {
                UpdaterLogger.getLogger().error("Failed to install ALL updates:");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createRestartScript() throws IOException {
        CheckSpigotRestart.checkYml();
    }
}

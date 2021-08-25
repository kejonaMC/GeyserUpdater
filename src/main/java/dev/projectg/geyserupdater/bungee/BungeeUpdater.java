package dev.projectg.geyserupdater.bungee;

import dev.projectg.geyserupdater.bungee.command.GeyserUpdateCommand;
import dev.projectg.geyserupdater.bungee.bstats.Metrics;
import dev.projectg.geyserupdater.common.GeyserUpdater;
import dev.projectg.geyserupdater.common.UpdaterBootstrap;
import dev.projectg.geyserupdater.common.logger.JavaUtilUpdaterLogger;
import dev.projectg.geyserupdater.common.logger.UpdaterLogger;

import dev.projectg.geyserupdater.common.util.ScriptCreator;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Path;

public class BungeeUpdater extends Plugin implements UpdaterBootstrap {

    private GeyserUpdater updater;

    @Override
    public void onEnable() {
        Path dataFolder = this.getDataFolder().toPath();
        try {
            updater = new GeyserUpdater(
                    dataFolder,
                    dataFolder.resolve("BuildUpdate"),
                    this.getProxy().getPluginsFolder().toPath(),
                    this,
                    new JavaUtilUpdaterLogger(getLogger()),
                    new BungeeScheduler(this),
                    new BungeePlayerHandler(),
                    this.getDescription().getVersion(),
                    "bootstrap/bungeecord/target/Geyser-BungeeCord.jar",
                    "bootstrap/bungee/target/floodgate-bungee.jar"
            );
        } catch (IOException e) {
            getLogger().severe("Failed to start GeyserUpdater! Disabling...");
            e.printStackTrace();
            return;
        }

        // Ensure we get disabled last
        new PluginMapModifier().run(updater.getLogger(), this);

        this.getProxy().getPluginManager().registerCommand(this, new GeyserUpdateCommand());
        new Metrics(this, 10203);
    }

    @Override
    public void onDisable() {
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
        ScriptCreator.createRestartScript(true);
    }
}

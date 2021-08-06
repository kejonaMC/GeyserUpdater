package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.update.age.AgeComparer;
import com.projectg.geyserupdater.common.update.age.provider.JenkinsBuildProvider;
import com.projectg.geyserupdater.common.update.age.type.BuildNumber;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class UpdateManager {

    /**
     * The {@link DownloadManager} to use for downloading new versions of plugins.
     */
    private final DownloadManager downloadManager;

    /**
     * All tracked plugins
     */
    private final Set<Updatable> updatables = new HashSet<>();

    /**
     * Plugins that are outdated and must be updated
     */
    private Set<Updatable> outdatedPlugins = new HashSet<>();

    public UpdateManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
        UpdaterLogger logger = UpdaterLogger.getLogger();

        for (PluginId pluginId : PluginId.values()) {
            if (pluginId.isEnabled()) {
                // Get the git.properties
                InputStream is = pluginId.getPluginClass().getResourceAsStream("git.properties");
                if (is == null) {
                    throw new AssertionError("Unable to find resource 'git.properties' for plugin: " + pluginId.name());
                }

                Properties gitProperties = new Properties();
                try {
                    gitProperties.load(is);
                    is.close();
                } catch (IOException e) {
                    logger.error("Failed to get git.properties for plugin: " + pluginId.name() + ". Unable to update.");
                    e.printStackTrace();
                }

                String buildNumberString = gitProperties.getProperty("git.build.number");
                String branch = gitProperties.getProperty("git.branch");
                if (buildNumberString == null || branch == null) {
                    throw new AssertionError("Failed to find build number or branch in Git Properties '" + gitProperties + "' of plugin '" + pluginId.name() + "'");
                }

                BuildNumber buildNumber = new BuildNumber(Integer.parseInt(buildNumberString));
                JenkinsBuildProvider jenkins = new JenkinsBuildProvider();
                register(new Updatable(
                        pluginId.name(),
                        new AgeComparer<>(buildNumber, jenkins),
                        pluginId.getLatestFileLink(),
                        null));
            }
        }

        // Load extra stuff from the config if we wanted, I guess
    }

    /**
     * Register an {@link Updatable} to be updated
     * @param updatable The {@link Updatable} to be updated
     */
    public void register(Updatable updatable) {
        updatables.add(updatable);
    }

    public void checkAll() {
        outdatedPlugins = new HashSet<>();
        for (Updatable updatable : updatables) {
            // todo: make sure we don't run this sync... maybe instantiate GeyserUpdater.class async?
            if (!updatable.ageComparer.checkIfEquals()) {
                outdatedPlugins.add(updatable);
            }
        }

        UpdaterLogger.getLogger().info("Updates required for plugins: " + outdatedPlugins);
    }

    public void updateAll() {
        for (Updatable updatable : outdatedPlugins) {
            downloadManager.queue(updatable);
        }
    }
}

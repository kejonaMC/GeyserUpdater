package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.GeyserUpdater;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.update.age.AgeComparer;
import com.projectg.geyserupdater.common.update.age.provider.JenkinsBuildProvider;
import com.projectg.geyserupdater.common.update.age.type.BuildNumber;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UpdateManager {

    private final List<Updatable> updatables = new ArrayList<>();

    public UpdateManager(GeyserUpdater geyserUpdater) {
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
                register(new Updatable(new AgeComparer<>(buildNumber, jenkins)));
            }
        }

        // Load extra stuff from the config if we wanted, I guess
    }

    /**
     * Register an {@link Updatable} to be updated
     * @param updatable The {@link Updatable} to be updated
     * @return true, if it was registered.
     */
    public boolean register(Updatable updatable) {
        return updatables.add(updatable);
    }
}

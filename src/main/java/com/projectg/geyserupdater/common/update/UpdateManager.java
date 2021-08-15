package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import com.projectg.geyserupdater.common.update.age.DownloadResult;
import com.projectg.geyserupdater.common.update.age.IdentityComparer;
import com.projectg.geyserupdater.common.update.age.provider.FileHashProvider;
import com.projectg.geyserupdater.common.update.age.provider.JenkinsBuildProvider;
import com.projectg.geyserupdater.common.update.age.provider.JenkinsHashProvider;
import com.projectg.geyserupdater.common.update.age.type.BuildNumber;
import com.projectg.geyserupdater.common.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
    protected Set<Updatable> outdatedPlugins = new HashSet<>();

    /**
     * Plugins that are in the queue for download or are being downloaded
     */
    protected final Set<Updatable> updatablesInQueue = new HashSet<>();


    public UpdateManager(Path defaultDownloadLocation, UpdaterScheduler scheduler, int downloadTimeLimit) {
        this.downloadManager = new DownloadManager(this, scheduler, downloadTimeLimit);
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
                    UpdaterLogger.getLogger().error("Failed to find build number or branch in git Properties '" + gitProperties + "' of plugin '" + pluginId.name() + "'. Not updating.");
                    continue;
                }

                // For age comparer
                BuildNumber buildNumber = new BuildNumber(Integer.parseInt(buildNumberString));
                JenkinsBuildProvider buildProvider = new JenkinsBuildProvider();

                // File hash comparer
                IdentityComparer<?, ?> hashComparer = null;
                try {
                    FileHashProvider localHashProvider = new FileHashProvider(FileUtils.getCodeSourceLocation(pluginId.getClass()));
                    JenkinsHashProvider jenkinsHashProvider = new JenkinsHashProvider(pluginId.getLatestFileLink() + "/*fingerprint*/");
                    hashComparer = new IdentityComparer<>(localHashProvider, jenkinsHashProvider);
                } catch (URISyntaxException | MalformedURLException e) {
                    UpdaterLogger.getLogger().error("Failure while getting location of file for " + pluginId.name() + ". It will be possible to update it, but not to compare file hashes.");
                    e.printStackTrace();
                }

                register(new Updatable(
                        pluginId.name(),
                        new IdentityComparer<>(buildNumber, buildProvider),
                        hashComparer,
                        pluginId.getLatestFileLink(),
                        defaultDownloadLocation));
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

    public void setOutdatedPlugins() {
        outdatedPlugins = new HashSet<>();
        for (Updatable updatable : updatables) {
            // todo: make sure we don't run this sync... maybe instantiate GeyserUpdater.class async?
            if (!updatable.identityComparer.checkIfEquals()) {
                outdatedPlugins.add(updatable);
            }
        }

        UpdaterLogger.getLogger().info("Updates required for plugins: " + outdatedPlugins);
    }

    public Set<Updatable> getOutdatedPlugins() {
        return outdatedPlugins;
    }

    /**
     * Update an outdated Updatable
     * @return true if the download was queued, false if the Updatable is not outdated.
     */
    public boolean update(Updatable updatable) {
        if (outdatedPlugins.contains(updatable)) {
            downloadManager.queue(updatable);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update all outdated Updatables tracked.
     */
    public void updateAll() {
        for (Updatable updatable : outdatedPlugins) {
            update(updatable);
        }
    }

    protected void finish(Updatable updatable, DownloadResult result) {
        outdatedPlugins.remove(updatable);
        updatablesInQueue.remove(updatable);

        DownloadResult finalResult = result;
        if (updatable.hashComparer != null) {
            if (!updatable.hashComparer.checkIfEquals()) {
                UpdaterLogger.getLogger().warn("The file hash of the downloaded file did not match the hash provided online for " + updatable);
                finalResult = DownloadResult.HASH_FAIl;
            }
        }

        UpdaterLogger.getLogger().info("Finished download for " + updatable + " with result: " + finalResult);
    }
}

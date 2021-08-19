package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.config.UpdaterConfiguration;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UpdateManager {

    // todo: make sure we keep track of ones that have been updated, or remove it from the updatables list

    /**
     * The {@link DownloadManager} to use for downloading new versions of plugins.
     */
    private final DownloadManager downloadManager;

    private final Map<Updatable, Status> registry = new HashMap<>();

    /**
     * All tracked plugins
     */
    private final Set<Updatable> updatables = new HashSet<>();

    /**
     * Plugins that are outdated and must be updated
     */
    private Set<Updatable> outdatedUpdatables = new HashSet<>();

    /**
     * Plugins that are in the queue for download or are being downloaded
     */
    private final Set<Updatable> updatablesInQueue = new HashSet<>();

    private final UpdaterConfiguration config;

    public UpdateManager(Path defaultDownloadLocation, UpdaterScheduler scheduler, UpdaterConfiguration config) {
        this.config = config;
        this.downloadManager = new DownloadManager(this, scheduler, config.getDownloadTimeLimit());
        UpdaterLogger logger = UpdaterLogger.getLogger();

        // If we must schedule a checker to check for updates on an interval
        boolean updateCheckerRequired = false;
        for (PluginId pluginId : PluginId.values()) {
            if (pluginId.isEnable()) {
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

                pluginId.setBranch(branch);

                // For age comparer
                BuildNumber buildNumber = new BuildNumber(Integer.parseInt(buildNumberString));
                JenkinsBuildProvider buildProvider;
                try {
                    buildProvider = new JenkinsBuildProvider(pluginId.getLatestBuildNumber());
                } catch (MalformedURLException e) {
                    UpdaterLogger.getLogger().error("Failed to create build number checker for " + pluginId + ". Not updating.");
                    e.printStackTrace();
                    continue;
                }

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

                boolean autoCheck = pluginId.isAutoCheck();
                if (autoCheck) {
                    updateCheckerRequired = true;
                }

                register(new Updatable(
                        pluginId.name(),
                        new IdentityComparer<>(buildNumber, buildProvider),
                        hashComparer,
                        pluginId.getLatestFileLink(),
                        defaultDownloadLocation,
                        autoCheck,
                        pluginId.isAutoUpdate()));
            }
        }

        // Load extra stuff from the config if we wanted, I guess

        // Check for updates on a schedule, if at least one updatable requires it
        if (updateCheckerRequired) {
            scheduleUpdateChecker(scheduler, config.getAutoUpdateInterval());
        }
    }

    /**
     * Register an {@link Updatable} to be updated
     * @param updatable The {@link Updatable} to be updated
     */
    public void register(Updatable updatable) {
        registry.put(updatable, Status.CURRENT);
    }


    /**
     * Potentially blocking
     */
    public boolean isOutdated(Updatable updatable) {
        Status status = Objects.requireNonNull(registry.get(updatable));
        switch (status) {
            case CURRENT:
                return !updatable.identityComparer.checkIfEquals();
            case OUTDATED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Blocking
     */
    protected void finish(Updatable updatable, DownloadResult result) {
        if (registry.get(updatable) != Status.DOWNLOADING) {
            throw new IllegalStateException("Cannot finish an Updatable if its current status is not DOWNLOADING");
        }
        UpdaterLogger logger = UpdaterLogger.getLogger();

        if (updatable.hashComparer == null) {
            if (result == DownloadResult.SUCCESS) {
                // cant check hash, but the download result is success
                Objects.requireNonNull(registry.replace(updatable, Status.DOWNLOADED));
                return;
            }
        } else {
            if (updatable.hashComparer.checkIfEquals()) {
                // hash is correct
                Objects.requireNonNull(registry.replace(updatable, Status.DOWNLOADED));
                return;
            }
        }

        // Hash is not correct, or cannot check hash and there was a fail
        if (config.isDeleteOnFail()) {
            UpdaterLogger.getLogger().warn("The file hash of the downloaded file did not match the hash provided online for " + updatable + ". Deleting file.");
            try {
                Files.deleteIfExists(updatable.outputFile);
            } catch (IOException e) {
                logger.error("Failed to delete failed download file of " + updatable);
                e.printStackTrace();
            }
        }
    }

    private void scheduleUpdateChecker(UpdaterScheduler scheduler, long interval) {
        scheduler.schedule(() -> {
            List<String> autoDownloads = new ArrayList<>();
            for (Updatable updatable : updatables) {
                // Only check if it is not known to be outdated, and if it should be checked automatically
                if (!outdatedUpdatables.contains(updatable) && updatable.autoCheck) {
                    // We should check if it needs an update
                    if (!updatable.identityComparer.checkIfEquals()) {
                        // It is outdated
                        outdatedUpdatables.add(updatable);
                        if (updatable.autoUpdate) {
                            autoDownloads.add(updatable.toString());
                            updatablesInQueue.add(updatable);
                            downloadManager.queue(updatable);
                        }
                    }
                }
            }

            // todo: also send messages to players with the permission
            if (!outdatedUpdatables.isEmpty()) {
                UpdaterLogger logger = UpdaterLogger.getLogger();
                logger.info("The following updatables are outdated: " + outdatedUpdatables.toString().substring(0, outdatedUpdatables.size()));
                if (!autoDownloads.isEmpty()) {
                    logger.info("The following updatables are set to download automatically and have been queued for download: " + autoDownloads.toString().substring(0, autoDownloads.size()));
                }
            }

        }, true, 0L, interval, TimeUnit.HOURS);
    }
}

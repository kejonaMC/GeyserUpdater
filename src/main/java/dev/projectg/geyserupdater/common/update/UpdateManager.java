package dev.projectg.geyserupdater.common.update;

import dev.projectg.geyserupdater.common.config.UpdaterConfiguration;
import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import dev.projectg.geyserupdater.common.update.age.IdentityComparer;
import dev.projectg.geyserupdater.common.update.age.provider.FileHashProvider;
import dev.projectg.geyserupdater.common.update.age.provider.JenkinsBuildProvider;
import dev.projectg.geyserupdater.common.update.age.provider.JenkinsHashProvider;
import dev.projectg.geyserupdater.common.update.age.type.BuildNumber;
import dev.projectg.geyserupdater.common.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UpdateManager {

    /**
     * The {@link DownloadManager} to use for downloading new versions of plugins.
     */
    private final DownloadManager downloadManager;
    private final UpdaterScheduler scheduler;
    private final UpdaterConfiguration config;

    private final Map<Updatable, UpdateStatus> registry = new HashMap<>();

    private boolean isAutoChecking = false;

    public UpdateManager(Path defaultDownloadLocation, UpdaterScheduler scheduler, UpdaterConfiguration config) {
        this.scheduler = scheduler;
        this.config = config;
        this.downloadManager = new DownloadManager(this, scheduler, config.getDownloadTimeLimit());
        UpdaterLogger logger = UpdaterLogger.getLogger();

        // If we must schedule a checker to check for updates on an interval
        boolean updateCheckerRequired = false;
        for (PluginId pluginId : PluginId.values()) {
            if (pluginId.isEnable()) {
                // Get the git.properties
                InputStream is = pluginId.getPluginClass().getClassLoader().getResourceAsStream("git.properties");
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
        registry.put(updatable, UpdateStatus.UNKNOWN);
        if (updatable.autoCheck && !isAutoChecking) {
            scheduleUpdateChecker(scheduler, config.getAutoUpdateInterval());
        }
    }

    public boolean isTracked(Updatable updatable) {
        return registry.containsKey(updatable);
    }

    /**
     * Potentially blocking
     */
    public boolean isOutdated(Updatable updatable) {
        if (!isTracked(updatable)) {
            throw new IllegalArgumentException("Updatable must be tracked by the UpdateManager in order to check if it is outdated!");
        }
        UpdateStatus status = Objects.requireNonNull(registry.get(updatable));
        switch (status) {
            case UNKNOWN:
                // It was latest last we checked, or we haven't checker before
                return !updatable.identityComparer.checkIfEquals();
            case OUTDATED:
                return true;
            default:
                // A new version is either being downloaded or has been already
                return false;
        }
    }

    /**
     * Blocking
     */
    protected void finish(Updatable updatable, DownloadResult result) {
        if (registry.get(updatable) != UpdateStatus.DOWNLOADING) {
            throw new IllegalStateException("Cannot finish an Updatable if its current status is not DOWNLOADING");
        }
        UpdaterLogger logger = UpdaterLogger.getLogger();

        if (updatable.hashComparer == null) {
            if (result == DownloadResult.SUCCESS) {
                // cant check hash, but the download result is success
                registry.put(updatable, UpdateStatus.DOWNLOADED);
                return;
            }
        } else if (updatable.hashComparer.checkIfEquals()) {
            // hash is correct
            registry.put(updatable, UpdateStatus.DOWNLOADED);
            return;
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
            List<String> outdatedOnes = new ArrayList<>();
            List<String> autoDownloads = new ArrayList<>();

            for (Updatable updatable : registry.keySet()) {
                // Only check if it is not known to be outdated, and if it should be checked automatically
                if (registry.get(updatable) == UpdateStatus.UNKNOWN && updatable.autoCheck) {
                    // We should check if it needs an update
                    if (!updatable.identityComparer.checkIfEquals()) {
                        // It is outdated
                        registry.put(updatable, UpdateStatus.OUTDATED);
                        outdatedOnes.add(updatable.toString());
                        if (updatable.autoUpdate) {
                            downloadManager.queue(updatable);
                            autoDownloads.add(updatable.toString());
                        }
                    }
                }
            }

            // todo: also send messages to players with the permission
            if (!outdatedOnes.isEmpty()) {
                UpdaterLogger logger = UpdaterLogger.getLogger();
                logger.info("The following updatables are outdated: " + outdatedOnes.toString().substring(0, outdatedOnes.size()));
                if (!autoDownloads.isEmpty()) {
                    logger.info("The following updatables are set to download automatically and have been queued for download: " + autoDownloads.toString().substring(0, autoDownloads.size()));
                }
            }

        }, true, 0L, interval, TimeUnit.HOURS);

        isAutoChecking = true;
    }
}

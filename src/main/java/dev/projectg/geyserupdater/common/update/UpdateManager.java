package dev.projectg.geyserupdater.common.update;

import com.google.common.collect.ImmutableMap;
import dev.projectg.geyserupdater.common.config.UpdaterConfiguration;
import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.scheduler.UpdaterScheduler;
import dev.projectg.geyserupdater.common.update.identity.IdentityComparer;
import dev.projectg.geyserupdater.common.update.identity.provider.FileHashProvider;
import dev.projectg.geyserupdater.common.update.identity.provider.JenkinsBuildProvider;
import dev.projectg.geyserupdater.common.update.identity.provider.JenkinsHashProvider;
import dev.projectg.geyserupdater.common.update.identity.type.BuildNumber;
import dev.projectg.geyserupdater.common.util.WebUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

                // The download location, used for both downloading and hash checking
                Path downloadLocation = defaultDownloadLocation.resolve(Paths.get(WebUtils.getFileName(pluginId.getLatestFileLink())));

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
                IdentityComparer<?, ?, ?> hashComparer = null;
                try {
                    FileHashProvider localHashProvider = new FileHashProvider(downloadLocation);
                    JenkinsHashProvider jenkinsHashProvider = new JenkinsHashProvider(pluginId.getLatestFileLink() + "/*fingerprint*/");
                    hashComparer = new IdentityComparer<>(localHashProvider, jenkinsHashProvider);
                } catch (MalformedURLException e) {
                    UpdaterLogger.getLogger().error("Failure while getting location of file for " + pluginId.name() + ". It will be possible to update it, but not to compare file hashes.");
                    e.printStackTrace();
                }

                register(new Updatable(
                        pluginId.name(),
                        new IdentityComparer<>(buildNumber, buildProvider),
                        hashComparer,
                        pluginId.getLatestFileLink(),
                        downloadLocation,
                        pluginId.isAutoCheck(),
                        pluginId.isAutoUpdate()));
            }
        }

        // Load extra stuff from the config if we wanted, I guess
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
                logger.info("Successfully downloaded update for: " + updatable);
                registry.put(updatable, UpdateStatus.DOWNLOADED);
                return;
            }
        } else {
            Object downloadHash = updatable.hashComparer.callLocalValue();
            Object anticipatedHash = updatable.hashComparer.callExternalValue();
            if (downloadHash == null) {
                logger.error("Failed to find hash for downloaded update of: " + updatable);
            } else if (anticipatedHash == null) {
                logger.error("Failed to find anticipated hash for downloaded update of: " + updatable);
            } else if (downloadHash.equals(anticipatedHash)) {
                // hash is "correct"
                logger.info("Successfully downloaded update for: " + updatable);
                logger.debug("Downloaded file hash: <" + downloadHash + ">. Anticipated hash: <" + anticipatedHash + ">");
                registry.put(updatable, UpdateStatus.DOWNLOADED);
                return;
            } else {
                logger.warn("The file hash of the downloaded file did not match the hash provided online for " + updatable);
                logger.warn("Downloaded file hash: <" + downloadHash + ">. Anticipated hash: <" + anticipatedHash + ">");
            }
        }

        // Hash is not correct, or cannot check hash and there was a fail
        if (config.isDeleteOnFail()) {
            logger.warn("Deleting failed download.");
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
                            registry.put(updatable, UpdateStatus.DOWNLOADING);
                            downloadManager.queue(updatable);
                            autoDownloads.add(updatable.toString());
                        }
                    }
                }
            }

            // todo: also send messages to players with the permission
            if (!outdatedOnes.isEmpty()) {
                UpdaterLogger logger = UpdaterLogger.getLogger();
                logger.info("The following updatables are outdated: " + outdatedOnes);
                if (!autoDownloads.isEmpty()) {
                    logger.info("The following updatables are set to download automatically and have been queued for download: " + autoDownloads);
                }
            }

        }, true, 0L, interval, TimeUnit.HOURS);

        isAutoChecking = true;
    }

    /**
     * @return a {@link Map#keySet()} of the tracked Updatable registry
     */
    public Set<Updatable> getTrackedUpdatables() {
        return registry.keySet();
    }

    public void shutdown() {
        List<Updatable> cancelled = downloadManager.shutdown();
        if (!cancelled.isEmpty()) {
            UpdaterLogger.getLogger().info("Cancelled the following downloads because of a shutdown: " + cancelled.stream().map(Updatable::toString).collect(Collectors.joining(", ")));
        }
    }
}

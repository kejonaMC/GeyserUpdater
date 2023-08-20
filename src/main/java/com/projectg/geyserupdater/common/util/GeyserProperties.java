package com.projectg.geyserupdater.common.util;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import org.geysermc.geyser.GeyserImpl;

import java.io.IOException;
import java.util.Properties;

public class GeyserProperties {

    // todo: a check to see if the local git branch is available on the CI (that knows if it failed because of a bad connection or not)
    // todo: proper error handling

    /**
     * Compare the local build number to the latest build number on Geyser CI
     *
     * @return true if local build number equals latest build number on Geyser CI
     * @throws IOException if it fails to fetch either build number
     */
    public static boolean isLatestBuild() throws IOException {
        UpdaterLogger.getLogger().debug("Running isLatestBuild()");
        // Removed get branch since current Geyser endpoints do not yet support it
        // getLatestGeyserBuildNumberFromDownloadPage(getGeyserGitPropertiesValue("git.branch"));
        int downloadPageBuildNumber = getLatestGeyserBuildNumberFromDownloadPage();
        int localBuildNumber = Integer.parseInt(getGeyserGitPropertiesValue("git.build.number"));
        // Compare build numbers.
        // We treat higher build numbers as "out of date" here because Geyser's build numbers have been (accidentally) reset in the past.
        // Self-compiled builds of Geyser simply do not have a `git.build.number` value, so it is /very/ unlikely that a user will ever have a Git build number higher than upstream anyway.
        return downloadPageBuildNumber == localBuildNumber;
    }

    /** Query the git properties of Geyser
     *
     * @param propertyKey the key of property to query
     * @return the value of the property
     * @throws IOException if failed to load the Geyser git properties
     */
    public static String getGeyserGitPropertiesValue(String propertyKey) throws IOException {
        UpdaterLogger.getLogger().debug("Running getGeyserGitPropertiesValue()");
        Properties gitProperties = new Properties();
        gitProperties.load(GeyserImpl.getInstance().getBootstrap().getResource("git.properties"));
        return gitProperties.getProperty(propertyKey);
    }

    /** Get the latest build number of a given branch of Geyser from Geyser download page.
     *
     * @return the latest build number from Geyser API
     */
    public static int getLatestGeyserBuildNumberFromDownloadPage() {
        UpdaterLogger.getLogger().debug("Running getLatestGeyserBuildNumberFromJenkins()");
        return new GeyserApi().endPoints().build;
    }
}
package com.projectg.geyserupdater.common.util;

import com.projectg.geyserupdater.common.update.JenkinsUpdatable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    /**
     * Compare the local build number to the latest build number on Geyser CI
     *
     * @return true if local build number equals latest build number on Geyser CI
     * @throws IOException if it fails to fetch either build number
     */
    public static boolean isLatestBuild(JenkinsUpdatable updatable) throws IOException {
        int jenkinsBuildNumber = WebUtils.getLatestGeyserBuildNumberFromJenkins(updatable.branch);
        int localBuildNumber = updatable.buildNumber;
        // Compare build numbers.
        // We treat higher build numbers as "out of date" here because Geyser's build numbers have been (accidentally) reset in the past.
        // Self-compiled builds of Geyser simply do not have a `git.build.number` value, so it is /very/ unlikely that a user will ever have a Git build number higher than upstream anyway.
        return jenkinsBuildNumber == localBuildNumber;
    }


    public static String getBranch(Properties properties) {
        properties.get
    }

    public static Properties getProperties(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }
}

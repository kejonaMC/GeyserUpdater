package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.util.WebUtils;

import java.io.IOException;
import java.util.Objects;

public class Updatable {

    private final String projectLink;
    private final String branch;
    private final int localBuildNumber;

    public Updatable(String artifactLink, PluginId pluginId) {
        Objects.requireNonNull(artifactLink);
        Objects.requireNonNull(pluginId);



    }


    /**
     * Compare the local build number to the latest build number on Geyser CI
     *
     * @return true if local build number equals latest build number on Geyser CI
     * @throws IOException if it fails to fetch either build number
     */
    public boolean isLatestBuild() throws IOException {
        int jenkinsBuildNumber = WebUtils.getLatestGeyserBuildNumberFromJenkins(projectLink + "/" + branch);
        // Compare build numbers.
        // We treat higher build numbers as "out of date" here because Geyser's build numbers have been (accidentally) reset in the past.
        // Self-compiled builds of Geyser simply do not have a `git.build.number` value, so it is /very/ unlikely that a user will ever have a Git build number higher than upstream anyway.
        return jenkinsBuildNumber == localBuildNumber;
    }
}

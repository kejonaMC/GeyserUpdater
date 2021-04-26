package com.alysaa.geyserupdater.common.util;

import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GeyserProperties {

    /**
     * Compare the local build number to the latest build number on Geyser CI
     *
     * @return true if local build number equals latest build number on Geyser CI
     * @throws IOException if it fails to fetch either build number
     */
    public static boolean isLatestBuild() throws IOException {
        int jenkinsBuildNumber = getLatestGeyserBuildNumberFromJenkins(getGeyserGitPropertiesValue("git.branch"));
        int localBuildNumber = Integer.parseInt(getGeyserGitPropertiesValue("git.build.number"));
        // Compare build numbers.
        // We treat higher build numbers as out of date here because Geyser's build numbers have been (accidentally) reset in the past.
        // TODO: Verify if non-Jenkins (self-compiled) builds have a build number associated with them.
        return jenkinsBuildNumber == localBuildNumber;
    }

    /** Query the git properties of Geyser
     *
     * @param propertyKey the key of property to query
     * @return the value of the property
     * @throws IOException if failed to load the Geyser git properties
     */
    public static String getGeyserGitPropertiesValue(String propertyKey) throws IOException {
        Properties gitProperties = new Properties();
        gitProperties.load(FileUtils.getResource("git.properties"));
        return gitProperties.getProperty(propertyKey);
    }

    /** Get the latest build number of a given branch of Geyser from jenkins CI
     *
     * @param gitBranch the branch to query
     * @return the latest build number
     * @throws UnsupportedEncodingException if failed to encode the given gitBranch
     */
    public static int getLatestGeyserBuildNumberFromJenkins(String gitBranch) throws UnsupportedEncodingException {
        String buildXMLContents = WebUtils.getBody("https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitBranch, StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        return Integer.parseInt(buildXMLContents.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
    }
}
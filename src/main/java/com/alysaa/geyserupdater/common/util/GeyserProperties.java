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
        int jenkinsBuildNumber = jenkinsLatestBuildNumber(queryGitProperties("git.branch"));
        int localBuildNumber = Integer.parseInt(queryGitProperties("git.build.number"));
        // Compare build numbers.
        return jenkinsBuildNumber == localBuildNumber;
    }

    /** Query the git properties of Geyser
     *
     * @param propertyKey the key of property to query
     * @return the value of the property
     * @throws IOException if failed to load the Geyser git properties
     */
    public static String queryGitProperties(String propertyKey) throws IOException {
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        return gitProp.getProperty(propertyKey);
    }

    /** Get the latest build number of a given branch of Geyser from jenkins CI
     *
     * @param gitBranch the branch to query
     * @return the latest build number
     * @throws UnsupportedEncodingException if failed to encode the given gitBranch
     */
    public static int jenkinsLatestBuildNumber(String gitBranch) throws UnsupportedEncodingException {
        String XML = WebUtils.getBody("https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitBranch, StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        return Integer.parseInt(XML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
    }
}
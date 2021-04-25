package com.alysaa.geyserupdater.common.util;

import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class CheckBuildNum {
    public static Properties getGeyserGitProperties() {
        try {
            Properties gitProperties = new Properties();
            gitProperties.load(FileUtils.getResource("git.properties"));
            return gitProperties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getLatestGeyserBuildNumber() {
        String buildXMLContents = null;
        try {
            buildXMLContents = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(getGeyserGitProperties().getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return (buildXMLContents.startsWith("<buildNumber>")) ? Integer.parseInt(buildXMLContents.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim()) : 0;
    }

    public static int getCurrentGeyserBuildNumber() {
        return Integer.parseInt(getGeyserGitProperties().getProperty("git.build.number"));
    }
}
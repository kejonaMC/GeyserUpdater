package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.velocity.VelocityUpdater;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class BuildNumChecker {

    public static void checkBuildNumberVelocity() {
        Properties gitProp = new Properties();
        try {
            gitProp.load(FileUtils.getResource("git.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String buildXML = null;
        try {
            buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                VelocityUpdater.logger.warn("Geyser is on the latest build!");
                for (Player all : VelocityUpdater.server.getAllPlayers()) {
                    if (all.hasPermission("gupdater.geyserupdate")) {
                        all.sendMessage(Component.text("[GeyserUpdater] Geyser is on the latest build!"));
                    }
                }
            } else {
                VelocityUpdater.logger.warn("Current running Geyser build is outdated, attempting to download latest!");
                for (Player all : VelocityUpdater.server.getAllPlayers()) {
                    if (all.hasPermission("gupdater.geyserupdate")) {
                        all.sendMessage(Component.text("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!"));
                    }
                }
                GeyserVeloDownload.downloadGeyser();
            }
        }
    }
}

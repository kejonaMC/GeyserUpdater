package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownload;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownload;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Logger;

public class CheckBuildNum {

    public static void checkBuildNumberSpigot() throws IOException {
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                SpigotUpdater.plugin.getLogger().info("Geyser is on the latest build!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("gupdater.geyserupdate"))
                        player.sendMessage("[GeyserUpdater] Geyser is on the latest build!");
                }
            } else {
                SpigotUpdater.plugin.getLogger().info("Current running Geyser build is outdated, attempting to download latest!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("gupdater.geyserupdate"))
                        player.sendMessage("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!");
                }
                GeyserSpigotDownload.downloadGeyser();
            }
        }
    }
    public static void checkBuildNumberBungee() throws IOException {
        Logger logger = BungeeUpdater.plugin.getLogger();
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                logger.info("[GeyserUpdater] Geyser is on the latest build!");
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.hasPermission("gupdater.geyserupdate")) {
                        all.sendMessage(new TextComponent("[GeyserUpdater] Geyser is on the latest build!"));
                    }
                }
            } else {
                logger.info("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!");
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.hasPermission("gupdater.geyserupdate")) {
                        all.sendMessage(new TextComponent("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!"));
                    }
                    GeyserBungeeDownload.downloadGeyser();
                }
            }
        }
    }
}

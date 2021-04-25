package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownload;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownload;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;
import com.alysaa.geyserupdater.velocity.util.GeyserVelocityDownload;

import net.kyori.adventure.text.Component;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class CheckBuildNum {
    public static void checkBuildNumberSpigot() {
        // Compare build numbers.
        if (getCurrentGeyserBuildNumber() >= getLatestGeyserBuildNumber()) {
            SpigotUpdater.plugin.getLogger().info("Geyser is on the latest build!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("gupdater.geyserupdate")) {
                    player.sendMessage("[GeyserUpdater] Geyser is on the latest build!");
                }
            }
        } else {
            SpigotUpdater.plugin.getLogger().info("Current running Geyser build is outdated, attempting to download latest!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("gupdater.geyserupdate")) {
                    player.sendMessage("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!");
                }
            }
            GeyserSpigotDownload.downloadGeyser();
        }
    }

    public static void checkBuildNumberBungee() {
        // Compare build numbers.
        if (getCurrentGeyserBuildNumber() >= getLatestGeyserBuildNumber()) {
            BungeeUpdater.plugin.getLogger().info("[GeyserUpdater] Geyser is on the latest build!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent("[GeyserUpdater] Geyser is on the latest build!"));
                }
            }
        } else {
            BungeeUpdater.plugin.getLogger().info("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!");
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(new TextComponent("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!"));
                }
            }
            GeyserBungeeDownload.downloadGeyser();
        }
    }

    public static void checkBuildNumberVelocity() {
        // Compare build numbers.
        if (getCurrentGeyserBuildNumber() >= getLatestGeyserBuildNumber()) {
            VelocityUpdater.logger.warn("Geyser is on the latest build!");
            for (com.velocitypowered.api.proxy.Player all : VelocityUpdater.server.getAllPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(Component.text("[GeyserUpdater] Geyser is on the latest build!"));
                }
            }
        } else {
            VelocityUpdater.logger.warn("Current running Geyser build is outdated, attempting to download latest!");
            for (com.velocitypowered.api.proxy.Player all : VelocityUpdater.server.getAllPlayers()) {
                if (all.hasPermission("gupdater.geyserupdate")) {
                    all.sendMessage(Component.text("[GeyserUpdater] Current running Geyser build is outdated, attempting to download latest!"));
                }
            }
            GeyserVelocityDownload.downloadGeyser();
        }
    }

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
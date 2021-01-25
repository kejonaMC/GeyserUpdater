package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.bungee.util.GeyserBungeeDownload;
import com.alysaa.geyserupdater.spigot.util.GeyserSpigotDownload;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class CheckBuildNum {

    public static void CheckBuildNumberSpigot() throws IOException {
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                System.out.println("[GeyserUpdater] Geyser is on the latest build!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp())
                        player.sendMessage("[GeyserUpdater] Geyser is on the latest build!");
                }
            } else {
                System.out.println("[GeyserUpdater] Current running Geyser build is outdated!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp())
                        player.sendMessage("[GeyserUpdater] Current running Geyser build is outdated");
                }
                Path p = Paths.get("plugins/Geyser-BungeeCord.jar");
                boolean exists = Files.exists(p);
                boolean notExists = Files.notExists(p);

                if (exists) {
                    GeyserBungeeDownload.GeyserDownload();
                } else if (notExists) {
                    GeyserSpigotDownload.GeyserDownload();
                }
            }
        }
    }

    public static void CheckBuildNumberSpigotAuto() throws IOException {
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                System.out.println("[GeyserUpdater] Geyser is on the latest build!");
            } else {
                System.out.println("[GeyserUpdater] Current running Geyser build is outdated!");
                Path p = Paths.get("plugins/Geyser-BungeeCord.jar");
                boolean exists1 = Files.exists(p);
                boolean notExists1 = Files.notExists(p);

                if (exists1) {
                    GeyserBungeeDownload.GeyserDownload();
                } else if (notExists1) {
                    GeyserSpigotDownload.GeyserDownload();
                }
            }
        }
    }

    public static void CheckBuildNumberBungeeAuto() throws IOException {
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                System.out.println("[GeyserUpdater] Geyser is on the latest build!");
            } else {
                System.out.println("[GeyserUpdater] Current running Geyser build is outdated!");
                Path p = Paths.get("plugins/Geyser-BungeeCord.jar");
                boolean exists1 = Files.exists(p);
                boolean notExists1 = Files.notExists(p);

                if (exists1) {
                    GeyserBungeeDownload.GeyserDownload();
                } else if (notExists1) {
                    GeyserSpigotDownload.GeyserDownload();
                }
            }
        }
    }

    public static void CheckBuildNumberBungee() throws IOException {
        Properties gitProp = new Properties();
        gitProp.load(FileUtils.getResource("git.properties"));
        String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        if (buildXML.startsWith("<buildNumber>")) {
            int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
            int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
            // Compare build numbers.
            if (latestBuildNum == buildNum) {
                System.out.println("[GeyserUpdater] Geyser is on the latest build!");
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.hasPermission("gupdater.geyserupdate")) ;
                    {
                        all.sendMessage("[GeyserUpdater] Geyser is on the latest build!");
                    }
                }
            } else {
                System.out.println("[GeyserUpdater] Current running Geyser build is outdated!");
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.hasPermission("gupdater.geyserupdate")) ;
                    {
                        all.sendMessage("[GeyserUpdater] Current running Geyser build is outdated!");
                    }
                    Path p = Paths.get("plugins/Geyser-BungeeCord.jar");
                    boolean exists1 = Files.exists(p);
                    boolean notExists1 = Files.notExists(p);

                    if (exists1) {
                        GeyserBungeeDownload.GeyserDownload();
                    } else if (notExists1) {
                        GeyserSpigotDownload.GeyserDownload();
                    }
                }
            }
        }
    }
}
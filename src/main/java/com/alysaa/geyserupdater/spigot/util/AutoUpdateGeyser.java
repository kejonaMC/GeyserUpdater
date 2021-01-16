package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AutoUpdateGeyser extends BukkitRunnable {
    public AutoUpdateGeyser(SpigotUpdater plugin) {}

    @Override
    public void run() {
        System.out.println("[GeyserUpdater] Checking current Geyser version!");
        try {
            Properties gitProp = new Properties();
            gitProp.load(FileUtils.getResource("git.properties"));
            String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
            if (buildXML.startsWith("<buildNumber>")) {
                int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
                int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
                // Compare build numbers.
                if (latestBuildNum == buildNum) {
                    System.out.println("[GeyserUpdater] Geyser is already on the latest build!");
                } else {
                    System.out.println("[GeyserUpdater] Geyser build is outdated. now downloading latest build!");
                    try {
                        // Download build
                        OutputStream os = null;
                        InputStream is = null;
                        String fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/spigot/target/Geyser-Spigot.jar";
                        String outputPath = ("plugins/update/Geyser-Spigot.jar");
                        try {
                            // create a url object
                            URL url = new URL(fileUrl);
                            // connection to the file
                            URLConnection connection = url.openConnection();
                            // get input stream to the file
                            is = connection.getInputStream();
                            // get output stream to download file
                            os = new FileOutputStream(outputPath);
                            final byte[] b = new byte[2048];
                            int length;
                            // read from input stream and write to output stream
                            while ((length = is.read(b)) != -1) {
                                os.write(b, 0, length);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            // close streams
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("[GeyserUpdater] Geyser has been updated! Changes will take place once server has been restarted!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
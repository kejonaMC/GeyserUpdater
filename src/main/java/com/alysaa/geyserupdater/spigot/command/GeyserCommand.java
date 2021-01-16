package com.alysaa.geyserupdater.spigot.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GeyserCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("geyserupdate") && player.hasPermission("gupdater.geyserupdate")) {
                sender.sendMessage(ChatColor.GOLD + "[GeyserUpdater] Checking current Geyser version!");
                try {
                    Properties gitProp = new Properties();
                    gitProp.load(FileUtils.getResource("git.properties"));
                    String buildXML = WebUtils.getBody("https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
                    if (buildXML.startsWith("<buildNumber>")) {
                        int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
                        int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
                        // Compare build numbers.
                        if (latestBuildNum == buildNum) {
                            sender.sendMessage("[GeyserUpdater] Geyser is on the latest build!");
                        } else {
                            sender.sendMessage("[GeyserUpdater] Geyser build is outdated. now downloading latest build!");
                            // Download Build
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
                            sender.sendMessage("[GeyserUpdater] Geyser has been updated! Changes will take place once the server has been restarted!");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
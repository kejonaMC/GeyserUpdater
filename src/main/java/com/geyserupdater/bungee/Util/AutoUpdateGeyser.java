package com.geyserupdater.bungee.Util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.WebUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AutoUpdateGeyser {
    public static void checkUpdate(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.GOLD + "[Geyser-Updater] Checking current Geyser version"));
        try {
            Properties gitProp = new Properties();
            gitProp.load(FileUtils.getResource("git.properties"));
            String buildXML = WebUtils.getBody("https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/" + URLEncoder.encode(gitProp.getProperty("git.branch"), StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
            if (buildXML.startsWith("<buildNumber>")) {
                int latestBuildNum = Integer.parseInt(buildXML.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
                int buildNum = Integer.parseInt(gitProp.getProperty("git.build.number"));
                // Compare build numbers.
                if (latestBuildNum == buildNum) {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "[Geyser-BungeeCord-Updater] Geyser is on the latest build!"));
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "[Geyser-BungeeCord-Updater] Geyser build is outdated. Geyser is now downloading latest build!"));
                    try {
                        OutputStream os = null;
                        InputStream is = null;
                        String fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/bungeecord/target/Geyser-BungeeCord.jar";
                        String outputPath = ("plugins/Geyser-BungeeCord-Updater/BuildUpdate/Geyser-BungeeCord.jar");
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

                            sender.sendMessage(new TextComponent(ChatColor.GREEN + "[Geyser-BungeeCord-Updater] Geyser has been updated. Changes take place once the server has been restarted!"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkFile() {
        Path p = Paths.get("plugins/Geyser-BungeeCord-Updater/BuildUpdate/Geyser-BungeeCord.jar");
        boolean exists = Files.exists(p);
        boolean notExists = Files.notExists(p);

        if (exists) {
            System.out.println("[Geyser-BungeeCord-Updater] New update is available! Bungeecord needs to be restarted before the updated build loads!");
        } else if (notExists) {
            System.out.println("[Geyser-BungeeCord-Updater] There is no updated build yet in the Geyser-Updater folder! this can happen because you are already on the latest build, if not you can download it with the geyserupdate command or if you have enabled auto updating you will need to wait! ");
        } else {
            System.out.println("[Geyser-BungeeCord-Updater] Oops something went wrong in the Build folder in the Geyser-Updater!");
        }
    }
}
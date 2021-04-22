package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class GeyserSpigotDownload {
    public static void downloadGeyser() {
        Runnable runnable = () -> {
            try {
                OutputStream os = null;
                InputStream is = null;
                String fileUrl = "https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/spigot/target/Geyser-Spigot.jar";
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
                // Check if the file was downloaded successfully
                boolean downloadSuccess = FileUtils.checkFile("plugins/update/Geyser-Spigot.jar", false);
                if (!downloadSuccess) {
                    SpigotUpdater.plugin.getLogger().info("Failed to download a newer version of Geyser!");
                }
                // Restart the server if the option is enabled
                if (SpigotUpdater.plugin.getConfig().getBoolean("Auto-Restart-Server") && downloadSuccess) {
                    SpigotUpdater.plugin.getLogger().info("A new version of Geyser has been downloaded, the server will restart in 10 Seconds!");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', SpigotUpdater.getPlugin().getConfig().getString("Restart-Message-Players")));
                    }
                    Thread.sleep(10000);
                    Bukkit.spigot().restart();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }
}
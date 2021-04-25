package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
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
                boolean downloadSuccess = CheckBuildFile.checkSpigotFile(false);
                // Restart the server if the option is enabled
                if (SpigotUpdater.plugin.getConfig().getBoolean("Auto-Restart-Server") && downloadSuccess) {
                    SpigotUpdater.plugin.getLogger().info("The Server will restart in 10 seconds!");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', SpigotUpdater.getPlugin().getConfig().getString("Restart-Message-Players")));
                    }
                    Thread.sleep(10000);
                    Object spigotServer = null;
                    try {
                        spigotServer = SpigotUpdater.plugin.getServer().getClass().getMethod("spigot").invoke(SpigotUpdater.plugin.getServer());
                    } catch (NoSuchMethodException e) {
                        SpigotUpdater.plugin.getLogger().severe("You are not running Spigot (or a fork of it, such as Paper)! GeyserUpdater cannot automatically restart your server!");
                        e.printStackTrace();
                        return;
                    }
                    Method restartMethod = spigotServer.getClass().getMethod("restart");
                    restartMethod.setAccessible(true);
                    restartMethod.invoke(spigotServer);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                SpigotUpdater.plugin.getLogger().severe("Your server version is too old to be able to be automatically restarted!");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }
}
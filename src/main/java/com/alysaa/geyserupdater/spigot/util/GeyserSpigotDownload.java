package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.alysaa.geyserupdater.spigot.SpigotUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class GeyserSpigotDownload {
    public static void GeyserDownload() {
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
                    CheckBuildFile.CheckSpigotFile();
                }
            }
            if (SpigotUpdater.plugin.getConfig().getBoolean("EnableAutoRestart")) {
                try {
                    System.out.println("[GeyserUpdater] The Server will restart in 10 seconds!");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp())
                            player.sendMessage("[GeyserUpdater] The Server will restart in 10 seconds!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().shutdown();
    }
}

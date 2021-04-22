package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.alysaa.geyserupdater.common.util.FileUtils;
import com.alysaa.geyserupdater.velocity.VelocityUpdater;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class GeyserVeloDownload {
    public static void downloadGeyser() {
        OutputStream os = null;
        InputStream is = null;
        String fileUrl = "https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/velocity/target/Geyser-Velocity.jar";
        String outputPath = ("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar");
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
        boolean downloadSuccess = FileUtils.checkFile("plugins/GeyserUpdater/BuildUpdate/Geyser-Velocity.jar", false);
        if (!downloadSuccess) {
            VelocityUpdater.logger.info("Failed to download a newer version of Geyser!");
        }
        // Restart the server if the option is enabled
        if (VelocityUpdater.configf.getBoolean("Auto-Restart-Server") && downloadSuccess) {
            VelocityUpdater.logger.warn("A new version of Geyser has been downloaded, the server will restart in 10 Seconds!");
            for (Player player : VelocityUpdater.server.getAllPlayers()) {
                player.sendMessage(Component.text(VelocityUpdater.configf.getString("Restart-Message-Players")));
            }
            Runnable runnable = () -> {
                try {
                    Thread.sleep(10000);
                    VelocityUpdater.server.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}


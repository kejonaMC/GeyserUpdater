package com.alysaa.geyserupdater.common.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;


public class ScriptCreator {

    public static void checkSpigotRestart() {
        FileConfiguration spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
        String scriptPath = spigot.getString("settings.restart-script");
        File script = new File(scriptPath);
        //need to add os check on string
        String scriptName;
        if (OSUtils.isWindows()) scriptName = "ServerRestartScript.bat";
        else if (OSUtils.isLinux() || OSUtils.isMac()) scriptName = "./ServerRestartScript.sh";
        else {
            System.out.println("Your OS is not supported for script checking!");
            return;
        }
        spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
        spigot.set("settings.restart-script", scriptName);
        try {
            spigot.save("spigot.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (script.exists())
            System.out.println("[GeyserUpdater] Has detected a restart script.");
        else {
            try {
                URI fileURI;
                fileURI = new URI(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                File jar = new File(fileURI.getPath());
                ScriptCreator.createScript(jar.getName(), false);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void createScript(String jarPath, boolean runLoop) throws IOException {

        File file;
        String extension;
        if (OSUtils.isWindows()) {
            extension = "bat";
        } else if (OSUtils.isLinux() || OSUtils.isMac()) {
            extension = "sh";
        } else {
            System.out.println("Your OS is not supported! We support Linux, Mac, and Windows for automatic script creation!");
            return;
        }
        file = new File("ServerRestartScript." + extension);
        if (!file.exists()) {
            System.out.println("[GeyserUpdater] A custom restart script has been made for you." );
            System.out.println("[GeyserUpdater] You will need to shutdown the server and use our provided restart script.");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);

            if (OSUtils.isWindows()) {
                dos.writeBytes("@echo off\n");
            } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                dos.writeBytes("#!/bin/sh\n");
            }
            // The restart signal from Spigot is being used in the GeyserSpigotDownload class, which means that a loop in this script is not necessary for spigot.
            // GeyserBungeeDownload can only use the stop signal, so a loop must be used to keep the script alive.
            if (runLoop) {
                if (OSUtils.isWindows()) {
                    dos.writeBytes(":restart\n");
                } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                    dos.writeBytes("while true; do\n");
                }
            }
            dos.writeBytes("java -Xmx" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1024 * 1024) + "M -jar "+ jarPath +" nogui\n");
            if (runLoop) {
                if (OSUtils.isWindows()) {
                    dos.writeBytes("timeout 10 && Goto restart\n");
                } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                    dos.writeBytes("echo \"Server stopped, restarting in 10 seconds!\"; sleep 10; done\n");
                }
            }
        }
    }
}
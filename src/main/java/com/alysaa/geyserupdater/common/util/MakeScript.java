package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.common.util.OSUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeScript {

    public static void checkSpigotRestart() {

        FileConfiguration spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
        String scriptPath = spigot.getString("settings.restart-script");
        File script = new File(scriptPath);
        //need to add os check on string
        String scriptName = ("./ServerRestartScript.bat");
        spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
        spigot.set("settings.restart-script",scriptName);
        try {
            spigot.save("spigot.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (script.exists())
            System.out.println("[GeyserUpdater] Has detected a restart script.");
        else
            try {
                URI fileURI;
                fileURI = new URI(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                File jar = new File(fileURI.getPath());
                MakeScript.createScript(jar.getName());
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
    }

    public static void createScript(String jarPath) throws IOException {
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
            System.out.println("[GeyserUpdater] A custom restart script has been made for you, its located in the main server folder. you will need to edit this and also make sure you enable it in spigot.yml!");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            if (OSUtils.isWindows()) {
                dos.writeBytes("@echo off\n");
            } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                dos.writeBytes("#!/bin/sh\n");
            }
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1024 * 1024) + "M -jar "+ jarPath +" nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }


}

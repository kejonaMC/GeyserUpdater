package com.alysaa.geyserupdater.common.util.CreateScript;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeWinBat {
    public static void CreateBungeeWinBat() throws IOException {
        Path p = Paths.get("GeyserUpdaterScript.bat");
        boolean exists = Files.exists(p);
        if (exists) {
        } else {
            System.out.println("[GeyserUpdater] A custom restart script has been made for you, its located in the main server folder. you will need to edit this and also make sure you enable it in spigot.yml!");
            File file = new File("GeyserUpdaterScript.bat");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("@echo off\n");
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1024 * 1024) + "M -jar "+ ProxyServer.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "") +" nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }

    public static void CreateSpigotWinBat() throws IOException {
        Path p = Paths.get("GeyserUpdaterScript.bat");
        boolean exists = Files.exists(p);
        if (exists) {
        } else {
            System.out.println("[GeyserUpdater] A custom restart script has been made for you, its located in the main server folder. you will need to edit this and also make sure you enable it in spigot.yml!");
            File file = new File("GeyserUpdaterScript.bat");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("@echo off\n");
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1024 * 1024) + "M -jar "+ Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "") +" nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }
}

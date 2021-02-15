package com.alysaa.geyserupdater.common.util.CreateScript;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeWinBat {
    public static void CreateBungeeWinBat() throws IOException {
        Path p = Paths.get("startserver.bat");
        boolean exists = Files.exists(p);
        if (exists) {
        } else {
            File file = new File("startserver.bat");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("@echo off\n");
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx1G -jar BungeeCord.jar nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }
    public static void CreateSpigotWinBat() throws IOException {
        Path p = Paths.get("startserver.bat");
        boolean exists = Files.exists(p);
        if (exists) {
        } else {
            File file = new File("startserver.bat");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("@echo off\n");
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx1G -jar Spigot.jar nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }
}

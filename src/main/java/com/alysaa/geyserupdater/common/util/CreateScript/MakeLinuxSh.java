package com.alysaa.geyserupdater.common.util.CreateScript;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeLinuxSh {
    public static void CreateBungeeLinuxSh() throws IOException {
        Path p = Paths.get("plugins/GeyserUpdater/startserver.sh");
        boolean exists = Files.exists(p);
        if (exists) {
        } else {
            System.out.println("[GeyserUpdater] A custom restart script has been made for you, its located in the GeyserUpdater folder. you will need to edit this and also make sure you enable it in spigot.yml!");
            File file = new File("plugins/GeyserUpdater/startserver.sh");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("#!/bin/sh\n");
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx"+Runtime.getRuntime().totalMemory() / (1024 * 1024)+"M -jar BungeeCord.jar nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }

    public static void CreateSpigotLinuxSh() throws IOException {
        Path p = Paths.get("plugins/GeyserUpdater/startserver.sh");
        boolean exists = Files.exists(p);
        if (exists) {
        } else {
            System.out.println("[GeyserUpdater] A custom restart script has been made for you, its located in the GeyserUpdater folder. you will need to edit this and also make sure you enable it in spigot.yml!");
            File file = new File("plugins/GeyserUpdater/startserver.sh");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("#!/bin/sh\n");
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx"+Runtime.getRuntime().totalMemory() / (1024 * 1024)+"M -jar Spigot.jar nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }
}



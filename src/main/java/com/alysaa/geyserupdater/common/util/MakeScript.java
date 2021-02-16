package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.common.util.OSUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeScript {

    public static void createScript(String jarPath) throws IOException {
        Path p = Paths.get("GeyserUpdaterScript.bat");
        boolean exists = Files.exists(p);
        if (!exists) {
            System.out.println("[GeyserUpdater] A custom restart script has been made for you, its located in the main server folder. you will need to edit this and also make sure you enable it in spigot.yml!");
            File file = new File("GeyserUpdaterScript.bat");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            if (OSUtils.isWindows()) {
                dos.writeBytes("@echo off\n");
            } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                dos.writeBytes("#!/bin/sh\n");
            } else {
                System.out.println("[GeyserUpdater] Your OS is not supported! We support Linux, Mac, and Windows to create scripts.");
                return;
            }
            dos.writeBytes(":restart\n");
            dos.writeBytes("java -Xmx" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1024 * 1024) + "M -jar "+ jarPath +" nogui\n");
            dos.writeBytes("Goto restart\n");
        }
    }


}

package com.alysaa.geyserupdater.common.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class RestartScriptFactory {

    public static void createScript(String jarPath, boolean isBungee) throws IOException {
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
            // The restart signal from Spigot is being used in the GeyserSpigotDownload class, which means that a loop in this script is not necessary for spigot.
            // GeyserBungeeDownload can only use the stop signal, so a loop must be used to keep the script alive. This only supports Windows currently.
            if (isBungee) {
                dos.writeBytes(":restart\n");
            }
            dos.writeBytes("java -Xmx" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1024 * 1024) + "M -jar "+ jarPath +" nogui\n");
            if (isBungee) {
                dos.writeBytes("Goto restart\n");
            }
        }
    }
}
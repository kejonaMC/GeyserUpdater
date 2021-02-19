package com.alysaa.geyserupdater.common.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class ScriptCreator {

    public static void createScript(String jarName, boolean runLoop) throws IOException {
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
            System.out.println("[GeyserUpdater] A custom restart script has been made for you.");
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
            List<String> memory = ManagementFactory.getRuntimeMXBean().getInputArguments();

            dos.writeBytes("java -Xmx" + ManagementFactory.getRuntimeMXBean().getInputArguments().replaceAll("[<>\\[\\],-]", "") + "M -jar " + ManagementFactory.getRuntimeMXBean().getClassPath() + " nogui\n");
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

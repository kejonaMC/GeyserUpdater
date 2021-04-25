package com.alysaa.geyserupdater.common.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

public class ScriptCreator {

    public static void createScript(boolean runLoop) throws IOException {
        File file;
        String extension;
        if (OSUtils.isWindows()) {
            extension = "bat";
        } else if (OSUtils.isLinux() || OSUtils.isMac()) {
            extension = "sh";
        } else {
            System.out.println("[GeyserUpdater] Your operating system is not supported! GeyserUpdater only supports automatic script creation for Linux, macOS, and Windows.");
            return;
        }
        file = new File("ServerRestartScript." + extension);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);

            if (OSUtils.isWindows()) {
                dos.writeBytes("@echo off\n");
            } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                dos.writeBytes("#!/bin/sh\n");
            }
            //
            // The restart signal from Spigot is being used in the GeyserSpigotDownload class, which means that a loop in this script is not necessary for spigot.
            // GeyserBungeeDownload can only use the stop signal, so a loop must be used to keep the script alive.
            if (runLoop) {
                if (OSUtils.isWindows()) {
                    dos.writeBytes(":restart\n");
                } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                    dos.writeBytes("while true; do\n");
                }
            }
            // Fetch JVM flags
            List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            String runtimeFlags = String.join(" ", inputArguments);
            // Write command to start server
            dos.writeBytes("java " + runtimeFlags + " -jar " + ManagementFactory.getRuntimeMXBean().getClassPath() + " nogui\n");
            if (runLoop) {
                if (OSUtils.isWindows()) {
                    dos.writeBytes("timeout 10 && Goto restart\n");
                } else if (OSUtils.isLinux() || OSUtils.isMac()) {
                    dos.writeBytes("echo \"Server stopped, restarting in 10 seconds!\"; sleep 10; done\n");
                }
            }
            System.out.println("[GeyserUpdater] GeyserUpdater has finished creating a custom restart script for you.");
            if (runLoop) {
                System.out.println("[GeyserUpdater] You will need to shut down and start the server again using the newly-generated script in order for this functionality to work.");
            }
        }
    }
}

package com.projectg.geyserupdater.common.util;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

public class ScriptCreator {

    /**
     * Create a restart script for the server, if the OS is supported.
     * If the platform is spigot, the restart-script value in spigot.yml will be set to the created script.
     *
     * @param runLoop Whether or not to integrate a loop into the script (should only be used for bungee/velocity)
     * @throws IOException If there was a failure checking for an existing script, or creating a new one.
     */
    public static void createRestartScript(boolean runLoop) throws IOException {
        UpdaterLogger logger = UpdaterLogger.getLogger();

        File file;
        String extension;
        if (OsUtils.isWindows()) {
            extension = "bat";
        } else if (OsUtils.isLinux() || OsUtils.isMacos()) {
            extension = "sh";
        } else {
            logger.warn("Your operating system is not supported! GeyserUpdater only supports automatic script creation for Linux, macOS, and Windows.");
            return;
        }
        file = new File("ServerRestartScript." + extension);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);

            if (OsUtils.isWindows()) {
                dos.writeBytes("@echo off\n");
            } else if (OsUtils.isLinux() || OsUtils.isMacos()) {
                dos.writeBytes("#!/bin/sh\n");
            }
            // The restart signal from Spigot is being used in the GeyserSpigotDownloader class, which means that a loop in this script is not necessary for spigot.
            // GeyserBungeeDownloader can only use the stop signal, so a loop must be used to keep the script alive.
            if (runLoop) {
                if (OsUtils.isWindows()) {
                    dos.writeBytes(":restart\n");
                } else if (OsUtils.isLinux() || OsUtils.isMacos()) {
                    dos.writeBytes("while true; do\n");
                }
            }
            // Fetch JVM flags
            List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            String runtimeFlags = String.join(" ", inputArguments);
            // Write command to start server
            dos.writeBytes("java " + runtimeFlags + " -jar " + ManagementFactory.getRuntimeMXBean().getClassPath() + " nogui\n");
            if (runLoop) {
                if (OsUtils.isWindows()) {
                    dos.writeBytes("timeout 10 && goto restart\n");
                } else if (OsUtils.isLinux() || OsUtils.isMacos()) {
                    dos.writeBytes("echo \"Server stopped, restarting in 10 seconds!\"; sleep 10; done\n");
                }
            }
            logger.info("GeyserUpdater has finished creating a restart script.");
            if (runLoop) {
                logger.warn("You will need to shut down and start the server again using the newly-generated script in order for the auto-restart functionality to begin working.");
            }
        }
    }
}

package com.alysaa.geyserupdater.bungee.util;

import com.alysaa.geyserupdater.common.util.CreateScript.MakeLinuxSh;
import com.alysaa.geyserupdater.common.util.CreateScript.MakeMacSh;
import com.alysaa.geyserupdater.common.util.CreateScript.MakeWinBat;

import java.io.IOException;

public class CheckOSScriptBungee {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void CheckingOs() {
        if (isWindows()) {
            System.out.println("[GeyserUpdater] Windows OS detected!");

            try {
                MakeWinBat.CreateBungeeWinBat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isMac()) {
            System.out.println("[GeyserUpdater] Mac OS detected!");
            try {
                MakeMacSh.CreateBungeeMacSh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isUnix()) {
            System.out.println("[GeyserUpdater] Linux OS detected!");
            try {
                MakeLinuxSh.CreateBungeeLinuxSh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Your OS is not support!!");
        }
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0);
    }
}


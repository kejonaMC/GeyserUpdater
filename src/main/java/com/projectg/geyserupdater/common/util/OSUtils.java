package com.projectg.geyserupdater.common.util;

public class OSUtils {

    private static boolean isWindows = false;
    private static boolean isLinux = false;
    private static boolean isMacos = false;
    private static boolean isKnownOS = false;
    private static boolean isInitialized = false;

    private static void initialize() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            isWindows = true;
            isKnownOS = true;
        } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            isLinux = true;
            isKnownOS = true;
        } else if (OS.contains("mac")) {
            isMacos = true;
            isKnownOS = true;
        }
        isInitialized = true;
    }
    public static boolean isWindows() {
        if (!isInitialized) {
            initialize();
        }
        return isWindows;
    }
    public static boolean isMacos() {
        if (!isInitialized) {
            initialize();
        }
        return isMacos;
    }
    public static boolean isLinux() {
        if (!isInitialized) {
            initialize();
        }
        return isLinux;
    }
    public static boolean isKnownOS() {
        if (!isInitialized) {
            initialize();
        }
        return isKnownOS;
    }
}



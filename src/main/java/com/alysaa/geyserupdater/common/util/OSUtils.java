package com.alysaa.geyserupdater.common.util;

public class OSUtils {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isLinux() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0);
    }

    public static OSType getOS() {
        if (isLinux()) return OSType.LINUX;
        else if (isMac()) return OSType.MACOS;
        else if (isWindows()) return OSType.WINDOWS;
        else return null;
    }
}

enum OSType {
    WINDOWS("windows"),
    MACOS("macos"),
    LINUX("linux");

    OSType(String windows) {}
}



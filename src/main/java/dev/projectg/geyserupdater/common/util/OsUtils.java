package dev.projectg.geyserupdater.common.util;

public class OsUtils {

    private static boolean isWindows = false;
    private static boolean isLinux = false;
    private static boolean isMacos = false;
    private static boolean isKnownOS = false;

    static {
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
    }

    public static boolean isWindows() {
        return isWindows;
    }
    public static boolean isMacos() {
        return isMacos;
    }
    public static boolean isLinux() {
        return isLinux;
    }
    public static boolean isKnownOS() {
        return isKnownOS;
    }
}



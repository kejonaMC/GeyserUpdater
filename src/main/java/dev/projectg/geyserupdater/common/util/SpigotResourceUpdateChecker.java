package dev.projectg.geyserupdater.common.util;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class SpigotResourceUpdateChecker {

    private static final String VERSION_REGEX = "(\\d+.){1,2}\\d+(-SNAPSHOT){0,1}";

    /**
     * Get the latest version of GeyserUpdater from the spigot resource page
     * @return the latest version, null if there was an error.
     */
    public static String getVersion(int resourceId) {

        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
            String version = builder.toString();
            if (version.matches(VERSION_REGEX)) {
                return version;
            } else {
                UpdaterLogger.getLogger().warn("Got unexpected string when checking Spigot resource page version: " + version);
                return null;
            }
        } catch (IOException exception) {
            UpdaterLogger.getLogger().error("Failed to check for updates: " + exception.getMessage());
            return null;
        }
    }
}

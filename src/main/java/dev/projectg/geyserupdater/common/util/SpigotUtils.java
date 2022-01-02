package dev.projectg.geyserupdater.common.util;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class SpigotUtils {

    private static final String VERSION_REGEX = "(\\d+.){1,2}\\d+(-SNAPSHOT|-RC\\d{1,2}){0,1}";

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
            if (!version.matches(VERSION_REGEX)) {
                UpdaterLogger.getLogger().warn("Got unexpected string when checking version of Spigot resource " + resourceId + ": " + version);
            }

            return version;
        } catch (IOException exception) {
            UpdaterLogger.getLogger().error("Failed to check for updates: " + exception.getMessage());
            return null;
        }
    }

    public static URL getDownloadUrl(int resourceId) {
        try {
            return new URL("https://api.spiget.org/v2/resources/" + resourceId + "/download");
        } catch (MalformedURLException e) {
            throw new AssertionError("Unexpected MalformedURLException when getting download link for spigot resource");
        }
    }
}

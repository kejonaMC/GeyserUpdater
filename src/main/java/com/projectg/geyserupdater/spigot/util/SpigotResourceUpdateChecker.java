package com.projectg.geyserupdater.spigot.util;

import com.projectg.geyserupdater.spigot.SpigotUpdater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class SpigotResourceUpdateChecker {
    // TODO: this mess

    /**
     * Get the latest version of GeyserUpdater from the spigot resource page
     * @param updater plugin instance
     * @return the latest version, if successful. Will return null if there was a failure.
     */
    public static String getVersion(SpigotUpdater updater) {
        // todo: use the better and easier api

        try (InputStream inputStream = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=88555").openStream(); Scanner scanner = new Scanner(inputStream)) {
            StringBuilder total = new StringBuilder();
            while (scanner.hasNext()) {
                total.append(scanner.next());
            }
            JsonObject jsonObject = JsonParser.parseString(total.toString()).getAsJsonObject();
            return jsonObject.get("current_version").getAsString();
        } catch (IOException exception) {
            updater.getLogger().severe("Failed to check for updates: " + exception.getMessage());
            return null;
        }
    }
}

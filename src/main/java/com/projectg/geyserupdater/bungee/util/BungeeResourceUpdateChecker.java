package com.projectg.geyserupdater.bungee.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class BungeeResourceUpdateChecker {

    // todo this mess too

    public static String getVersion() {

        try (InputStream inputStream = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=88555").openStream(); Scanner scanner = new Scanner(inputStream)) {
            StringBuilder total = new StringBuilder();
            while (scanner.hasNext()) {
                total.append(scanner.next());
            }
            JsonObject jsonObject = JsonParser.parseString(total.toString()).getAsJsonObject();
            return jsonObject.get("current_version").getAsString();
        } catch (IOException e) {
            UpdaterLogger.getLogger().error("Failed to check for updates: " + e.getMessage());
            return null;
        }
    }
}

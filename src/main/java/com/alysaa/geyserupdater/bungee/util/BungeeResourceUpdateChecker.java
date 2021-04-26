package com.alysaa.geyserupdater.bungee.util;


import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class BungeeResourceUpdateChecker {

    // todo this mess too

    public static String getVersion(BungeeUpdater updater) {

        try (InputStream inputStream = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=88555").openStream(); Scanner scanner = new Scanner(inputStream)) {
            String total = "";
            while (scanner.hasNext()) {
                total += scanner.next();
            }
            JsonObject jsonObject = new JsonParser().parse(total).getAsJsonObject();
            String version = jsonObject.get("current_version").getAsString();
            return version;
        } catch (IOException e) {
            updater.getLogger().info("Cannot look for updates: " + e.getMessage());
            return null;
        }
    }
}

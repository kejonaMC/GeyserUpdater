package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.spigot.SpigotUpdater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class SpigotResourceUpdateChecker {
    // TODO: this mess

    private JavaPlugin plugin;
    private int resourceId;

    public SpigotResourceUpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }
    public static String getVersion(SpigotUpdater updater) {
        try (InputStream inputStream = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=88555").openStream(); Scanner scanner = new Scanner(inputStream)) {
            String total = "";
            while (scanner.hasNext()) {
                total += scanner.next();
            }
            JsonObject jsonObject = new JsonParser().parse(total).getAsJsonObject();
            String version = jsonObject.get("current_version").getAsString();
            return version;
        } catch (IOException exception) {
            updater.getLogger().severe("Failed to check for updates: " + exception.getMessage());
            return null;
        }
    }
}

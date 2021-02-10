package com.alysaa.geyserupdater.common.util;


import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class ResourceUpdaterBungee{

    public static BungeeUpdater plugin;
    private static int resourceId;

    public ResourceUpdaterBungee(BungeeUpdater plugin, int resourceId) {
        ResourceUpdaterBungee.plugin = plugin;
        ResourceUpdaterBungee.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {

        try (InputStream inputStream = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
            String total = "";
            while (scanner.hasNext()) {
                total += scanner.next();
            }
            JsonObject jsonObject = new JsonParser().parse(total).getAsJsonObject();
            String version = jsonObject.get("current_version").getAsString();
            consumer.accept(version);
        } catch (IOException exception) {
            BungeeUpdater.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
        }
    }
}

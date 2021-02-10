package com.alysaa.geyserupdater.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class ResourceUpdaterSpigot {

    private JavaPlugin plugin;
    private int resourceId;

    public ResourceUpdaterSpigot(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                String total = "";
                while (scanner.hasNext()) {
                    total += scanner.next();
                }
                JsonObject jsonObject = new JsonParser().parse(total).getAsJsonObject();
                String version = jsonObject.get("current_version").getAsString();
                consumer.accept(version);
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}

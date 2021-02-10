package com.alysaa.geyserupdater.common.util;


import com.alysaa.geyserupdater.bungee.BungeeUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class ResourceUpdaterBungee {

    public static BungeeUpdater plugin;
    private static int resourceId;

    public ResourceUpdaterBungee(BungeeUpdater plugin, int resourceId) {
        ResourceUpdaterBungee.plugin = plugin;
        ResourceUpdaterBungee.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {

        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ResourceUpdaterBungee.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
            }
        } catch (IOException exception) {
            BungeeUpdater.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
        }
    }
}

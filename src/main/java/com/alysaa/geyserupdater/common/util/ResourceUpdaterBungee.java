package com.alysaa.geyserupdater.common.util;

import com.alysaa.geyserupdater.bungee.BungeeUpdater;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class ResourceUpdaterBungee {

    private final BungeeUpdater plugin;
    private final int resourceId;

    public ResourceUpdaterBungee(BungeeUpdater plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        ProxyServer.getInstance().getScheduler().schedule(this.plugin,30, 30, () -> {
            {
                try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=88555" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                } catch (IOException exception) {
                    this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
                }
            })
        });
    }
}
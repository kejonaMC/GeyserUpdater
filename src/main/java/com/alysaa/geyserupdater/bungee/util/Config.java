package com.alysaa.geyserupdater.bungee.util;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Config {
    public static File startConfig(Plugin plugin, String file) {
        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdir();
        }
        File resourceFile = new File(folder, file);
        try {
            if (!resourceFile.exists()) {
                try (InputStream in = plugin.getResourceAsStream(file);
                    OutputStream out = new FileOutputStream(resourceFile)) {
                    byte[] buffer = new byte[in.available()];
                    in.read(buffer);
                    out.write(buffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }
}

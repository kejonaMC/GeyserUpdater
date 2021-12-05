package com.projectg.geyserupdater.common.configurate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration {

    /**
     * Load GeyserUpdater Config
     *
     * @param dataDirectory The config's directory
     */
    public ConfigurationJackson configSetup(Path dataDirectory) {
        createConfig(dataDirectory);
        ConfigurationJackson config = new ConfigurationJackson();
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
           config = mapper.readValue(new File(dataDirectory + "\\" + "config.yml"), ConfigurationJackson.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    /**
     * Create GeyserUpdater Config
     *
     * @param path The config's directory
     */
    private void createConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}

package com.projectg.geyserupdater.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.projectg.geyserupdater.common.config.UpdaterConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {

    public static UpdaterConfiguration loadConfig(Path userConfig) throws IOException {
        if (!Files.exists(userConfig)) {
            Files.createDirectories(userConfig.getParent());
            try (InputStream inputStream = FileUtils.class.getResourceAsStream("/config.yml")) {
                Objects.requireNonNull(inputStream);
                Files.copy(inputStream, userConfig);
            }
        }

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        return yamlMapper.readValue(userConfig.toFile(), UpdaterConfiguration.class);
    }
}


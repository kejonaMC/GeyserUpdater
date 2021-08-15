package com.projectg.geyserupdater.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.projectg.geyserupdater.common.config.UpdaterConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Get the file that a class resides in.
     * @param clazz The class
     * @return The file as a {@link Path}
     * @throws URISyntaxException if there was a failure getting the {@link java.net.URL} of the {@link java.security.CodeSource}
     */
    public static Path getCodeSourceLocation(Class<?> clazz) throws URISyntaxException {
        return Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
    }
}


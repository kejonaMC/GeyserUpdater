package dev.projectg.geyserupdater.common.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

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


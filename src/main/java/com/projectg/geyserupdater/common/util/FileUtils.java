package com.projectg.geyserupdater.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.projectg.geyserupdater.common.config.UpdaterConfiguration;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtils {
    // TODO: this whole cached thing only works if you're using checkFile for one file...

    /**
     * Epoch time of that last occurrence that {@link #checkFile(String, boolean)} directly checked a file. Returns a value of 0 if the check file method has never been called.
     */
    private static long callTime = 0;

    /**
     * Returns a cached result of {@link #checkFile(String, boolean)}. Returns null if the method has never been called.
     */
    private static boolean cachedResult;

    /**
     * Check if a file exists.
     *
     * @param path the path of the file to test
     * @param allowCached allow a cached result of maximum 30 minutes to be returned
     * @return true if the file exists, false if not
     */
    public static boolean checkFile(String path, boolean allowCached) {
        UpdaterLogger logger = UpdaterLogger.getLogger();
        if (allowCached) {
            long elapsedTime = System.currentTimeMillis() - callTime;
            if (elapsedTime < 30 * 60 * 1000) {
                logger.debug("Returning a cached result of the last time we checked if a file exists. The cached result is: " + cachedResult);
                return cachedResult;
            } else {
                logger.debug("Not returning a cached result of the last time we checked if a file exists because it has been too long.");
            }
        }
        Path p = Paths.get(path);
        boolean exists = Files.exists(p);

        logger.debug("Checked if a file exists. The result: " + exists);
        callTime = System.currentTimeMillis();
        cachedResult = exists;
        return exists;
    }

    /**
     * Download a file
     *
     * @param fileURL the url of the file
     * @param outputPath the path of the output file to write to
     */
    public static void downloadFile(String fileURL, String outputPath) throws IOException {
        // TODO: better download code?

        UpdaterLogger.getLogger().debug("Attempting to download a file with URL and output path: " + fileURL + " , " + outputPath);

        Path outputDirectory = Paths.get(outputPath).getParent();
        Files.createDirectories(outputDirectory);

        OutputStream os;
        InputStream is;
        // create a url object
        URL url = new URL(fileURL);
        // connection to the file
        URLConnection connection = url.openConnection();
        // get input stream to the file
        is = connection.getInputStream();
        // get output stream to download file
        os = new FileOutputStream(outputPath);
        final byte[] b = new byte[2048];
        int length;
        // read from input stream and write to output stream
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        // close streams
        is.close();
        os.close();
    }

    public static UpdaterConfiguration loadConfig(Path userConfig) throws IOException {
        if (!Files.exists(userConfig)) {
            //Files.createDirectories(userConfig.getParent()); todo: necessary?
            try (InputStream inputStream = FileUtils.class.getResourceAsStream("/config.yml")) {
                Objects.requireNonNull(inputStream);
                Files.copy(inputStream, userConfig);
            }
        }

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        return yamlMapper.readValue(userConfig.toFile(), UpdaterConfiguration.class);
    }
}


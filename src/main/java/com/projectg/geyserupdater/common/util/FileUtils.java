package com.projectg.geyserupdater.common.util;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    /**
     * Epoch time of that last occurrence that {@link #checkFile(String, boolean)} directly checked a file. Returns a value of 0 if the check file method has never been called.
     */
    private static long callTime = 0;

    /**
     * Returns a cached result of {@link #checkFile(String, boolean)}. Returns null if the method has never been called.
     */
    private static boolean cachedResult;

    // todo this is absolutely abhorrent and assumes we are always downloading the same jar

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
     * @param expectedSha256 the expected sha256 hash of the downloaded file
     */
    public static void downloadFile(String fileURL, String outputPath, @Nullable String expectedSha256) throws IOException {
        UpdaterLogger logger = UpdaterLogger.getLogger();
        logger.debug("Attempting to download a file with URL and output path: " + fileURL + " , " + outputPath);

        Path outputDirectory = Paths.get(outputPath).getParent();
        Files.createDirectories(outputDirectory);

        // Download Jar file
        URL url = new URL(fileURL);
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(outputPath)) {

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            logger.error("Failed to download %s to %s".formatted(fileURL, outputPath), e);
        }

        if (expectedSha256 != null) {
            // hash the file
            File file = new File(outputPath);
            ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
            String hash = byteSource.hash(Hashing.sha256()).toString();

            // compare
            if (expectedSha256.equals(hash)) {
                if (logger.isDebug()) {
                    logger.debug("Successful checksum for %s of %s".formatted(file, hash));
                }
            } else {
                logger.warn("Expected a hash of %s but got %s".formatted(expectedSha256, hash));

                // If the checksum failed we attempt to delete the broken build.
                if (file.delete()) {
                    logger.warn("Downloaded a jar whose checksum was incorrect, deleting: " + file);
                } else {
                    logger.error("Failed to delete a defective download, please delete manually: " + file);
                }
            }
        }
    }
}
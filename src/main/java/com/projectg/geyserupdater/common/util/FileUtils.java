package com.projectg.geyserupdater.common.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;

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
    public static void downloadFile(String fileURL, String outputPath, String platformname) throws IOException {
        UpdaterLogger logger = UpdaterLogger.getLogger();
        logger.debug("Attempting to download a file with URL and output path: " + fileURL + " , " + outputPath);

        // TODO: this whole cached thing only works if you're using checkFile for one file...

        Path outputDirectory = Paths.get(outputPath).getParent();
        Files.createDirectories(outputDirectory);
        // Download Jar file
        URL url = new URL(fileURL);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(outputPath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        // Checking file checksum
        ServerPlatform serverPlatform = ServerPlatform.valueOf(platformname);
        String sha256 = null;
        switch (serverPlatform) {
            case SPIGOT -> sha256 = new GeyserApi().endPoints().downloads.spigot.sha256;
            case BUNGEECORD -> sha256 = new GeyserApi().endPoints().downloads.bungeecord.sha256;
            case VELOCITY -> sha256 = new GeyserApi().endPoints().downloads.velocity.sha256;
        }
        // Manually Hash the files bytecode to match hash from Geyser API
        File file = new File(outputPath);
        ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
        HashCode hc = byteSource.hash(Hashing.sha256());
        String checksum = hc.toString();

        if (sha256 != null && sha256.equals(checksum)) {
            logger.debug("SHA256 Checksum matches!");
        } else {
            // If the checksum failed we delete the broken build.
            if (file.delete()) {
                logger.info("Please report this to KejonaMC Staff, SHA256 did not match, deleted the defective build: " + file.getName());
            }
        }
    }

    private enum ServerPlatform {
        SPIGOT,
        BUNGEECORD,
        VELOCITY
    }
}
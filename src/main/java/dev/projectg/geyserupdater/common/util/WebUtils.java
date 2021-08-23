package dev.projectg.geyserupdater.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.projectg.geyserupdater.common.GeyserUpdater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Full credit to GeyserMC
// https://github.com/GeyserMC/Geyser/blob/master/connector/src/main/java/org/geysermc/connector/utils/WebUtils.java

public class WebUtils {

    /**
     * Makes a web request to the given URL and returns the body as a string
     *
     * @param reqURL URL to fetch
     * @return Body contents
     */
    public static String getBody(URL reqURL) throws IOException {
        HttpURLConnection con = (HttpURLConnection) reqURL.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "GeyserUpdater-" + GeyserUpdater.getInstance().version); // Otherwise Java 8 fails on checking updates

        return connectionToString(con);
    }

    public static String getBody(String reqURL) throws IOException {
        return getBody(new URL(reqURL));
    }

    /**
     * Makes a web request to the given URL and returns the body as a {@link JsonNode}.
     *
     * @param reqURL URL to fetch
     * @return the response as JSON
     */
    public static JsonNode getJson(String reqURL) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(reqURL).openConnection();
        con.setRequestProperty("User-Agent", "GeyserUpdater-" + GeyserUpdater.getInstance().version);
        return new ObjectMapper().readTree(con.getInputStream());
    }

    /**
     * Downloads a file from the given URL and saves it to disk
     *
     * @param reqURL File to fetch
     * @param fileLocation Location to save on disk
     */
    public static void downloadFile(String reqURL, Path fileLocation) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(reqURL).openConnection();
        con.setRequestProperty("User-Agent", "GeyserUpdater-" + GeyserUpdater.getInstance().version);
        InputStream in = con.getInputStream();
        Files.copy(in, fileLocation, StandardCopyOption.REPLACE_EXISTING);
        // todo: need to close the inputstream or not?
        in.close();
        con.disconnect();
    }


    /**
     * Get the string output from the passed {@link HttpURLConnection}
     *
     * @param con The connection to get the string from
     * @return The body of the returned page
     */
    private static String connectionToString(HttpURLConnection con) throws IOException {
        // Send the request (we don't use this but its required for getErrorStream() to work)
        con.getResponseCode();

        // Read the error message if there is one if not just read normally
        InputStream inputStream = con.getErrorStream();
        if (inputStream == null) {
            inputStream = con.getInputStream();
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                content.append("\n");
            }

            con.disconnect();
        }

        return content.toString();
    }

    /**
     * Get the filename at the end of a url
     * @param url The url to get the filename at the end from. Should not end with a /
     */
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

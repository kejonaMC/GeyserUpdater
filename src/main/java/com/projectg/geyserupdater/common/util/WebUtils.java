package com.projectg.geyserupdater.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectg.geyserupdater.common.GeyserUpdater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
     * @return Body contents or error message if the request fails
     */
    public static String getBody(String reqURL) {
        try {
            URL url = new URL(reqURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "GeyserUpdater-" + GeyserUpdater.getInstance().version); // Otherwise Java 8 fails on checking updates

            return connectionToString(con);
        } catch (Exception e) {
            return e.getMessage();
        }
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
    public static void downloadFile(String reqURL, Path fileLocation) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(reqURL).openConnection();
            con.setRequestProperty("User-Agent", "GeyserUpdater-" + GeyserUpdater.getInstance().version);
            InputStream in = con.getInputStream();
            Files.copy(in, fileLocation, StandardCopyOption.REPLACE_EXISTING);
            // todo: need to close the inputstream or not?
            in.close();
        } catch (Exception e) {
            throw new AssertionError("Unable to download and save file: " + fileLocation + " (" + reqURL + ")", e);
        }
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

    /** Get the latest build number of a given branch of Geyser from jenkins CI
     *
     * @param branchLink Example: https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/master
     * @return the latest build number
     * @throws UnsupportedEncodingException if failed to encode the given gitBranch
     */
    public static int getLatestGeyserBuildNumberFromJenkins(String branchLink) throws UnsupportedEncodingException {
        // todo use json
        String buildXMLContents = WebUtils.getBody(URLEncoder.encode(branchLink, StandardCharsets.UTF_8.toString()) + "/lastSuccessfulBuild/api/xml?xpath=//buildNumber");
        return Integer.parseInt(buildXMLContents.replaceAll("<(\\\\)?(/)?buildNumber>", "").trim());
    }
}

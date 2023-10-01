package com.projectg.geyserupdater.common.util;

import com.google.gson.Gson;
import com.projectg.geyserupdater.common.json_schema.EndpointResponse;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeyserDownloadApi {
    private static final Gson gson = new Gson();

    public EndpointResponse data() throws Exception {
        URL url = new URL(Constants.GEYSER_BASE_URL + Constants.GEYSER_LATEST_MASTER_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("Received %s from GET of %s".formatted(connection.getResponseCode(), url));
        }

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            return gson.fromJson(reader, EndpointResponse.class);
        }
    }
}
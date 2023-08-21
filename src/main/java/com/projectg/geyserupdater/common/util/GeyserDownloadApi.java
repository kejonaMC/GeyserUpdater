package com.projectg.geyserupdater.common.util;

import com.google.gson.Gson;
import com.projectg.geyserupdater.common.json_schema.EndpointResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeyserDownloadApi {
    public EndpointResponse data() {
        try {
            URL url = new URL(Constants.GEYSER_BASE_URL + Constants.GEYSER_LATEST_MASTER_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                    return gson.fromJson(reader, EndpointResponse.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
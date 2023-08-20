package com.projectg.geyserupdater.common.util;

import com.google.gson.Gson;
import com.projectg.geyserupdater.common.pojo.Root;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeyserApi {
    public Root endPoints() {
        try {
            URL url = new URL(Constants.GEYSER_BASE_URL + Constants.GEYSER_LATEST_MASTER_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                    return gson.fromJson(reader, Root.class);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
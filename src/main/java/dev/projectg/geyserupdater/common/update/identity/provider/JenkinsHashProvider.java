package dev.projectg.geyserupdater.common.update.identity.provider;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.update.identity.type.Md5FileHash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JenkinsHashProvider implements IdentityProvider<Md5FileHash> {

    private final URL url;

    public JenkinsHashProvider(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public Md5FileHash getValue() {
        // Don't use WebUtils so that we don't have to iterate over the whole page contents, and have better error messages.

        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "GeyserUpdater");

            if (con.getResponseCode() != 200) {
                UpdaterLogger.getLogger().error("Unable to find md5 from jenkins fingerprint at: " + url + " because the Http response code was not 200 (OK).");
                return null;
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("MD5")) {
                        con.disconnect();
                        return new Md5FileHash(inputLine.substring(inputLine.indexOf("MD5: ") + 5, inputLine.indexOf("</div>")));
                    }
                }
                con.disconnect();

                UpdaterLogger.getLogger().error("Unable to find md5 from jenkins fingerprint at: " + url + " because the page scan failed to find the hash.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        UpdaterLogger.getLogger().error("Unable to find md5 from jenkins fingerprint at: " + url + " because of an exception.");
        return null;
    }
}

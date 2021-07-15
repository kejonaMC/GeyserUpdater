package com.projectg.geyserupdater.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class UpdateManager {

    private List<Updateable> updateables = new ArrayList<>();

    public UpdateManager() {
    }

    public List<Updateable> getUpdateables() {
        return new ArrayList<>(updateables);
    }

    public void add(Updateable updateable) {
        updateables.add(updateable);
    }

    public void add(PluginId pluginId) throws IOException {
        // todo: have this stuff in Updateable or PluginId (probably the former)
        Properties properties = new Properties();

        InputStream is = pluginId.pluginClass.getResourceAsStream("git.properties");
        if (is == null) {
            throw new AssertionError("Unable to find resource: git.properties");
        }

        properties.load(is);
        is.close();

        String branch = properties.getProperty("git.branch"); //todo: pass default here?
        String number = properties.getProperty("git.build.number");
        Objects.requireNonNull(branch, number);

        updateables.add(new Updateable(pluginId, branch, Integer.parseInt(number)));
    }
}

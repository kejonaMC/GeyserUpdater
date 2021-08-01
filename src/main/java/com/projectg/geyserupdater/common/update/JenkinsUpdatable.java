package com.projectg.geyserupdater.common.update;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JenkinsUpdatable implements Updatable{

    public final PluginId pluginId;
    public final String branch;
    public final int buildNumber;

    public JenkinsUpdatable(PluginId pluginId) throws IOException {
        this.pluginId = pluginId;

        // Get the class of the plugin
        Class<?> pluginClass;
        try {
            pluginClass = Class.forName(pluginId.pluginClass);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Failed to find Class '" + pluginId.pluginClass + "' for plugin: " + pluginId.name());
        }

        // Get the git.properties
        InputStream is = pluginClass.getResourceAsStream("git.properties");
        if (is == null) {
            throw new AssertionError("Unable to find resource 'git.properties' for plugin: " + pluginId.name());
        }

        Properties properties = new Properties();
        properties.load(is);
        is.close();

        String branch = properties.getProperty("git.branch");
        String number = properties.getProperty("git.build.number");
        if (branch == null || number == null) {
            throw new AssertionError("Failed to find branch or build number in git.properties of plugin: " + pluginId.name());
        }

        this.branch = branch;
        this.buildNumber = Integer.parseUnsignedInt(number);
    }

    @Override
    public boolean isOutdated() {
        return false;
    }

    @Override
    public String getLatestDownloadLink() {
        return null;
    }
}

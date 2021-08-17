package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.GeyserUpdater;

public enum PluginId {
    GEYSER("https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/", "org.geysermc.connector.GeyserConnector"),
    FLOODGATE("https://ci.opencollab.dev/job/GeyserMC/job/Floodgate/job/", "org.geysermc.floodgate.FloodgatePlatform");

    /**
     * https://ci.opencollab.dev/job/GeyserMC/job/{PLUGIN_PAGE}/job/
     */
    private final String projectLink;

    private String branch;
    private String artifactLink;

    /**
     * A class from the given plugin
     */
    private final String pluginClassName;

    PluginId(String link, String pluginClassName) {
        this.projectLink = link;
        this.pluginClassName = pluginClassName;
    }

    /**
     * @return The download link. Not usable if {@link PluginId#setArtifact(String)} has not been called.
     */
    public String getLatestFileLink() {
        return projectLink + branch + "/lastSuccessfulBuild/" + artifactLink;
    }

    public String getLatestBuildNumber() {
        return projectLink + branch + "/lastSuccessfulBuild/buildNumber";
    }

    /**
     * @param branch The branch to be used for {@link PluginId#getLatestFileLink()}
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * @param artifactLink The artifact link. `artifact/bootstrap/spigot/target/Geyser-Spigot.jar` for example.
     */
    public void setArtifact(String artifactLink) {
        this.artifactLink = artifactLink;
    }

    /**
     * @return A class from the plugin. Will throw an {@link AssertionError} if the class is not available, i.e. the plugin is not loaded.
     */
    public Class<?> getPluginClass() {
        try {
            return Class.forName(pluginClassName);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Failed to find Class '" + pluginClassName + "' for plugin: " + this.name() + ". Is the plugin loaded?");
        }
    }

    /**
     * @return True if the plugin is enabled in {@link com.projectg.geyserupdater.common.config.UpdaterConfiguration}
     */
    public boolean isEnabled() {
        if (this == GEYSER) {
            return GeyserUpdater.getInstance().getConfig().isAutoUpdateGeyser();
        } else if (this == FLOODGATE) {
            return GeyserUpdater.getInstance().getConfig().isAutoUpdateFloodgate();
        } else {
            //todo fix this bs
            return false;
        }
    }
}

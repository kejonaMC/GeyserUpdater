package com.projectg.geyserupdater.common.update;

public enum PluginId {
    GEYSER("https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/", "org.geysermc.connector.GeyserConnector"),
    FLOODGATE("https://ci.opencollab.dev/job/GeyserMC/job/Floodgate/job/", "org.geysermc.floodgate.FloodgatePlatform");

    /**
     * https://ci.opencollab.dev/job/GeyserMC/job/{PLUGIN_PAGE}/job/
     */
    public final String link;

    /**
     * A class from the given plugin
     */
    public final String pluginClass;

    PluginId(String link, String pluginClass) {
        this.link = link;
        this.pluginClass = pluginClass;
    }
}

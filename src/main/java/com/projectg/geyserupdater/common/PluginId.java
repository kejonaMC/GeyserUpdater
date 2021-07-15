package com.projectg.geyserupdater.common;

import org.geysermc.connector.GeyserConnector;
import org.geysermc.floodgate.FloodgatePlatform;

public enum PluginId {
    GEYSER("https://ci.opencollab.dev/job/GeyserMC/job/Geyser/job/", GeyserConnector.class),
    FLOODGATE("https://ci.opencollab.dev/job/GeyserMC/job/Floodgate/job/", FloodgatePlatform.class);

    /**
     * https://ci.opencollab.dev/job/GeyserMC/job/{PLUGIN_PAGE}/job/
     */
    public final String link;

    /**
     * A class from the given plugin
     */
    public final Class<?> pluginClass;

    PluginId(String link, Class<?> pluginClass) {
        this.link = link;
        this.pluginClass = pluginClass;
    }
}

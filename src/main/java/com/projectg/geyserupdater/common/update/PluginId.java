package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.GeyserUpdater;

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
    public final String pluginClassName;

    PluginId(String link, String pluginClassName) {
        this.link = link;
        this.pluginClassName = pluginClassName;
    }

    public Class<?> getPluginClass() {
        try {
            return Class.forName(pluginClassName);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Failed to find Class '" + pluginClassName + "' for plugin: " + this.name() + ". Is the plugin loaded?");
        }
    }

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

package com.projectg.geyserupdater.common;

public class Updateable {

    public final PluginId pluginId;
    public final String branch;
    public final int buildNumber;

    public Updateable(PluginId pluginId, String branch, int buildNumber) {
        this.pluginId = pluginId;
        this.branch = branch;
        this.buildNumber = buildNumber;
    }
}

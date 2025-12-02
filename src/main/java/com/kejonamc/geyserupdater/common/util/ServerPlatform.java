package com.kejonamc.geyserupdater.common.util;

public enum ServerPlatform {
    SPIGOT("spigot"),
    BUNGEECORD("bungeecord"),
    VELOCITY("velocity");

    private final String urlComponent;

    ServerPlatform(String urlComponent) {
        this.urlComponent = urlComponent;
    }

    public String getUrlComponent() {
        return urlComponent;
    }
}


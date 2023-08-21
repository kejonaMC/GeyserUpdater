package com.projectg.geyserupdater.common.json_schema;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;


@Getter
@Accessors(fluent = true)
public class EndpointResponse {
    private String project_id;
    private String project_name;
    private String version;
    private int build;
    private Date time;
    private String channel;
    private boolean promoted;
    private ArrayList<Change> changes;
    private Downloads downloads;

    @Getter
    public static class Bungeecord {
        private String name;
        private String sha256;
    }

    @Getter
    public static class Change {
        private String commit;
        private String summary;
        private String message;
    }

    @Getter
    public static class Downloads {
        private Bungeecord bungeecord;
        private Fabric fabric;
        private Spigot spigot;
        private Sponge sponge;
        private Standalone standalone;
        private Velocity velocity;
    }

    @Getter
    public static class Fabric {
        private String name;
        private String sha256;
    }

    @Getter
    public static class Spigot {
        private String name;
        private String sha256;
    }

    @Getter
    public static class Sponge {
        private String name;
        private String sha256;
    }

    @Getter
    public static class Standalone {
        private String name;
        private String sha256;
    }

    @Getter
    public static class Velocity {
        private String name;
        private String sha256;
    }
}
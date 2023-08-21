package com.projectg.geyserupdater.common.json_schema;

import java.util.ArrayList;
import java.util.Date;

public class EndpointResponse {
    public String project_id;
    public String project_name;
    public String version;
    public int build;
    public Date time;
    public String channel;
    public boolean promoted;
    public ArrayList<Change> changes;
    public Downloads downloads;

    public static class Bungeecord {
        public String name;
        public String sha256;
    }

    public static class Change {
        public String commit;
        public String summary;
        public String message;
    }

    public static class Downloads {
        public Bungeecord bungeecord;
        public Fabric fabric;
        public Spigot spigot;
        public Sponge sponge;
        public Standalone standalone;
        public Velocity velocity;
    }

    public static class Fabric {
        public String name;
        public String sha256;
    }

    public static class Spigot {
        public String name;
        public String sha256;
    }

    public static class Sponge {
        public String name;
        public String sha256;
    }

    public static class Standalone {
        public String name;
        public String sha256;
    }

    public static class Velocity {
        public String name;
        public String sha256;
    }
}
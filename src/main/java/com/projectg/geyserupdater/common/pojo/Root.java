package com.projectg.geyserupdater.common.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Root {
    public String project_id;
    public String project_name;
    public String version;
    public int build;
    public Date time;
    public String channel;
    public boolean promoted;
    public ArrayList<Change> changes;
    public Downloads downloads;
}
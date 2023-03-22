package com.projectg.geyserupdater.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectg.geyserupdater.common.pojo.Root;
import org.geysermc.geyser.util.WebUtils;
import org.json.JSONObject;

public class GeyserAPI {
    public Root endPoints() {
        Root api;
        try {
            JSONObject json = new JSONObject(WebUtils.getBody(Constants.GEYSER_BASE_URL + Constants.GEYSER_LATEST_MASTER_ENDPOINT));
            ObjectMapper om = new ObjectMapper();
            api = om.readValue(json.toString(), Root.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return api;
    }
}

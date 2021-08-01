package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.GeyserUpdater;
import com.projectg.geyserupdater.common.config.UpdaterConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class UpdateManager {

    private List<Updatable> updatables = new ArrayList<>();

    public UpdateManager(GeyserUpdater geyserUpdater) {
        UpdaterConfiguration config = geyserUpdater.getConfig();
        boolean updateGeyser = config.isAutoUpdateGeyser();
        boolean updateFloodgate = config.isAutoUpdateFloodgate();







    }
}

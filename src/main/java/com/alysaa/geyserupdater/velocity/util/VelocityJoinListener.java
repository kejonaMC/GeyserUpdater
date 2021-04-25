package com.alysaa.geyserupdater.velocity.util;

import com.alysaa.geyserupdater.common.util.CheckBuildFile;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import net.kyori.adventure.text.Component;

public class VelocityJoinListener {

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        // We allow a cached result of maximum age 30 minutes to be used
        if (CheckBuildFile.checkVelocityFile(true)) {
            if (event.getPlayer().hasPermission("gupdater.geyserupdate")) {
                event.getPlayer().sendMessage(Component.text("[GeyserUpdater] A new Geyser build has been downloaded! Please restart Velocity in order to use the updated build!"));
            }
        }
    }
}

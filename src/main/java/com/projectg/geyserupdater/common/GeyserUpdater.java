package com.projectg.geyserupdater.common;

import com.projectg.geyserupdater.common.scheduler.UpdaterScheduler;


public class GeyserUpdater {

    private final UpdaterScheduler scheduler;
    private final PlayerHandler playerHandler;

    public GeyserUpdater(UpdaterScheduler scheduler, PlayerHandler playerHandler) {
        this.scheduler  = scheduler;
        this.playerHandler = playerHandler;
    }


    public UpdaterScheduler getScheduler() {
        return scheduler;
    }
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}

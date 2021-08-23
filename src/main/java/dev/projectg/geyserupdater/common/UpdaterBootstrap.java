package dev.projectg.geyserupdater.common;

import java.io.IOException;

public interface UpdaterBootstrap {

    void onDisable();

    void createRestartScript() throws IOException;

}

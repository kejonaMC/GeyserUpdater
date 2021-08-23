package dev.projectg.geyserupdater.common.update.identity.type;

import org.jetbrains.annotations.NotNull;

public class BuildNumber implements Identity<Integer> {

    private final int buildNumber;

    public BuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public @NotNull Integer value() {
        return buildNumber;
    }
}

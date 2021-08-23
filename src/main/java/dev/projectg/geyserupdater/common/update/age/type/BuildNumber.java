package dev.projectg.geyserupdater.common.update.age.type;

public class BuildNumber implements Identity<Integer> {

    private final int buildNumber;

    public BuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public Integer value() {
        return buildNumber;
    }
}

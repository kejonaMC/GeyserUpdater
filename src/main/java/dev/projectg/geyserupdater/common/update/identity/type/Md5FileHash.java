package dev.projectg.geyserupdater.common.update.identity.type;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Md5FileHash implements Identity<String> {

    private final String hash;

    public Md5FileHash(@Nonnull String md5Hash) {
        hash = Objects.requireNonNull(md5Hash);
    }

    @Override
    public @NotNull String value() {
        return hash;
    }
}

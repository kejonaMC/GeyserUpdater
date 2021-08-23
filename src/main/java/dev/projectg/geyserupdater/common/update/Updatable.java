package dev.projectg.geyserupdater.common.update;

import dev.projectg.geyserupdater.common.update.age.IdentityComparer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Updatable {

    @Nonnull public final String pluginIdentity;
    @Nonnull public final IdentityComparer<?, ?> identityComparer;
    @Nullable public final IdentityComparer<?, ?> hashComparer;
    @Nonnull public final String downloadUrl;
    @Nonnull public final Path outputFile;
    public final boolean autoCheck;
    public final boolean autoUpdate;

    /**
     * Immutable container for information regarding how to update something
     * @param name The name
     * @param identityComparer The {@link IdentityComparer} to check if the plugin is outdated
     * @param hashComparer The {@link IdentityComparer} to check if the hash of the downloaded file is acceptable.
     * @param downloadUrl The complete download link of the plugin
     * @param file If the Path is a file, the download will be written to that file. If the Path is a directory, the file will be written to that directory, and the filename will deduced from the link provided.
     */
    public Updatable(@Nonnull String name,
                     @Nonnull IdentityComparer<?, ?> identityComparer,
                     @Nullable IdentityComparer<?, ?> hashComparer,
                     @Nonnull String downloadUrl,
                     @Nonnull Path file,
                     boolean autoCheck,
                     boolean autoUpdate) {

        this.pluginIdentity = Objects.requireNonNull(name);
        this.identityComparer = Objects.requireNonNull(identityComparer);
        this.hashComparer = hashComparer;
        Objects.requireNonNull(downloadUrl);
        Objects.requireNonNull(file);
        this.autoCheck = autoCheck;
        this.autoUpdate = autoUpdate;


        // Remove / from the end of the link if necessary
        if (downloadUrl.endsWith("/")) {
            this.downloadUrl = downloadUrl.substring(0, downloadUrl.length() - 1);
        } else {
            this.downloadUrl = downloadUrl;
        }

        // Make sure the file linked is a jar
        if (!downloadUrl.endsWith(".jar")) {
            throw new IllegalArgumentException("Download URL provided for plugin '" + name + "' must direct to a file that ends in '.jar'");
        }

        // Figure out the output file name if necessary
        if (Files.isDirectory(file)) {
            this.outputFile = file.resolve(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));
        } else {
            this.outputFile = file;
        }
    }

    @Override
    public String toString() {
        return pluginIdentity;
    }
}

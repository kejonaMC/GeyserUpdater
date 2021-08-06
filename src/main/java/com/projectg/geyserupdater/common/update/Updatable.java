package com.projectg.geyserupdater.common.update;

import com.projectg.geyserupdater.common.update.age.AgeComparer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class Updatable {

    @Nonnull public final String pluginIdentity;
    @Nonnull public final AgeComparer<?, ?> ageComparer;
    @Nonnull public final String downloadUrl;
    @Nonnull public final String outputFileName;

    /**
     * @param pluginIdentity The plugin name, which can be used to identify the plugin.
     * @param ageComparer The age comparer to check if the plugin is outdated
     * @param downloadUrl The complete download link of the plugin
     * @param outputFileName The output file name. May be null to attempt to use the file name that the downloadUrl provides.
     */
    public Updatable(@Nonnull String pluginIdentity, @Nonnull AgeComparer<?, ?> ageComparer, @Nonnull String downloadUrl, @Nullable String outputFileName) {
        Objects.requireNonNull(pluginIdentity);
        Objects.requireNonNull(ageComparer);
        Objects.requireNonNull(downloadUrl);

        this.pluginIdentity = pluginIdentity;
        this.ageComparer = ageComparer;

        // Remove / from the end of the link if necessary
        if (downloadUrl.endsWith("/")) {
            this.downloadUrl = downloadUrl.substring(0, downloadUrl.length() - 1);
        } else {
            this.downloadUrl = downloadUrl;
        }

        // Make sure the file linked is a jar
        if (!downloadUrl.endsWith(".jar")) {
            throw new IllegalArgumentException("Download URL provided for plugin '" + pluginIdentity + "' must direct to a file that ends in '.jar'");
        }

        // Figure out the output file name if necessary
        if (outputFileName == null) {
            this.outputFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        } else {
            this.outputFileName = outputFileName;
        }
    }

    @Override
    public String toString() {
        return pluginIdentity;
    }
}

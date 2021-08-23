package dev.projectg.geyserupdater.common.update.identity.provider;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.update.identity.type.Md5FileHash;

import java.io.IOException;
import java.nio.file.Path;

public class FileHashProvider implements IdentityProvider<Md5FileHash> {

    Path file;

    public FileHashProvider(Path file) {
        this.file = file;
    }

    @Override
    public Md5FileHash getValue() {
        // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
        ByteSource byteSource = Files.asByteSource(file.toFile());

        // todo: non beta usage? I dont know.
        String md5Hash = null;
        try {
            md5Hash = byteSource.hash(Hashing.md5()).toString();
        } catch (IOException e) {
            UpdaterLogger.getLogger().error("Exception while getting md5 hash of file: " + file.getFileName());
            e.printStackTrace();
        }
        return new Md5FileHash(md5Hash);
    }
}

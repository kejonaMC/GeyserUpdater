package com.projectg.geyserupdater.common.update.age.type;

public class Md5FileHash implements Identity<String> {

    private final String hash;

    private Md5FileHash(String md5Hash) {
        hash = md5Hash;
    }

    @Override
    public String value() {
        return hash;
    }
}

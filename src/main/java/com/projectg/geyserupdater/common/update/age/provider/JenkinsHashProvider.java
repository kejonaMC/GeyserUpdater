package com.projectg.geyserupdater.common.update.age.provider;

import com.projectg.geyserupdater.common.update.age.type.Age;
import com.projectg.geyserupdater.common.update.age.type.Md5FileHash;

public class JenkinsHashProvider implements Age<Md5FileHash> {

    public JenkinsHashProvider(String link) {

    }


    @Override
    public Md5FileHash value() {
        return null;
    }
}

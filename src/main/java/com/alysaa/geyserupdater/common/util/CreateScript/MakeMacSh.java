package com.alysaa.geyserupdater.common.util.CreateScript;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MakeMacSh {
    public static void CreateMacSh() throws IOException {
        File file = new File("plugins/geyserupdater/startserver.sh");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes("#!/bin/sh\n");
        dos.writeBytes(":restart\n");
        dos.writeBytes("java -Xmx1G -jar BungeeCord.jar nogui\n");
        dos.writeBytes("Goto restart\n");
    }
}

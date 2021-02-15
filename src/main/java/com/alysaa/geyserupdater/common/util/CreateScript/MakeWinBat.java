package com.alysaa.geyserupdater.common.util.CreateScript;

import java.io.*;

public class MakeWinBat {
    public static void CreateWinBat() throws IOException {
        File file = new File("plugins/geyserupdater/startserver.bat");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes("@echo off\n");
        dos.writeBytes(":restart\n");
        dos.writeBytes("java -Xmx1G -jar BungeeCord.jar nogui\n");
        dos.writeBytes("Goto restart\n");
    }
}

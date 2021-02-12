package com.alysaa.geyserupdater.common.util;

import java.io.*;

public class MakeBat {
    public static void CreateBat() throws IOException {
        File file = new File("startserver.bat");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes("@echo off\n");
        dos.writeBytes(":restart\n");
        dos.writeBytes("java -Xms512M -Xmx1G -jar BungeeCord.jar nogui\n");
        dos.writeBytes("Goto restart\n");
    }
}

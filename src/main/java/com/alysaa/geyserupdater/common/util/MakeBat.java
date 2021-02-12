package com.alysaa.geyserupdater.common.util;

import java.io.*;

public class MakeBat {
    public static void CreateBat() throws IOException {
        File file = new File("startserver.bat");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes("testing >> testing\n");
        dos.writeBytes("testing\n");
    }
}

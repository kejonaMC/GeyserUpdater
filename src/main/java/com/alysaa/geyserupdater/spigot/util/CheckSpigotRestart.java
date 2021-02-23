package com.alysaa.geyserupdater.spigot.util;

import com.alysaa.geyserupdater.common.util.OSUtils;
import com.alysaa.geyserupdater.common.util.ScriptCreator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CheckSpigotRestart {
    public static void checkYml() {
        FileConfiguration spigot = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getWorldContainer(), "spigot.yml"));
        String scriptPath = spigot.getString("settings.restart-script");
        File script = new File(scriptPath);
        if (script.exists()) {
            System.out.println("[GeyserUpdater] Has detected a restart script.");
        } else {
            try {
                // Tell the createScript method that a loop is not necessary because spigot has a restart system.
                ScriptCreator.createScript(false);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            // Set the restart-script entry in spigot.yml to the one we just created
            String scriptName;
            if (OSUtils.isWindows()) {
                scriptName = "ServerRestartScript.bat";
            }
            else if (OSUtils.isLinux() || OSUtils.isMac()) {
                scriptName = "./ServerRestartScript.sh";
            }
            else {
                System.out.println("[GeyserUpdater] Your OS is not supported for script checking!");
                return;
            }
            spigot.set("settings.restart-script", scriptName);
            try {
                spigot.save("spigot.yml");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            System.out.println("[GeyserUpdater] Has set restart-script in spigot.yml to " + scriptName);
        }
    }
}

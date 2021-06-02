package com.projectg.geyserupdater.spigot.util;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.OSUtils;
import com.projectg.geyserupdater.common.util.ScriptCreator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CheckSpigotRestart {
    /**
     * Run {@link ScriptCreator#createRestartScript(boolean)} if an existing restart script is not defined in spigot.yml
     */
    public static void checkYml() {
        UpdaterLogger logger = UpdaterLogger.getLogger();
        // Do this early just as a check
        String scriptName;
        if (OSUtils.isWindows()) {
            scriptName = "ServerRestartScript.bat";
        } else if (OSUtils.isLinux() || OSUtils.isMacos()) {
            scriptName = "./ServerRestartScript.sh";
        } else {
            logger.warn("Your operating system is not supported! GeyserUpdater only supports automatic script creation for Linux, macOS, and Windows.");
            return;
        }
        FileConfiguration spigotConfigurationYamlFile = YamlConfiguration.loadConfiguration(new File(new File("").getAbsolutePath(), "spigot.yml"));
        String scriptPath = spigotConfigurationYamlFile.getString("settings.restart-script");
        File script = new File(scriptPath);
        if (script.exists()) {
            logger.info("An existing restart script has been detected!");
        } else {
            try {
                // Tell the createScript method that a loop is not necessary because spigot has a restart system.
                ScriptCreator.createRestartScript(false);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            // Set the restart-script entry in spigot.yml to the one we just created
            spigotConfigurationYamlFile.set("settings.restart-script", scriptName);
            try {
                spigotConfigurationYamlFile.save("spigot.yml");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            logger.warn("The config value 'restart-script' in spigot.yml has been set to " + scriptName);
            logger.warn("You must restart the server in order for the restart functionality to work!");
        }
    }
}

[![Build Status](https://ci.alysaa.net/job/GeyserUpdater/job/main/badge/icon)](https://ci.alysaa.net/job/GeyserUpdater/job/main/)
[![License](https://img.shields.io/badge/License-GPL-orange)](https://github.com/YHDiamond/GeyserUpdater/blob/main/LICENSE)
[![bStats Spigot](https://img.shields.io/bstats/servers/10202?color=yellow&label=Spigot%20servers)](https://bstats.org/plugin/bukkit/GeyserUpdater/10202)
[![bStats Bungee](https://img.shields.io/bstats/servers/10203?label=Bungee%20servers)](https://bstats.org/plugin/bungeecord/GeyserUpdater/10203)
[![bStats Velocity](https://img.shields.io/bstats/servers/10673?color=purple&label=Velocity%20servers)](https://bstats.org/plugin/velocity/GeyserUpdater/10673)
[![Discord](https://img.shields.io/discord/806179549498966058?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/xXzzdAXa2b)
[![Spigot page downloads](https://img.shields.io/spiget/downloads/88555?color=yellow&label=Spigot%20page%20downloads)](https://www.spigotmc.org/resources/geyserupdater.88555/)
[![Spigot reviews](https://img.shields.io/spiget/stars/88555?color=yellow&label=Spigot%20rating)](https://www.spigotmc.org/resources/geyserupdater.88555/)

# GeyserUpdater
### GeyserUpdater is a plugin that can update Geyser on Spigot, Bungeecord and Velocity manually or automatically.

[Here](https://discord.gg/xXzzdAXa2b) is our Discord link, you can come to get support, get GitHub feeds, or just hang out. Don't ping anyone that doesn't have the pingable role!

Note: This is NOT an official GeyserMC plugin. It is made to work with GeyserMC but it is not maintained or produced by GeyserMC. If you need support with this plugin, do not ask Geyser devs, and instead, go to our Discord server linked above.

## Downloading

Please download the plugin from [our Spigot page](https://www.spigotmc.org/resources/geyserupdater.88555/) so we can get an idea of how many people are downloading the plugin.

If you don't want to use the Spigot website you can download the plugin [our CI](https://ci.alysaa.net/job/GeyserUpdater/job/main) or use the actions button in the GitHub bar and download the zip with the GeyserUpdater jar inside.

## Installation

GeyserUpdater can be installed on Spigot, Bungeecord, and Velocity. Place the GeyserUpdater plugin file in the plugins folder and restart the server.

## Usage

| Commands | Permission |
| --- | --- |
| `/geyserupdate` | `gupdater.geyserupdate` |

After you run the `/geyserupdate` command or enable auto updating, it will check the current running Geyser version. If it's outdated, it will automatically download the latest Geyser build. Changes will only take place once the server has been shutdown and restarted.

Note that for Bungeecord and Velocity, if the server is not shutdown cleanly, the new version of Geyser will not be applied. 

## Config

### `Auto-Update-Geyser`:
*Default:* `false`  

If enabled, the plugin will check for a newer version of Geyser on startup and every 24 hours thereafter. If a newer version exists it will be downloaded. 

### `Auto-Restart-Server`:

*Default:* `false`  

If enabled, attempts to restart the server 10 seconds after a new version of Geyser has been successfully downloaded. 

- **Spigot:** Attempts with the restart feature of Spigot. A restart script must be defined in [spigot.yml](https://www.spigotmc.org/wiki/spigot-configuration/) in order for spigot's restart feature to work. If you are using a hosting provider, a [server wrapper](https://minecraftservers.fandom.com/wiki/Server_wrappers), or a systemd unit (or similar) to run the server, leave the `restart-script` value in spigot.yml blank. Your server should automatically restart without the script.

- **Bungeecord/Velocity:** The server will simply stop. If you are using a hosting provider, a [server wrapper](https://minecraftservers.fandom.com/wiki/Server_wrappers), or a systemd unit (or similar) to run the server, your proxy should automatically restart. If not, you can use a script to *start* the proxy that will make it start again once it has ended. 

### `Auto-Script-Generating`:

*Default:* `false`  

Do not use this if you are using a hosting provider, a [server wrapper](https://minecraftservers.fandom.com/wiki/Server_wrappers), or a systemd unit (or similar) to run the server.

When enabled, a restart script named ServerRestartScript.bat or ServerRestartScript.bat will be generated for you. It will attempt to use the same JVM flags that your server started with. Although it should typically work without issue, it is recommended to verify and modify it to your liking. 

- **Spigot:** The `restart-script` value in [spigot.yml](https://www.spigotmc.org/wiki/spigot-configuration/) will be automatically set to use the generated restart script. This will allow the spigot restart feature to work if you have `Auto-Restart-Server` enabled, and if you run `/restart`. If you run linux it is recommended to modify the script so that your server runs in a [screen](https://www.gnu.org/software/screen/) or [tmux](https://github.com/tmux/tmux/wiki) session, so that you can attach to the terminal of the restarted server. 

- **Bungeecord/Velocity:** You must use the generated script to start the proxy. This will make the server restart after it has stopped. 

### `Restart-Message-Players`:

*Default:* `'&2The server will restart in 10 Seconds!'`  

This is the message that is sent to all players when `Auto-Restart-Server` is going to restart the server in 10 seconds. 

## bStats
[Spigot stats](https://bstats.org/plugin/bukkit/GeyserUpdater/10202)

[Bungee stats](https://bstats.org/plugin/bungeecord/GeyserUpdater/10203)

[Velocity stats](https://bstats.org/plugin/velocity/GeyserUpdater/10673)

## Release History
* 1.3.0
    * Added Velocity support.
* 1.2.0
    * Added Restart script generating depended on OS, and setting it up on spigot.yml.
    * Added config version check.
* 1.1.0
    * Added message if GeyserUpdater has an available update on startup
* 1.0.0
    * Full release! (No changes but we decided we are ready for it now, we will still update more if we need to)
* 0.2.6
    * Add warning for players in config option
* 0.2.5
    * Added bStats support
* 0.2.4
    * Added auto restart on Spigot/Bungeecord after downloading update.
    * If you use this build you will need to regenerate config.yml file due to changes!
* 0.2.2
    * Mini-rework > Common classes.
    * Jenkins CI has been setup.
    * Pipeline has been added.
* 0.1.0
    * Merged Spigot & Bungeecord updater into 1 plugin.
    * Added maven


## Meta

The project is owned by:
- [Jens](https://github.com/Jens-Co)
- [YHDiamond](https://github.com/YHDiamond)
</br>

Special thanks to:
- [rtm516](https://github.com/rtm516), who helped us with basically everything. Without him this project wouldn't even have a readme.  
- [Konica](https://github.com/Konicai), who did alot of work on the updater code.

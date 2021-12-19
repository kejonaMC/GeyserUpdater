[![Build Status](https://ci.projectg.dev/job/GeyserUpdater/job/main/badge/icon)](https://ci.projectg.dev/job/GeyserUpdater/job/main/)
[![License](https://img.shields.io/badge/License-GPL-orange)](https://github.com/YHDiamond/GeyserUpdater/blob/main/LICENSE)
[![bStats Spigot](https://img.shields.io/bstats/servers/10202?color=yellow&label=Spigot%20servers)](https://bstats.org/plugin/bukkit/GeyserUpdater/10202)
[![bStats Bungee](https://img.shields.io/bstats/servers/10203?label=Bungee%20servers)](https://bstats.org/plugin/bungeecord/GeyserUpdater/10203)
[![bStats Velocity](https://img.shields.io/bstats/servers/10673?color=purple&label=Velocity%20servers)](https://bstats.org/plugin/velocity/GeyserUpdater/10673)
[![Discord](https://img.shields.io/discord/806179549498966058?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/gkU5AwGpJg)
[![Spigot page downloads](https://img.shields.io/spiget/downloads/88555?color=yellow&label=Spigot%20page%20downloads)](https://www.spigotmc.org/resources/geyserupdater.88555/)
[![Spigot reviews](https://img.shields.io/spiget/stars/88555?color=yellow&label=Spigot%20rating)](https://www.spigotmc.org/resources/geyserupdater.88555/)

# GeyserUpdater
### GeyserUpdater is a plugin that downloads and applies new builds of Geyser on Spigot/CraftBukkit, BungeeCord, and Velocity either manually or automatically.
##### Supports Minecraft 1.8 〜 1.18

If you'd like, click [here](https://discord.gg/xXzzdAXa2b) to join our Discord server! You can come here to receive support, ask about contributing, get GitHub feeds, or just simply hang out. Please do not ping anyone that doesn't have the "Pingable" role!

**Note:** This is _NOT_ an official GeyserMC plugin. It is made to work with Geyser, but it is not maintained or produced by GeyserMC. If you need support with this plugin, please do not ask the Geyser developers — instead, please go to our Discord server which is linked above.

## Downloading

Please download the plugin from [our Spigot page](https://www.spigotmc.org/resources/geyserupdater.88555/), so we can get an idea of how many people are downloading the plugin.

If you don't want to use the Spigot website, you can download the plugin from [our CI](https://ci.projectg.dev/job/GeyserUpdater/job/main), or use the actions button in the GitHub bar and download the zip with the GeyserUpdater jar inside.

## Installation

GeyserUpdater can be installed on [Spigot](https://www.spigotmc.org/wiki/buildtools/) / [CraftBukkit](https://www.spigotmc.org/wiki/buildtools/#compile-craftbukkit), [BungeeCord](https://www.spigotmc.org/wiki/bungeecord-installation/), and [Velocity](https://velocitypowered.com/downloads) (or any of their forks, such as [Paper](https://papermc.io/downloads) or [Waterfall](https://papermc.io/downloads#Waterfall)).

Simply place the GeyserUpdater `*.jar` file in your server's plugins folder and restart your server.

## Usage

| Commands | Permission |
| --- | --- |
| `/geyserupdate` | `gupdater.geyserupdate` |

After you run the `/geyserupdate` command or enable auto-updating, GeyserUpdater will check the currently-installed version of Geyser. If it's outdated, GeyserUpdater will automatically download the latest build of Geyser. Changes will only take place once the server has been shut down and restarted.

Please note that for BungeeCord and Velocity, if the server is not shut down cleanly, the new version of Geyser will **not** be applied.

## Configuration

### `Auto-Update-Geyser`:
*Default:* `false`  

If enabled, GeyserUpdater will check for new Geyser builds on server start, and on the interval specified by `Auto-Update-Interval`. If a new build exists, it will be downloaded.

### `Auto-Update-Interval`:
*Default:* `24`

The interval in hours between each auto update check.

### `Auto-Restart-Server`:
*Default:* `false`  

If enabled, GeyserUpdater will attempt to restart the server 10 seconds after a new version of Geyser has been successfully downloaded. 

- **Spigot:** Attempts to restart using Spigot's `/restart`. A restart script _must_ be defined in [spigot.yml](https://www.spigotmc.org/wiki/spigot-configuration/) in order for Spigot's restart feature to work properly. If you are using a hosting provider, a [server wrapper](https://minecraftservers.fandom.com/wiki/Server_wrappers), or a `systemd` unit (or similar) to run the server, leave the `restart-script` value in spigot.yml blank. Your server should automatically restart without needing a script.

- **BungeeCord/Velocity:** The server will simply stop. If you are using a hosting provider, a [server wrapper](https://minecraftservers.fandom.com/wiki/Server_wrappers), or a `systemd` unit (or similar) to run the server, your proxy should automatically restart. If not, you can use a script to *start* the proxy that will make it start again once it has stopped. 

### `Auto-Script-Generating`:
*Default:* `false`  

Do not use this if you are using a hosting provider, a [server wrapper](https://minecraftservers.fandom.com/wiki/Server_wrappers), or a `systemd` unit (or similar) to run your server.

When enabled, a restart script named `ServerRestartScript.sh` (macOS, Linux) or `ServerRestartScript.bat` (Windows) will be generated for you. It will attempt to use the same JVM flags that your server started with. Although it should typically work without any issue, it is recommended to verify and modify the generated script to your liking. 

- **Spigot:** The `restart-script` value in [spigot.yml](https://www.spigotmc.org/wiki/spigot-configuration/) will be automatically set to use the generated restart script. This will allow Spigot's restart feature to work if you have `Auto-Restart-Server` enabled, and if you run `/restart`. If you are running your server on Linux, it is recommended to modify the script so that your server starts in a [screen](https://www.gnu.org/software/screen/) or [tmux](https://github.com/tmux/tmux/wiki) session so that you can attach to the console of the server after it has restarted. 

- **BungeeCord/Velocity:** You must use the generated script to start the proxy. This will make the server restart after it has stopped. 

### `Restart-Message-Players`:
*Default:* `'&2This server will be restarting in 10 seconds!'`  

This is the message that is sent to all players when `Auto-Restart-Server` is going to restart the server in 10 seconds. 

## bStats
[Spigot stats](https://bstats.org/plugin/bukkit/GeyserUpdater/10202)

[Bungee stats](https://bstats.org/plugin/bungeecord/GeyserUpdater/10203)

[Velocity stats](https://bstats.org/plugin/velocity/GeyserUpdater/10673)

## Release History
* 1.6.1
    * Update for Geyser's resource system refactor (Build #943, commit [763743a](https://github.com/GeyserMC/Geyser/commit/763743a845796e4619ba596b82d4eabb39045448))
* 1.6.0
    * Update for Geyser 2.0, bump to Java 16 
* 1.5.0
    * This update breaks existing configurations. Please rename or delete the old one to generate a new configuration.
    * Add option to automatically check for updates on a custom interval.
    * Add debug logger config option.
    * All warn and error log messages are now coloured correctly (if the platform supports it).
* 1.4.0
    * Any branch of Geyser that is available on their Jenkins CI can now be updated.
    * Added support for CraftBukkit.
    * Added support for non-default Bukkit/Spigot world-containers
    * Important bugfixes, general increase in quality and better documentation.
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
- [ProjectG](https://github.com/ProjectG-Plugins)
</br>

Special thanks to:
- [rtm516](https://github.com/rtm516), who helped us with basically everything. Without him, this project wouldn't even have a README.
- [Karen/あけみ ](https://github.com/akemin-dayo), for their work in 1.4.0

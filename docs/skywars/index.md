![SkyWars](https://dabo.guru/logo/SkyWars.png)

### SkyWars: status
SkyWars is currently in maintenance mode, with small feature updates. It will definitely break in the Minecraft 1.13 update, due to how it stores arena files. I won't be investing a ton of time into fixing that until the final Spigot binaries for 1.13 are released, or at least until an API is released that's final.

I haven't been completely active lately with updates, but I'll be adding things when I can. Due to my limited time, I'll be implementing the features with the highest votes first, on the [feature poll](http://www.strawpoll.me/embed_1/10498111).

### Features
* Integrates seamlessly into any server, survival, creative or otherwise.
 * Join the SkyWars queue from any world, and you'll be transported back exactly where you came from once the game ends!
 * SkyWars saves gamemode, health, hunger, potion status, and pretty much anything else you'd need to keep track of.
* Use an unlimited number of arenas at the same time! The only limit is your server player capacity.
* Use SkyWars's kit system to let players pay money each game for a kit, and give access to extra kits to donors through permissions support!
* Craft your server's unique SkyWars experience!
 * Create custom kits just by filling your inventory with the items you want!
 * Create arenas without any entering items into config files! Just build the arena, set the spawns with **/sws** and [save it](https://dabo.guru/projects/skywars/creating-an-arena)!
 * Change any and all messages sent by SkyWars to players in messages.yml!
* Support for randomly filled chests!
* Support for portals to step into and join the queue.
* Custom economy rewards for winning games or killing in games.

### Installing SkyWars
* Server must be running at least Java 7.
* Server must be at least Minecraft version 1.7.8.
* Download SkyWars from BukkitDev, SpigotMC or GitHub Releases, links below.
* Put SkyWars.jar into your plugins directory, and restart the server.
* SkyWars is now functional with the two default arenas!

#### Documentation
* [SkyWars Functions Guide (Start Here)](https://dabo.guru/projects/skywars/functions-guide)
* [Commands and Permissions](https://dabo.guru/projects/skywars/commands-and-permissions)
* [Configuring SkyWars](https://dabo.guru/projects/skywars/configuring-skywars)
* [SkyWars Score System](https://dabo.guru/projects/skywars/score)
* [FAQ / Troubleshooting](https://dabo.guru/projects/skywars/faq)
* [Join Signs & Portals](https://dabo.guru/projects/skywars/signs-and-portals)

#### Customization
* [Creating a new Kit](https://dabo.guru/projects/skywars/creating-a-new-kit)
* [Creating a new Arena](https://dabo.guru/projects/skywars/creating-an-arena)
* [Adding chest randomization](https://dabo.guru/projects/skywars/configuring-chests)

#### Video Tutorial (credit to Koz4Christ!)

[![Koz4Christ SkyWars tutorial](https://img.youtube.com/vi/c6EqmpGnF40/0.jpg)](https://www.youtube.com/watch?v=c6EqmpGnF40)

#### Download Releases
* [BukkitDev/SkyWars](http://dev.bukkit.org/bukkit-plugins/skywars/)
* [SpigotMC/SkyWars](http://www.spigotmc.org/resources/skywars.167/)
* [GitHub/SkyWars](https://github.com/SkyWars/SkyWars/releases)

#### Other links
* [GitHub/SkyWars](https://github.com/SkyWars/SkyWars/)
* [MCStats Statistics](http://mcstats.org/plugin/SkyWars)
* [Full Changelog](https://dabo.guru/projects/skywars/changelog)
* [Full kits.yml Reference of valid values](https://dabo.guru/projects/skywars/reference/kits/)
* [Testing SkyWars (for developers)](https://dabo.guru/projects/skywars/testing-skywars)

### Translations / Localization
Pleyer messages have been fully translated to other languages by efforts from the community.

As of 2.2.2, all messages have been translated to English (en), Persian (fa), Traditional Chinese (hk), Hungarian (hu) and Dutch (nl).

Most messages, but not all, have been translated to Simplified Chinese (cn), Czech (cz), German (de), Danish (dk), Spanish (es), French (fr), Polish (pl), Portuguese (pt) and Russian (ru).

These incomplete translations were all at one point complete, though most lack 2-3 messages added in the most recent update.

Change the locale by setting **locale** in **main-config.yml**.

You can help SkyWars by creating translations for your language, or updating existing translations. See [SkyWars-Translations/Translating](https://github.com/SkyWars/SkyWars-Translations/wiki/Translating).

Translations can also be locally updated and tested by editing **messages.yml**.

#### MCStats / Plugin metrics
SkyWars uses two services to report statistics. The first service, MCStats / Plugin Metrics, reports data to http://mcstats.org every 15 minutes. As of version 2.1.7, the second service, plugin-statistics, reports a small subset of that data to https://dabo.guru every hour.

All data gathered by MCStats can be viewed at http://mcstats.org/plugin/SkyWars. As MCStats is a relatively well-known service, I'll let you visit their website for more information.

To opt out from MCStats, change **opt-out: false** to **opt-out: true** in **plugins/PluginMetrics/config.yml**.

The other service, plugin-statistics, is currently unique to SkyWars. It reports data every hour, starting one hour from server startup. The data reported consists solely of: the plugin name, the plugin version, the server version, the online player count, and an instance UUID. The plugin-statistics UUID is reset every server startup to a random number, and is not stored in any file.

All data gathered can be viewed at https://dabo.guru/statistics/skywars/.

For more information, and a copy of both the plugin-side and server-side code, visit [https://github.com/daboross/plugin-statistics](https://github.com/daboross/plugin-statistics#plugin-statistics).

To disable plugin-statistics, change **report-statistics: true** to **report-statistics: false** in **plugins/SkyWars/main-config.yml**.

#### SkyWars Report
When the **/sw report** command is used, SkyWars will submit a debug report for your server including server version, a list of all plugins, and other information to **gist.github.com**.

SkyWars will not publicly post information about your server to gist.github.com unless an admin uses the **/sw report** command. If you want to completely disable this functionality, add the following line to your **main-config.yml** file: **disable-report: true**.

### Credits
Both default maps, Skyblock Warriors and Water Warriors, were created by [SwipeShot](http://www.youtube.com/user/SwipeShot).

### Sponsors
We would like to thank [JetBrains](http://www.jetbrains.com/idea/) for their support of this project.

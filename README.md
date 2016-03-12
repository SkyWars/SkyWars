![SkyWars](https://dabo.guru/logo/SkyWars.png)

### SkyWars is back!

SkyWars is back! The first publicly released SkyWars plugin is now receiving intermittent updates, and has full support for Minecraft 1.9.

SkyWars v2.1.0 and above use a new arena backend, which comes with faster arena creation, and no longer requires SkyWarsBaseWorld.

If you were previously using SkyWars v2.0.1 or earlier, SkyWars will transfer all custom arenas to the new backend, so please **back up all SkyWars and arena data** before updating. If you were previously using SkyWars v1.4.5 or earlier, the update will also convert all user data from name-base to UUID-based storage.

### PSA: Vote on new features

If you use SkyWars, or plan on using SkyWars, please [vote](https://strawpoll.me/7055798) on what you most want to be added!

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
* Support for portals to step into and join the queue.
* Custom economy rewards for winning games or killing in games.

### Installing SkyWars
* Server must be running at least Java 7.
* Server must be at least Minecraft version 1.7.8.
* Download SkyWars from BukkitDev, SpigotMC or GitHub Releases, links below.
* Put SkyWars.jar into your plugins directory, and restart the server.
* SkyWars is now functional with the two default arenas!

#### Documentation
* [Commands and Permissions](https://dabo.guru/projects/skywars/commands-and-permissions)
* [Configuring SkyWars](https://dabo.guru/projects/skywars/configuring-skywars)
* [SkyWars Score System](https://dabo.guru/projects/skywars/score)
* [FAQ / Troubleshooting](https://dabo.guru/projects/skywars/faq)

#### Customization
* [Creating a new Kit](https://dabo.guru/projects/skywars/creating-a-new-kit)
* [Creating a new Arena](https://dabo.guru/projects/skywars/creating-an-arena)

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
All player messages fully translated to English and German, and partially translated to Spanish, French, Czech, Danish, Dutch, Polish, Portuguese and Russian. Partial translations were at one point complete, but don't have some of the newer messages translated.

Change the locale SkyWars uses by setting **locale** in **main-config.yml**.

To help out SkyWars by translating it into your local language, go to [SkyWars-Translations/Translating](https://github.com/SkyWars/SkyWars-Translations/wiki/Translating). Test out your localization as well by copying into **messages.yml**!

#### MCStats / Plugin metrics
SkyWars uses plugin metrics to keep track of people using the plugin.
All gathered data can be viewed at [http://mcstats.org/plugin/SkyWars](http://mcstats.org/plugin/SkyWars).

To opt out, change **opt-out: false** to **opt-out: true** in **plugins/PluginMetrics/config.yml**

#### SkyWars Report
SkyWars has the ability to use gist.github.com to generate a debug report for your server. When you use the **/sw report** command, SkyWars will gather information about your server, post it to gist.github.com, and give you a URL. SkyWars does not communicate with gist.github.com unless an admin uses the **/sw report** command. If you want to make it so that no one can use the **/sw report** command, add the following line to your **main-config.yml** file: **disable-report: true**.

### Credits
Both default maps, Skyblock Warriors and Water Warriors, were created by [SwipeShot](http://www.youtube.com/user/SwipeShot).

### Sponsors
We would like to thank [JetBrains](http://www.jetbrains.com/idea/) for their support of this project.

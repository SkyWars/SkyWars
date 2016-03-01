![SkyWars](https://dabo.guru/logo/SkyWars.png)

### No longer updated

SkyWars is no longer receiving dedicated updates and support.

There are several people who have volunteered to take over, and are working on updating the plugin with new features as we speak.

I am providing a limited number of support updates to ensure the plugin still works on the latest Spigot builds, but I may not have the time to answer questions in detail.

## Update warning for v2.*

SkyWars v2.* adds UUID support.

SkyWars v2.* builds require Minecraft version of 1.7.8 or higher.
If you need to use an earlier version of Minecraft, please download SkyWars v1.4.4 instead.

After updating to v2.*, SkyWars will migrate its storage from names to UUIDs as users log into your server.
This way there is no long transfer period, and it supports all properly configured proxies (bungeecord, lilypad, etc.).

## Features
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

## Installing SkyWars
* Server must be running at least Java 7.
* For SkyWars v2.0.0 and higher, server must be at least Minecraft version 1.7.8
* Download SkyWars from BukkitDev, SpigotMC or GitHub Releases, links below.
* Put SkyWars.jar into your plugins directory, and restart the server.
* Customize SkyWars by creating new kits and arenas unique to your server! See the links below for more information.

### Documentation
* [Commands and Permissions](https://dabo.guru/projects/skywars/commands-and-permissions)
* [Configuring SkyWars](https://dabo.guru/projects/skywars/configuring-skywars)
* [SkyWars's limited score system](https://dabo.guru/projects/skywars/score)

### Customization
* [Creating a new Kit](https://dabo.guru/projects/skywars/creating-a-new-kit)
* [Creating a new Arena](https://dabo.guru/projects/skywars/creating-an-arena)

### Debugging
* [SkyWars Worlds](https://dabo.guru/projects/skywars/skywars-worlds)
* [Troubleshooting SkyWars](https://dabo.guru/projects/skywars/troubleshooting)
* [Submitting a ticket](https://dabo.guru/projects/skywars/submitting-a-ticket)
* [Version Changes](https://dabo.guru/projects/skywars/changelog)

### For developers
* [GitHub/SkyWars](https://github.com/SkyWars/SkyWars/)
* [SkyWars API Overview](https://dabo.guru/projects/skywars/api-overview)
* [Testing SkyWars](https://dabo.guru/projects/skywars/testing-skywars)
* [MCStats Statistics](http://mcstats.org/plugin/SkyWars)

### Translations / Localization
* All player messages fully translated to English and German, and partially translated to Spanish, French, Czech, Danish, Dutch, Polish, Portuguese and Russian. Partial translations were at one point complete, but don't have some of the newer messages translated.
* Change the locale SkyWars uses by setting 'locale' in **main-config.yml**.
* To help out SkyWars by translating it into your local language, go to [SkyWars-Translations/Translating](https://github.com/SkyWars/SkyWars-Translations/wiki/Translating). Test out your localization as well by copying into `messages.yml`!

### MCStats / Plugin metrics
SkyWars uses plugin metrics to keep track of people using the plugin.
All gathered data can be viewed at [http://mcstats.org/plugin/SkyWars](http://mcstats.org/plugin/SkyWars).
To opt out, change **opt-out: false** to **opt-out: true** in **plugins/PluginMetrics/config.yml**

### SkyWars Report
SkyWars has the ability to use gist.github.com to generate a debug report for your server. When you use the
**/sw report** command, SkyWars will gather information about your server, post it to gist.github.com, and give you a
URL. SkyWars does not communicate with gist.github.com unless an admin uses the **/sw report** command. If you want to
make it so that no one can use the **/sw report** command, add the following line to your **main-config.yml** file:
**disable-report: true**

## Bug reports, Feature requests and other Questions
Please use the SkyWars issue tracker for all bug reports, feature requests and general questions.

See [submitting a ticket](https://dabo.guru/projects/skywars/submitting-a-ticket) for instructions on how to do this.

## Credits
Full credit for the default map, Skyblock Warriors, goes to [SwipeShot](http://www.youtube.com/user/SwipeShot).

## Sponsors
We would like to thank JetBrains for their support of this project.

[![JetBrains](https://www.jetbrains.com/idea/docs/logo_intellij_idea.png)](http://www.jetbrains.com/idea/).

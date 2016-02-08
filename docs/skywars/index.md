![SkyWars](https://dabo.guru/logo/SkyWars.png)

### No longer updated

SkyWars is no longer receiving updates and support. The project was fun while it lasted, but it is no longer. If you wish to take over SkyWars, please contact me at daboross@daboross.net.

### Update warning for v2.*

SkyWars v2.* adds UUID support.

SkyWars v2.* builds require Minecraft version of 1.7.8 or higher.
If you need to use an earlier version of Minecraft, please download SkyWars v1.4.4 instead.

After updating to v2.*, SkyWars will migrate its storage from names to UUIDs as users log into your server.
This way there is no long transfer period, and it supports all properly configured proxies (bungeecord, lilypad, etc.).

### Features
* Automatically create new arenas every time enough people join the queue.
* Have *as many arenas* going at the same time as you want!
 * Whenever the configured amount of people in the queue (default to 4), a new arena is created automatically!
 * There is no limit besides your server's capacity to how many arenas SkyWars can create.
* Teleports people to lobby location when they leave or die.
* When players walk near a set portal, they will be automatically added to the queue.
* Clears player inventories when the join a game and when the leave.

### Basic Installation
* Make sure your server is running Java 7. SkyWars **will not function** with Java 6.
* Download the SkyWars.jar for your server version, and put it in your plugins/ directory.
 * SkyWars v1.4.4 supports minecraft 1.5.2, 1.6.4 and 1.7.2.
 * SkyWars v2.0.1 supports minecraft 1.7.8 and higher.
* Restart your server. SkyWars will automatically create 2 new worlds. SkyWarsBaseWorld and SkyWarsArenaWorld.
* Go to where you want the lobby to be and use **/sw setlobby**.

### Useful links
* [Commands and Permissions](https://dabo.guru/projects/skywars/commands-and-permissions)
* [Configuration Guide](https://dabo.guru/projects/skywars/configuring-skywars)
* [Setting up a new Arena](https://dabo.guru/projects/skywars/creating-an-arena)
* [Worlds Created](https://dabo.guru/projects/skywars/skywars-worlds)
* [Common Issues / Troubleshooting](https://dabo.guru/projects/skywars/troubleshooting)
* [GitHub](https://github.com/SkyWars/SkyWars)
* [MCStats Statistics](http://mcstats.org/plugin/SkyWars)
* [TeamCity Development Builds - CI Server](http://ci.dabo.guru/p/SkyWarsParent)
* [Download Latest Snapshot](http://ci.dabo.guru/d/SkyWarsParent_SkyWars_MainBuild/SkyWars.jar)

#### MCStats / Plugin metrics
SkyWars uses plugin metrics to keep track of people using the plugin.
All gathered data can be viewed at [http://mcstats.org/plugin/SkyWars](http://mcstats.org/plugin/SkyWars).
To opt out, change **opt-out: false** to **opt-out: true** in **plugins/PluginMetrics/config.yml**

#### SkyWars Report
SkyWars has the ability to use gist.github.com to generate a debug report for your server. When you use the
**/sw report** command, SkyWars will gather information about your server, post it to gist.github.com, and give you a
URL. SkyWars does not communicate with gist.github.com unless an admin uses the **/sw report** command. If you want to
make it so that no one can use the **/sw report** command, add the following line to your **main-config.yml** file:
**disable-report: true**


### Bug reports, Feature requests and other Questions
Please use the SkyWars issue tracker for all bug reports, feature requests and general questions.

See [submitting a ticket](https://dabo.guru/projects/skywars/submitting-a-ticket) for instructions on how to do this.

### Map Credit
Full credit for the default map, Skyblock Warriors, goes to [SwipeShot](http://www.youtube.com/user/SwipeShot)

### Sponsors/other
This may be an almost empty section, but we would like to thank JetBrains for their support of this project.

[![JetBrains](https://www.jetbrains.com/idea/docs/logo_intellij_idea.png)](http://www.jetbrains.com/idea/)

### Translating!
We need help translating SkyWars! To help out, see [SkyWars-Translations/Translating](https://github.com/SkyWars/SkyWars-Translations/wiki/Translating).

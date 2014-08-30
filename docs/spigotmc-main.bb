[IMG]https://dabo.guru/logo/SkyWars.png[/IMG]

[SIZE=4][B]Update warning![/B][/SIZE]

SkyWars v2.0.0 will fail to start on any craftbukkit version below 1.7.8. If you need to use an earlier version of Minecraft, please download SkyWars v1.4.4 instead.

SkyWars v2.0.0 adds UUID support, which is why it requires a newer CraftBukkit version.

After updating to v2.0.0, SkyWars will migrate it's storage from names to UUIDs as users log into your server.

This way there is no long transfer period, and it supports any proxies (bungeecord, lilypad), as long as they forward UUIDs to CraftBukkit.

[SIZE=4][B]Features[/B][/SIZE]
[LIST]
[*]Automatically create new arenas every time enough people join the queue.
[*]Have [I]as many arenas[/I] going at the same time as you want!
[LIST]
[*]Whenever the configured amount of people in the queue (default to 4), a new arena is created automatically!
[*]There is no limit besides your server's capacity to how many arenas SkyWars can create.
[/LIST]
[*]Teleports people to lobby location when they leave or die.
[*]When players walk near a set portal, they will be automatically added to the queue.
[*]Clears player inventories when the join a game and when the leave.
[/LIST]

[SIZE=4][B]Basic Installation[/B][/SIZE]
[LIST]
[*]Make sure your server is running Java 7. SkyWars will not function with Java 6.
[*]Download the SkyWars.jar file, and put it in your plugins/ directory.
[LIST]
[*]SkyWars v1.4.4 supports minecraft 1.5.2, 1.6.4 and 1.7.2.
[*]SkyWars v2.0.0 supports minecraft 1.7.8, 1.7.9 and 1.7.10.
[/LIST]
[*]Restart your server. SkyWars will automatically create 2 new worlds. SkyWarsBaseWorld and SkyWarsArenaWorld.
[*]Go to where you want the lobby to be and use /sw setlobby.
[/LIST]

[SIZE=4][B]Useful links[/B][/SIZE]
[LIST]
[*][URL='https://dabo.guru/projects/skywars/commands-and-permissions']Commands and Permissions[/URL]
[*][URL='https://dabo.guru/projects/skywars/configuring-skywars']Configuration Guide[/URL]
[*][URL='https://dabo.guru/projects/skywars/creating-an-arena']Setting up a new Arena[/URL]
[*][URL='https://dabo.guru/projects/skywars/skywars-worlds']Worlds Created[/URL]
[*][URL='https://dabo.guru/projects/skywars/troubleshooting']Common Issues / Troubleshooting[/URL]
[*][URL='https://github.com/SkyWars/SkyWars']GitHub[/URL]
[*][URL='http://mcstats.org/plugin/SkyWars']MCStats Statistics[/URL]
[*][URL='http://ci.dabo.guru/p/SkyWarsParent']TeamCity Development Builds - CI Server[/URL]
[*][URL='http://ci.dabo.guru/d/SkyWarsParent_SkyWars_MainBuild/SkyWars.jar']Download Latest Snapshot[/URL]
[/LIST]

[B][SIZE=4]MCStats / Plugin metrics[/SIZE][/B]
SkyWars uses plugin metrics to keep track of people using the plugin. All gathered data can be viewed at [URL]http://mcstats.org/plugin/SkyWars[/URL]. To opt out, change opt-out: false to opt-out: true inplugins/PluginMetrics/config.yml

[B][SIZE=4]SkyWars Report[/SIZE][/B]
SkyWars has the ability to use gist.github.com to generate a debug report for your server. When you use the /sw report command, SkyWars will gather information about your server, post it to gist.github.com, and give you a URL. SkyWars does not communicate with gist.github.com unless an admin uses the /sw report command. If you want to make it so that no one can use the /sw report command, add the following line to your main-config.ymlfile: disable-report: true

[SIZE=4][B]Bug reports, Feature requests and other Questions[/B][/SIZE]
Please use the SkyWars issue tracker for all bug reports, feature requests and general questions.

See [URL='https://dabo.guru/projects/skywars/submitting-a-ticket']submitting a ticket[/URL] for instructions on how to do this.

[SIZE=4][B]Map Credit[/B][/SIZE]
Full credit for the default map, Skyblock Warriors, goes to [URL='http://www.youtube.com/user/SwipeShot']SwipeShot[/URL]

[SIZE=4][B]Sponsors/other[/B][/SIZE]
This may be an almost empty section, but we would like to thank JetBrains for their support of this project.

[URL='http://www.jetbrains.com/idea/'][IMG]http://www.jetbrains.com/idea/docs/logo_intellij_idea.png[/IMG][/URL]

[SIZE=4][B]Translating![/B][/SIZE]
We need help translating SkyWars! To help out, see [URL='https://github.com/SkyWars/SkyWars-Translations/wiki/Translating']SkyWars-Translations/Translating[/URL].

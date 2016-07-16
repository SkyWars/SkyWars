SkyWars Functions Guide
=======================

This is a work-in-progress guide to how SkyWars functions / starts games.

First, anyone in the game can join the Queue by using the `/sw join` command, by clicking on a [Join Sign](https://dabo.guru/projects/skywars/signs-and-portals), or by stepping on a [Join Portal](https://dabo.guru/projects/skywars/signs-and-portals).

When someone joins the queue, nothing immediately happens, but under certain conditions, a timer may begin:

If there are at least the minimum number of players in the queue (including the player who just joined), a timer for 200 seconds. If a timer was already running (started by the last player who joined the queue), that timer is canceled. Only the latest game timer stays running.

If there are the maximum number of players in the queue (including the player who joined), a timer starts for 30 seconds.

If there are already the maximum number of players in the queue before the player tries to join, the player will be added to a "secondary" queue, sent a short message, and then added to the regular queue once a game starts.

#### The Game Timer

All game timers counts down every second until it reaches zero, at which point players are teleported into the arena, and the game starts.

In addition to this however, the game timer will broadcast messages at intervals configured in seconds left on the timer. The default value for this configuration setting (`game-timer.times-to-message-before-start`) is the list [600, 300, 180, 60, 45, 30, 15, 5, 3, 2, 1], each in seconds. When the timer reaches each of these intervals left, it will broadcast a message to either the entire server, or the players in the queue, depending on the `only-broadcast-to-players-in-arena.starting-in-start-timer` setting.

When the timer reaches 30 seconds remaining (`game-timer.time-before-start-to-freeze-map-votes`), the plugin will start to create the arena by copying small chunks from the stored blocks file to a new location in the SkyWarsArenaWorld. The configuration option is named "time-before-start-to-freeze-map-votes", not because the plugin has support for voting on maps, but because that's what the setting will do once map voting is implemented.

When the timer reaches zero, SkyWars will have finished copying all block sections to SkyWarsArenaWorld, and all the players in the queue will be teleported to the arena.

#### SkyWars Gameplay

Once inside an arena, players will gather resources, find weapons in chests, and fight it out to the last man standing.

While leaving a game is possible using the **/sw leave** command, any players who leave do forfeit the game, and will receive a score penalty akin to dying.

All players who die and respawn or leave are teleported back to the locations they were originally in when the game started, and their original inventories, health, hunger, and potion status effects are restored.

The game ends when the only players remaining are on a single team. When this happens, the winners are also teleported back to their original positions, and their original inventories, health, hunger and potion status effects are restored. When the game ends, any players who haven't hit respawn yet (are still on the death screen) are forcefully respawned by SkyWars.

The winners of the game also receive 10 economy coins (`economy.win-reward`), and 7 SkyWars points (`points.win-point-diff`).

#### SkyWars Points

Players who win or kill in SkyWars games will receive SkyWars points. People who die or leave SkyWars games will have SkyWars points subtracted from their score.

These points aren't meant to have any economy value, and are purely for creating a leaderboard of sorts. To this end, SkyWars generates a list of top-10 players with the highest scores, as well as a rank for every player on the server.

To look at the top 10 players, the command **/sw top** is used. **/sw rank <player>** is used to look at a player's rank and score, or, if **<player>** is omitted, to look at your own rank and score.

As well as viewing via commands, you can also insert a player's score or rank into all of their chat messages using a custom chat plugin. To do this, add **{skywars.userscore}**, **{skywars.userrank}**, or both to your chat format. SkyWars will automatically replace each one respectively with a player's score number or rank number.

#### More Information / Next Steps

This concludes the SkyWars Functions Guide! See the links below:

* [Commands and Permissions](https://dabo.guru/projects/skywars/commands-and-permissions)
* [Configuring General SkyWars](https://dabo.guru/projects/skywars/configuring-skywars)
* [SkyWars Score System Details](https://dabo.guru/projects/skywars/score)
* [Join Sign & Join Portal Details](https://dabo.guru/projects/skywars/signs-and-portals)
* [FAQ / Troubleshooting](https://dabo.guru/projects/skywars/faq)


* [Creating a custom Kit](https://dabo.guru/projects/skywars/creating-a-new-kit)
* [Creating a custom Arena](https://dabo.guru/projects/skywars/creating-an-arena)
* [Adding chest randomization to arenas](https://dabo.guru/projects/skywars/configuring-chests)

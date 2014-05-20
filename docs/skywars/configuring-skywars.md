Configuring SkyWars - main-config.yml
=====================================

This is documentation for configuring `plugins/SkyWars/main-config.yml`.

For documentation on configuring a file in the `arenas/` directory, see http://dabo.guru/skywars/configuring-arenas.

For documentation on configuring `arena-parent.yml`, see http://dabo.guru/skywars/configuring-parent.

For documentation on configuring `kits.yml`, see http://dabo.guru/skywars/configuring-kits.


```yaml
## This is the configuration version. Unless you really want to mess up your
## configuration, you should not change this.
config-version: 1

## Whether or not to enable debug mode. Currently there are minimal debug calls,
## but in the future more may be added. There is no need to enable this unless
## you are asked to by SkyWars support (daboross).
debug: true

## This represents the order that the arenas are started in. Whenever enough
## people join the queue, a new arena is chosen either at RANDOM, or ORDERED.
## Setting this value to RANDOM will make a random arena be chosen from
## enabled-arenas when a new game starts. Setting it to ORDERED will make each
## enabled arena be started in sequence.
arena-order: RANDOM

## This is the string that will prefix all SkyWars messages sent to players.
message-prefix: '&8[&cSkyWars&8]&a '

## Whether or not to save inventories of players who are joining SkyWars games.
## When this is false, each player's inventory will be wiped when the join a
## game and when the leave a game. When this is true the inventory is stored
## in memory and then wiped when the join a game, then restored when they leave.
## Note that if the server crashes when games are running, or SkyWars is
## forcefully stopped, the inventories of the players in any game running at
## that time will not be recoverable.
save-inventory: true

## A list of enabled arenas. Each of the items in this list corresponds to a
## file in the arenas/ folder. When SkyWars loads, it will take each item in
## this list, look for a file in the arenas/ folder who's name is this followed
## by '.yml', then load it into the enabled arenas list.
enabled-arenas:
- skyblock-warriors

## Sub section for point storage
points:

  ## Whether to enable point storage or not. If point storage is not enabled,
  ## none of the following settings will take effect
  enable-points: true

  ## The amount of points to add to a player's score when they win a game.
  win-point-diff: 7

  ## The amount of points to add to a player's score when they die in a game.
  death-point-diff: -2

  ## The amount of points to add to a player's score when they kill someone in
  ## a game.
  kill-point-diff: 1

## The distance apart arenas will be in the SkyWarsArenaWorld. If you have
## created bigger arenas, such as ones that are bigger than 100 blocks, you may
## want to increase this value. Note that this is the distance from the minimum
## corner of one arena to the minimum corner of the next.
arena-distance-apart: 200

## Sub section for command whitelist
command-whitelist:

  ## Whether or not to filter commands that are run inside sky games.
  whitelist-enabled: true

  ## Whether to treat the whitelist as a 'blacklist'. If this is true, then all
  ## commands will be allowed in sky games *except* for ones that are in this list.
  ## If this is false, then only the commands in this list will be allowed, and
  ## all other ones will be blocked in sky games.
  treated-as-blacklist: false

  ## List of commands to allow / block in sky games.
  whitelist:
  - /skywars
  - /sw
  - /me
```

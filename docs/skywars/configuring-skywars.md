Configuring SkyWars - main-config.yml
=====================================

This is documentation for configuring `plugins/SkyWars/main-config.yml`.

To create a new kit, go to https://dabo.guru/projects/skywars/creating-a-new-kit.

To create a new arena, go to https://dabo.guru/projects/skywars/creating-an-arena.

To configure randomized chests, go to https://dabo.guru/projects/skywars/configuring-chests.

```yaml
# This is the configuration version. Unless you really want to mess up your
# configuration, you should not change this.
config-version: 2

# Whether or not to enable debug mode. In debug mode, SkyWars will print a
# huge amount of information to the server console. There is usually no need
# to enable this unless you are asked to by the developer.
debug: false

# When enabled, SkyWars will report statistics to https://dabo.guru, using the
# custom "plugin-statistics" system. This is similar to PluginMetrics/MCStats,
# but reports vastly less data, and does not use any persistent GUID for the
# server.
#
# If enabled, every hour, starting one hour from server startup, the plugin
# will send the plugin version, server version, online player count, and an
# instance UUID to https://dabo.guru/statistics/skywars/. The instance UUID is
# reset every server startup, and is not stored in any persistent files.
#
# If disabled, no data will be sent.
#
# For more information, see https://github.com/daboross/plugin-statistics.
report-statistics: true

# This represents the order that the arenas are started in. Whenever enough
# people join the queue, a new arena is chosen either at RANDOM, or ORDERED.
#
# Setting this value to RANDOM will make a random arena be chosen from
# enabled-arenas when a new game starts. Setting it to ORDERED will make each
# enabled arena be started in sequence.
arena-order: RANDOM

# This is the string that will prefix all SkyWars announcements.
message-prefix: '&8[&cSkyWars&8]&a '

# Whether or not to save inventories of players who are joining SkyWars games.
#
# When this is false, each player's inventory will be wiped when the join a
# game and when the leave a game. When this is true the inventory is stored in
# memory and then wiped when the join a game, then restored when they leave.
#
# Note that if the server crashes when games are running, or SkyWars is
# forcefully stopped, the inventories of the players in any game running at
# that time will not be recoverable.
save-inventory: true

# Whether or not experience level will be saved along with inventory.
#
# This won't work without save-inventory also being true. See save-inventory
# for more information.
save-experience: true

# Whether or not position, gamemode, health, hunger, and other survival player
# properties are saved when joining a SkyWars games (and restored upon leaving).
#
# This will teleport players back to their original locations, instead of
# going to a lobby when finished - and will restore their gamemode, health,
# hunger, exhaustion and stamina. This won't work without save-inventory also
# being true.
#
# If this is false, players are teleported to the lobby, and only their
# inventory (and experience) are restored. If false, gamemode, health and
# hunger are completely reset upon leaving a game.
save-position-gamemode-health: true

# A list of enabled arenas. Each of the items in this list corresponds to a
# file in the arenas/ folder. When SkyWars loads, it will take each item in
# this list, look for a file in the arenas/ folder who's name is this followed
# by '.yml', then load it into the enabled arenas list.
enabled-arenas:
- skyblock-warriors
- water-warriors

# Locale to use for all player messages. Currently available locales:
# cz, de, dk, en, es, fr, nl, pl, pt and ru.
# Note that some translations are incomplete. You can also modify
# `messages.yml` to make your own translation (if you do, be sure to set
# auto-update to false in messages.yml).
locale: en

# A list of minecraft gamerules to set in the world where arenas are run. By
# default, only 'doDaylightCycle' is set - this stops day and night from
# changing in the arena world.
skywars-arena-gamerules:
  doDaylightCycle: 'false'

# Whether or not players should be immediately respawned after dying. If true,
# the death/respawn screen is skipped and players are teleported immediately
# after dying. If false, players will have the respawn screen shown as normal.
# When false, players will still be force respawned when the game ends if they
# haven't clicked the respawn button by then.
skip-respawn-screen: true

# Sub section for score storage
points:

  # Whether to enable point storage or not. If point storage is not enabled,
  # none of the following settings will take effect
  enable-points: true

  # The number of points to add to a player's score when they win a game.
  win-point-diff: 7

  # The number of points to add to a player's score when they die in a game.
  death-point-diff: -2

  # The number of points to add to a player's score when they kill someone in
  # a game
  kill-point-diff: 1

  # Number of seconds between saving the score to hard disk
  # In JSON storage, this is the number of seconds between saving the entire
  # score file to the hard disk.
  #
  # In SQL storage, this is the number of seconds between saving only
  # uncommitted data to the SQL server. In SQL, this is also the number of
  # seconds between leaderboard updates.
  #
  # Recommended to be set to values 10-30 if using SQL. 200-500 for JSON.
  max-save-interval: 300

  # If true, use the SQL storage backend. If false, the JSON storage is used.
  #
  # Note: When using SQL backend, for most efficiency, set max-save-interval
  # to 20 SQL saving works differently than json saving, so max-save-interval
  # values from 20-100 seconds work best.
  use-sql: true

  # SQL login info
  sql:
    # Host to connect to
    host: 127.0.0.1
    # Port to connect to
    port: 3306
    # Database to store on. Data is stored in a `skywars_user` table in this
    # database.
    database: minecraft
    # Username to login as. Using `root` is not recommended. Instead you
    # should set up a more restricted user.
    username: root
    # Password to login with.
    password: aComplexPassword

    # Time in seconds between updating the ranks of each individual. This is
    # the rank shown in `/sw rank`, and this will update the ranks of everyone
    # on the server at the given interval. However, updating each individual's
    # rank is a more costly operation than updating the top 10 ranks, so it is
    # done on a separate timer. (top 10 ranks are updated according to
    # max-save-interval).
    individual-rank-update-interval: 120

# Subsection for the economy Vault hook
economy:

  # Whether or not to enable hooking into Vault for economy.
  # If disabled, no rewards will be given, and kits with costs will also be
  # disabled.
  enable-economy: true

  # Amount of money to give a player when they kill someone in a game.
  kill-reward: 10

  # Amount of money to give s player when they win a game.
  win-reward: 10

  # Whether or not to tell players when they get a win reward.
  reward-messages: true

# The distance apart arenas will be in the SkyWarsArenaWorld. If you have
# created bigger arenas, such as ones that are bigger than 100 blocks, you may
# want to increase this value.
#
# Note that this is the distance from the center of one arena to the center of
# the next, not the inner edges.
arena-distance-apart: 200

arena-copying:
  # If a multi-operation arena copy is enabled, this will be how many blocks
  # each copy operation copies. If you feel that SkyWars is causing your server
  # lag on game start and game end, adjusting this value and/or
  # time-before-start-to-start-arena-copy-operation may help.
  number-of-blocks-to-copy-at-once: 500

# Command whitelist sub-section
command-whitelist:

  # Whether or not to enable the command whitelist. If enabled, commands use
  # will be restricted when in a game.
  whitelist-enabled: true

  # If true, treat the values in `whitelist` as a blacklist, blocking them and
  # only them in game.
  # If false, treat the values in `whitelist` as a whitelist, blocking all
  # commands *except them* in game.
  treated-as-blacklist: false

  # Commands to whitelist/blacklist in game
  whitelist:
  - /skywars
  - /sw
  - /me

# Join sign appearance
#
# When the queue length changes or a game is started, every sign is updated
# with "{name}" set to the next arena's name, "{count}" set to the number of
# players in the queue, and "{max}" set to the maximum number of players to
# join the arena before SkyWars will start the game.
#
# This must be a list of four lines used for the four lines on the sign. If you
# want less than four lines, simply have the last lines be blank.
#
# When placing a sign, the sign will turn into a SkyWars Join Sign if the first
# "static" line of the template matches. "static" in this case means there are
# no changing elements such as "{name}", "{count}" and "{max}".
#
# For example, if you have the following configuration:
# join-sign-lines:
# - 'Players: {count}'
# - 'Arena: {name}'
# - 'Waiting for: {max}'
# - 'Click to join!'
#
# Then in order to create a join sign, you would have to write a sign with the
# first three lines blank, and the last line with "Click to join!" in it.
#
# With the default configuration, simply make a sign with the first line being
# "[SkyWars]" and it will turn into a join sign.
#
# NOTE! When you change this setting, all currently existing join signs will
# no longer function or update - SkyWars uses the lines in this setting to
# tell which signs are still join signs when updating. (all signs are also
# stored in locations.yml, but SkyWars double-checks them with the join sign
# line format to avoid overwriting changed signs).
join-sign-lines:
- '&8[&cSkyWars&8]'
- '&8Next Game: &c{name}'
- '&8Players: &2{count}&3/&2{max}'
- '&cClick to join!'

# Message limiting - only broadcasting some messages to players who are
# involved in the game the message is about.
only-broadcast-to-players-in-arena:
  # If true, game starting messages will only be shown to those in the game
  # which is starting.
  start: false
  # If true, death messages of players in a game will only be shown to alive
  # players in that game.
  death: true
  # If true, end/winning messages for games will only be shown to the
  # player(s) who won the game, and the last player who died.
  end: false
  # If true, "start timer" messages will only be shown to to the players who
  # are already in the arena queue.
  starting-in-start-timer: false

# Kit GUI subsection
kit-gui:
  # The Kit GUI is an visual "inventory" menu shown which contains one "totem"
  # item per kit.

  # If true, kits unavailable to the player (either due to lack of permission
  # or lack of funds) will be shown in the bottom of the kit GUI, with a
  # 9-space row separating them from the available kits.
  show-unavailable-kits: true

  # By default, the kit GUI is accessible via a command: `/sw kitgui`. If this
  # is enabled, the `kitgui` command will no longer exist, and `/sw kit` will
  # launch the kit GUI rather than show a list of kits available.
  replace-kit-command: false

  # If this is true, the kit GUI will be launched (shown to the player)
  # whenever the player joins the queue or clicks a join sign. Note that the
  # GUI will be shown even if the player is already in the queue: if this is
  # enabled, join signs can also be used as a "kit sign" which launches the
  # kit GUI when clicked.
  auto-show-on-join: true

# Settings related to the game timer, the SkyWars start timer.
# All sub-settings under 'game-timer' are in seconds
game-timer:
  # Time before the game starts after the maximum number of players have joined.
  time-till-start-after-max-join: 30
  # Time before the game starts after the minimum number of players have joined.
  time-till-start-after-any-join: 200
  # Time before the game starts to start copying the arena, in preparation of the game starting.
  time-before-start-to-start-arena-copy-operation: 45
  # Currently unused. In the future, this will be how long after players are teleported into the arena before they are
  # allowed to move.
  time-after-start-to-freeze-players: 5
  # This is a list of times, in seconds, to broadcast "game starting in X minutes/seconds" messages.
  #
  # Each of these times is in seconds before the game starts.
  #
  # For each time, if the time is divisible by 60, the message will be displayed as "start in X minutes". If it isn't,
  # the message will be "starting in X seconds."
  times-to-message-before-start:
  - 600
  - 300
  - 180
  - 60
  - 45
  - 30
  - 15
  - 5
  - 3
  - 2
  - 1

# Hooks available to hook into separate plugins
hooks:
  # This hook is supposed to be for hooking into the Multiverse plugin, but it
  # has not yet been added. This setting has no effect.
  multiverse-core-hook: true

  # This toggles the WorldEdit hook. SkyWars supports starting arenas both
  # using an integrated Bukkit method, and a faster WorldEdit method.
  #
  # If this is true, and the WorldEdit plugin is enabled on the server,
  # WorldEdit will be used to copy arena blocks.
  #
  # If this is false, or the WorldEdit plugin is not installed, the integrated
  # Bukkit method will be used to copy arena blocks.
  #
  # Note that if the WorldEdit hook is enabled, and WorldEdit is installed,
  #  SkyWars depends on WorldEdit being up-to-date to work correctly. If
  #  WorldEdit is out of date, arenas will not be copied correctly.
  worldedit-hook: true
```

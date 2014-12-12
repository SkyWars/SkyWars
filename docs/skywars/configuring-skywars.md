Configuring SkyWars - main-config.yml
=====================================

This is documentation for configuring `plugins/SkyWars/main-config.yml`.

For documentation on configuring a file in the `arenas/` directory, see https://dabo.guru/projects/skywars/configuring-arenas.

For documentation on configuring `arena-parent.yml`, see https://dabo.guru/projects/skywars/configuring-parent.

For documentation on configuring `kits.yml`, see https://dabo.guru/projects/skywars/configuring-kits.


```yaml
# This is the configuration version. Unless you really want to mess up your
#  configuration, you should not change this.
config-version: 2

# Whether or not to enable debug mode. In debug mode, SkyWars will print a huge
#  amount of information too the server console.
# There is no need to enable this unless you are asked to by SkyWars support
#  (daboross).
debug: false

# This represents the order that the arenas are started in. Whenever enough
# people join the queue, a new arena is chosen either at RANDOM, or ORDERED.
# Setting this value to RANDOM will make a random arena be chosen from
# enabled-arenas when a new game starts. Setting it to ORDERED will make each
# enabled arena be started in sequence.
arena-order: RANDOM

# This is the string that will prefix all SkyWars messages sent to players.
message-prefix: '&8[&cSkyWars&8]&a '

# Whether or not to save inventories of players who are joining SkyWars games.
# When this is false, each player's inventory will be wiped when the join a
# game and when the leave a game. When this is true the inventory is stored
# in memory and then wiped when the join a game, then restored when they leave.
# Note that if the server crashes when games are running, or SkyWars is
# forcefully stopped, the inventories of the players in any game running at
# that time will not be recoverable.
save-inventory: true

# A list of enabled arenas. Each of the items in this list corresponds to a
# file in the arenas/ folder. When SkyWars loads, it will take each item in
# this list, look for a file in the arenas/ folder who's name is this followed
# by '.yml', then load it into the enabled arenas list.
enabled-arenas:
- skyblock-warriors

locale: en

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
  #  a game
  kill-point-diff: 1

  # Number of seconds between saving the score to hard disk
  max-save-interval: 300

  # If true, use the SQL storage backend. If false, the JSON storage is used.
  use-sql: true

  # SQL login info
  sql:
    # Host to connect to
    host: 127.0.0.1
    # Port to connect to
    port: 3306
    # Database to store on. Data is stored in a `skywars_user` table in this
    #  database.
    database: minecraft
    # Username to login as. Using `root` is not recommended. Instead you should
    #  set up a more restricted user.
    username: root
    # Password to login with.
    password: aComplexPassword

# Subsection for the economy Vault hook
economy:

  # Whether or not to enable hooking into Vault for economy.
  # If disabled, no rewards will be given, and kits with costs will also
  #  be disabled.
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
# Note that this is the distance from the center of one arena to the center
#  of the next.
arena-distance-apart: 200

# Command whitelist sub-section
command-whitelist:

  # Whether or not to enable the command whitelist. If enabled, commands
  #  use will be restricted when in a game.
  whitelist-enabled: true

  # If true, treat the values in `whitelist` as a blacklist, blocking them and
  #  only them in game.
  # If false, treat the values in `whitelist` as a whitelist, blocking all
  #  commands *except them* in game.
  treated-as-blacklist: false

  # Commands to whitelist/blacklist in game
  whitelist:
  - /skywars
  - /sw
  - /me
```

Using the Score System
======================

### Score
The SkyWars plugin has a concept of a player "score", which is added to when
that player wins a game or kills someone in a game, and is subtracted from
when that player is killed.

### Score Configuration Options
Score is stored either in a json file, or on an sql server, depending on
your configuration.

You can configure how much someone's score changes from different events,
and how store is saved in the `main-config.yml`'s `points` section:

```yaml
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
  # In JSON storage, this is the number of seconds between saving the entire score
  # file to the hard disk.
  # In SQL storage, this is the number of seconds between saving only uncommitted data
  # to the SQL server. In SQL, this is also the number of seconds between leaderboard
  # top-10 updates (data shown on `/sw top`). Note that individual ranks (data shown
  # on `/sw rank`) is updated on a different timer, according to the
  # `individual-rank-update-interval` setting below.
  max-save-interval: 300

  # If true, use the SQL storage backend. If false, the JSON storage is used.
  # Note: When using SQL backend, for most efficiency, set max-save-interval to 20
  # SQL saving works differently than json saving, so max-save-interval values from
  # 20-100 seconds work best.
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

    # Time in seconds between updating the ranks of each individual. This is the
    # rank shown in `/sw rank`, and this will update the ranks of everyone on
    # the server at the given interval. However, updating each individual's rank is
    # a more costly operation than updating the top 10 ranks, so it is done on a
    # separate timer.
    individual-rank-update-interval: 60
 ```

 Note that whichever method you choose for score storage, may also be used to
 store other persistent user data, such as what kit each player has selected.

### Showing Score

#### In Chat

If you add the text `{SKYWARS.USERSCORE}` to someone's prefix or to the chat format in your preferred chat plugin, SkyWars will replace that text with the person's score number.

If you add the text `{SKYWARS.USERRANK}`, SkyWars will similarly replace that text with the person's rank number.

#### Top-10 leaderboard

To view the top 10 players with the most score, the command `/sw top` can be used.

When using the SQL backend, the data this command shows is updated every 20 seconds. In the JSON backend, the data is updated immediately.

#### Individual rank

To view someone's individual rank, the `/sw rank <player>` can be used, or `/sw rank` to view one's own rank.

When using the SQL backend, the data this command shows is updated by default every 60 seconds. In the JSON backend, the data is updated immediately.

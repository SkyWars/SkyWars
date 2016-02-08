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
 ```

 Note that whichever method you choose for score storage, may also be used to
 store other persistent user data, such as what kit each player has selected.

### Using Score

Currently, SkyWars has a very limited number of ways to actually show score.
There will *definitely* be more ways to access score added in the future.

Currently, the only way to show score is to add it in chat, as a prefix to a
player's name. If you add the text `{SKYWARS.USERSCORE}` to someone's prefix in
your prefered chat plugin, SkyWars will replace it with that person's score, as
a number.
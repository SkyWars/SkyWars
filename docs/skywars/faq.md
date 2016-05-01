FAQ
===

### Why does SkyWars create new worlds?

SkyWars v2.1.0 will create one world: SkyWarsArenaWorld. SkyWars uses this world to run games, and all players playing games will be located in this world. SkyWars will automatically delete this world when the server is shutdown.

SkyWars pre-2.1.0 will also create another world: SkyWarsBaseWorld. This world was used in SkyWars versions pre-2.1.0 to store the arena template. This world is no longer used at all by SkyWars v2.1.0+.

If you are using SkyWars v2.1.0 or later, and you haven't made any changes to the default arenas or added any custom arenas in SkyWarsBaseWorld, feel free to delete it!

### Help! Players can't break blocks in the arena!

Try setting 'spawn-protection' in server.properties to 0 and restarting the server (completely restart, not reload).

If that doesn't work, please submit a ticket below for additional help!

### Help! Iron doors aren't place correctly!

If you are using WorldEdit support, please ensure that your WorldEdit version is up to date, and that you are using the latest development version if you are using Minecraft 1.9+. There is a bug in some earlier WorldEdit versions which causes doors to not be placed correctly.

### Help! UnsupportedClassVersionError unsupported major.minor version ...

You need Java 7 to run SkyWars. Please update your server's Java version.

If you already think you have Java 7, try looking through installed programs and uninstalling Java 6. If Java 6 and Java 7 are both installed, the server might use Java 6, which will cause problems.

If that doesn't work, please submit a ticket below for additional help!

### Other questions/errors/bugs/requests!

If your question isn't answered above, ask it below! You'll need to create a GitHub account to submit a ticket (very fast process), or you can email me directly at daboross@daboross.net.

[Submit a ticket](https://dabo.guru/projects/skywars/submitting-a-ticket).

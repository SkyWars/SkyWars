Changelog
=========

2.1.3
-----

### SkyWars:
- Add configuration option to skip respawn screen when players in game die.
- Fix `/sws save` not saving spawn locations in the right places. This bug was introduced in v2.1.2.
- Fix SkyWars not filling up chests.yml with default values if it's an empty file.

### SkyWars-Translations:
- Add a second line to the `/sws save` saved message, to clarify when SkyWars renames arena to avoid naming conflict.

2.1.2
-----

### SkyWars:
- Add score leaderboard system!
- Add `/sw top` command to show top 10 highest ranked players (with most score).
- Add `/sw rank` command to view your own or someone else's score and rank.
  - Add new configuration option for how often personal rank is updated when using SQL score backend.
- Add support for replacing `{skywars.userrank}` with rank in any chat format (added to chat format via separate chat plugin).
- Add chest randomization, with new `chests.yml` configuration file.
  - Chest randomization is not enabled at all by default.
  - Default `chests.yml` contains a minimal number of items this update. It is recommended to add more items if you enable it.
- Add enabling chest randomizer per-chest with configuration options in each `arena-name.yml` file.
  - Chests are identified by their relative x, y and z positions in the arena.
- Add ability to configure items with different chance to occur and different values in chest randomization, and ability to set total "value" each chest will contain when using randomizer.
- Remove unused multiverse-inventories hook configuration option.
- Fix `/sws update-arena` to actually successfully run.
- Fix bug where all kit costs were removed when using `/sws createkit`.
- Fix inaccurate death messages when player is damaged before entering a match, and then jumps into void.
- Fix NullPointerException which occurs after using `/reload` or a plugin manager to reload SkyWars.

### SkyWars-API:
- Add easy-to-access API method for getting top players with most score.

### SkyWars-Translations:
- Update Dutch translations - thanks @riassmismans!
- Add new translations for `/sw top` and `/sw rank`.
- Add message to `/sw cancelall` for when there are no arenas running.
- Modify English `/sws` messages to be less confusing, changes not translated yet.

2.1.1
-----

### SkyWars:
- Add support for item names, item lore, colored leather armor and raw data in kits.
  - This also adds support for spawn eggs, mob heads, etc. - anything which has multiple versions. There are only now a few things missing from kits, such as custom fireworks.
- Fix colored leather armor not saving color when in chests in arenas.
- Fix SkyWars only working on Unix systems - SkyWars now runs correctly on Windows as well.
- Add a `/sws update-arena` command which recreates the template for an arena from the original area where it was added.
- Remove default player access to `/sw lobby`. This is to follow SkyWars using lobby a lot less with the position saving mechanism.

### SkyWars-Translations:
- Now **fully translated** to Dutch thanks to @MisterGiant!

2.1.0
-----

### SkyWars:
- Over 4000 lines of code changed from version 2.0.1!
- Add new default arena, **water-warriors**! SkyWars now has two default arenas included.
- Add support for Spigot version 1.9 (Still supports CraftBukkit 1.7.8+ and Spigot 1.7.8+!)
- Add support for saving player position, experience, gamemode, health, hunger and other survival variables when entering the arena, and restoring upon exiting!
- Add support for potions in kits
- Add a `/sws createkit` command to create a kit from all items in your current inventory
- Add a `/sw testkit` command for operators to test out a kit without entering a game.
- Update default kits.yml with more reasonable starting kits
- Fix possible bug where score data wouldn't save to SQL
- Fix bug where items would sometimes be found in arenas from past games
- Add arena "cache" concept: store arena templates in `.blocks` files
- Add hook to use WorldEdit when available to copy/paste arena templates
- Add support for setting custom gamerules in the arena world
 - Also ensure that it is always daylight in the arena world using a default gamerule.
- Stop players from getting money and points from killing themselves
- Add a message to players in arenas with teams to tell them who their teammates are!
- Fix team joining logic so that teams are always filled fairly
- Remove SkyWarsBaseWorld.zip from plugin jar, now just includes arena .blocks files.
- Update arena creation command to not store empty space (easier to use now)
- Remove arena-parent.yml
- Add "you can't do that" message for when player in-game commands are blocked
- Fix "forced to concede" message never showing up
- Remove all constant strings from all command handling, such as the "No permission" message
 - All messages are now fully translatable!

### SkyWars-API:
- Remove arena parent concept
- Add team interface
- Fix compatibility issues with Forge
- Add Potions API
- Add better inventory saving API
- Other internal changes with no public API differences

### SkyWars-Translations:
- Add translatable messages for all new commands
- Add translatable strings for previously non-translatable messages such as the No permission message!
- Now **fully translated** to German thanks to @Androkai!

2.0.1
-----

### SkyWars
* Fix broken end-game message when multiple people are still alive
* Fix NullPointerException after player leaves server when in game
* Fix `/sw status` showing UUIDs instead of player names in queue
* Fix ORDERED arena configuration status only ever playing one arena.
* Remove support for per-arena-configuration messages. Now all messages are configured in messages.yml.
* Add a message to `/sw kit` explaining how to remove a kit.
* Check to see if a player is out of money to use a kit when they join the queue, not just when a game starts.

* Lots of documentation fixes, and configuration documentation fixes
* Fix plugin compiling when the http://repo.daboross.net nexus server is offline

2.0.0
-----

### SkyWars:
* Actually bump major version number, as this is an API breaking change.
* Fix compatibility with craftbukkit 1.7.8

1.4.5
-----

### SkyWars:
* Update JSON storage to use UUIDs. Add gradual migration from username to UUID as users log in.
* Add SQL storage backend
* Fix players who have had their permissions removed still being able to access kits they have chosen
* Fix SkyWarsBaseWorld being created outside of world container if container is changed

### SkyWars-API:
* Rename SkyInGame to SkyPlayers, as it's not just keeping track of players in-game now.
* Update all API methods using java.io.File to use java.nio.file.Path instead
* Rename all Score methods from XScore to XPoints in SkyConfiguration
* Add score sql settings to SkyConfiguration
* Add hook enabled methods to SkyConfiguration, unused currently
* Update all API methods taking/giving player names to use UUIDs instead.
* Rename Points api to Score api, and update it to use UUID storage instead of player names.
* Each storage backend now needs to provide an extension of AbstractSkyPlayer with getScore/setScore/addScore methods.

### SkyWars-Translations:
* Add 'messages.kits.no-permission' message for when a permission is lost for a kit.

1.4.4
-----

### SkyWars:
* Fix players on the same team being able to hit eachother
* Fix team prefixes not showing on players when teams are enabled
* Fix `/sw kit` failing when the kit being applied doesn't have a permission

### SkyWars-Translations:
* Add red color to the error `/sw forcestart` displays if there are less than 2 players in the queue.
* Bump message version to 4 - this will update your messages.yml file with the latest messages

1.4.3
-----

### SkyWars:
* Fix players being able to use kits without permission.

### SkyWars-Translations:
* Add german translation (de)

1.4.2
-----

### SkyWars:
* Fix translations issues when listing kits with cost
* A lot of internal changes, stop using lombok. This doesn't result in any end-product changes.

1.4.1
-----

### SkyWars:
* Add a configuration option to disable the report command

1.4.0
-----

### SkyWars:
* Add support for hooking into vault.
 * Add optional rewards for winning a game and killing someone in a game
* Add a `/sw forcestart` command to start a game with less than the maximum number of players
* Combined support for 1.5.2, 1.6.4 and 1.7.2 in one jar file
* Add kit command and notices.
 * Add a notice that prompts user to pick kits when joining the queue, if there are kits available for them.
 * Add a kits.yml configuration that you can configure kits in
 * Add '{skywars.userscore}' as a replacement in chat messages
* Add a save timer for score, save every x minutes as well as when the server shuts down. Smart timer doesn't save if nothing has changed.
* Add configuration option for locale in main-config.yml that overrides system locale
* Change spawn location of arena world from 0,0 to -5000,-5000
* Copy contents from Dispensers as well as Chests

### SkyWars-API:
* Add events for joining and leaving the queue
* Add SkyEconomyAbstraction vault api
* Fix ArenaPlayerDeathEvent
* Add SkyInGame state storage
* Add Kit storage classes and kits api

### SkyWars-Translations:
* Add more untranslated strings to all message files
* Add Czech translation (cz)
* Add Spanish translation (es)
* Add French translation (fr)
* Update Portuguese translation
* Add Russian translation (ru)

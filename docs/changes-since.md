Changes since 2.1.0
-------------------

SkyWars:
- Add support for item names, item lore, colored leather armor and raw data in kits.
  - This also adds support for spawn eggs, mob heads, etc. - anything which has multiple versions. There are only now a few things missing from kits, such as custom fireworks.
- Fix colored leather armor not saving color when in chests in arenas.
- Fix SkyWars only working on Unix systems - SkyWars now runs correctly on Windows as well.
- Add a `/sws update-arena` command which recreates the template for an arena from the original area where it was added.
- Remove default player access to `/sw lobby`. This is to follow SkyWars using lobby a lot less with the position saving mechanism.

SkyWars-Translations:
- Now **fully translated** to Dutch thanks to @MisterGiant!

Changes since 2.0.1
-------------------

SkyWars:
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

SkyWars-API:
- Remove arena parent concept
- Add team interface
- Fix compatibility issues with Forge
- Add Potions API
- Add better inventory saving API
- Other internal changes with no public API differences

SkyWars-Translations:
- Add translatable messages for all new commands
- Add translatable strings for previously non-translatable messages such as the No permission message!
- Now **fully translated** to German thanks to @Androkai!

Changes since 2.0.0
-------------------

SkyWars:
* Fix broken end-game message when multiple people are still alive
* Fix NullPointerException after player leaves server when in game
* Fix `/sw status` showing UUIDs instead of player names in queue
* Fix ORDERED arena configuration status only ever playing one arena.
* Remove support for per-arena-configuration messages. Now all messages are configured in messages.yml.
* Add a message to `/sw kit` explaining how to remove a kit.
* Check to see if a player is out of money to use a kit when they join the queue, not just when a game starts.

* Lots of documentation fixes, and configuration documentation fixes
* Fix plugin compiling when the http://repo.daboross.net nexus server is offline

Changes since 2.1.2
-------------------

SkyWars:
- Add configuration option to skip respawn screen when players in game die.
- Fix `/sws save` not saving spawn locations in the right places. This bug was introduced in v2.1.2.
- Fix SkyWars not filling up chests.yml with default values if it's an empty file.

SkyWars-Translations:
- Add a second line to the `/sws save` saved message, to clarify when SkyWars renames arena to avoid naming conflict.

Changes since 2.1.1
-------------------

SkyWars:
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

SkyWars-API:
- Add easy-to-access API method for getting top players with most score.

SkyWars-Translations:
- Update Dutch translations - thanks @riassmismans!
- Add new translations for `/sw top` and `/sw rank`.
- Add message to `/sw cancelall` for when there are no arenas running.
- Modify English `/sws` messages to be less confusing, changes not translated yet.

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

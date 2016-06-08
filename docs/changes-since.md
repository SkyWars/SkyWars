Changes since 2.1.3
-------------------

SkyWars:
- Add join signs with auto-updating queue information. See https://dabo.guru/projects/skywars/signs-and-portals!
- Fixed up documentation for join portals.
- Fix a possible bug which would cause SkyWars to not save scores to JSON when using Java 7 instead of Java 8.

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

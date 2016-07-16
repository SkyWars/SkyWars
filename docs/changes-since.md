Changes since 2.1.6
-------------------

SkyWars:
- Fix bug introduced in v2.1.6 where chest randomization does not fill any chests.
- Add secondary plugin statistics system, reporting to https://dabo.guru
  - I meant to wait until v2.2.0 to include plugin-statistics, but this bugfix release needs to happen, and since the statistics is done I thought I might as well include it now.
  - For details on what data is reported, and how to opt out, see statistics section of https://dabo.guru/projects/skywars/, or "report-statistics" option in https://dabo.guru/projects/skywars/configuring-skywars.
  - To see recorded data (the same data I have access to), see https://dabo.guru/statistics/skywars/.

Changes since 2.1.5
-------------------

SkyWars:
- Fix kit messages & GUI being shown to those without the skywars.kit permission
- Fix right clicking on join signs not doing anything
- Add full support for all MC 1.9 variations of potions in custom arenas.
- Fix potions not working in kits and random chests in MC 1.9+.
- Fix armor not being applied in kits on MC 1.9+.
- Fix tab completion for all SkyWars commands with arguments (/sw kit, etc.)
- Fix multiple possible NullPointerExceptions errors when:
  - team mode is enabled, and a game is started without the maximum number of players
  - checking player interactions with other plugins
  - a player leaves the server while in a game
  - kits with no non-armor inventory items are used
- Fix errors starting arena when:
  - random chests with a high total level are used
  - double chests are placed in a custom arena
- Fix random chest configuration sections only being created for regular chests, not trapped chests
- Fix errors on leaving game when maximum player health is increased by armor or other items.
- Fix outdated WorldEdit versions causing glitches in SkyWars.
- Fix chests in custom arenas on Glowstone
- Remove UUID warning message on Glowstone and other non-CraftBukkit servers when UUIDs are supported

SkyWars-Translations:
- Make all game message punctuation consistent between all translated languages.

Changes since 2.1.4
-------------------

SkyWars:
- Add support for limiting start/win/death in an arena to only players in that arena
  - Enable death-message-limiting by default - deaths in game will only be displayed to those in that game.
- Fix players without the `skywars.join` permission being able to use join signs
- Implement an inventory-based kit-choosing GUI.
  - Each kit now has a "totem," and "description" configuration setting, which are used as the display item and lore for the kit in the kitGUI, respectively.
  - Add configuration options to display kit gui...:
    - with a `/sw kitgui` command
    - with the `/sw kit` command (replacing the command kit interface completely)
    - or, automatically whenever joining the queue (or clicking a join sign when already in the queue)
  - The `/sw kitgui` and automatic-showing are enabled by default for players with the `skywars.kitgui` permission (OP-only by default)
- Fix `/sw report` not working with the new `is.gd` API changes.
- Fix players always facing one direction when teleporting to the lobby. SkyWars now correctly applies the pitch/yaw.

SkyWars-API:
- Allow more optimization for the final SkyWars plugin jar file. This means a possibly faster and definitely smaller jar, but if you've been depending on SkyWars.jar from another plugin, this may break that dependency.
- Note that as long as you are depending on SkyWars-API.jar, and using all the public interfaces defined in that, you'll be fine. All of the API interfaces and methods are exempt from the optimization, and will work fine in any setting.

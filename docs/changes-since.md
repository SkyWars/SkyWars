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

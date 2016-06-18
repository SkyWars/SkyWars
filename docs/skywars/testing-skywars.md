SkyWars Function Testing
------------------------

This file overviews all the features of SkyWars that need to be tested manually.

This is for use if you want to submit a Pull Request to SkyWars and confirm that it doesn't break anything, or you want to test a development build for yourself.

This basically just lists all the things that are required to consider a SkyWars build "working".

Some things here aren't listed, but are implied by other steps. For instance, there is no reason to test `Confirm you win a game after killing everyone`
if you're already testing `Confirm your score is increased when you win a game`

I may do this differently in the future, but it seems a bit redundant for now.

Before testing:

* Completely wipe all configuration files and worlds
* Install craftbukkit build you're testing for
* Run server once, then stop
* Set online-mode to false in server.properties
* Set allow-nether to false in server.properties
* Set allow-end to false in bukkit.yml
* Delete `world_nether` and `world_the_end`
* Install Vault of the version you're testing for
* Install a test economy.
 * BOSEconomy works for pre-1.9 minecraft versions, and is simple to test and work with.
 * RoyalEconomy requires manual compilation, as it isn't a released plugin. However, it's an open source project, and it works well.
* Set `economy.enabled` to `true` in `plugins/SkyWars/main-config.yml`
* Update `permissions.yml` to enable usage of `/sw kit` and `/sw kitgui` by non-ops:

```yaml
player.basics:
    default: true
    children:
        skywars.kit: true
        skywars.kitgui: true
```

And now, testing!

* Start two arenas, confirm `/sw cancelall` works
* Start two arenas, confirm `/sw cancel` works on each
* Confirm `/sw cancel` fails with no arenas started
* Confirm `/sw report` works
* Add one person to queue, confirm `/sw forcestart` doesn't work
* Add two people to queue, confirm `/sw forcestart` works
* Join the queue using both the `/sw join` command, and a join portal
* Confirm `/sw kit` lists both available and unavailable kits.
* Apply a kit using `/sw kit`, and confirm it's applied
* Confirm the kit is still applied next game
* Confirm `/sw kit` shows you've applied a kit
* Confirm `/sw kit remove` removes your kit
* Confirm `/sw kit` won't let you apply a kit you don't have permission for
* Confirm `/sw kit` won't let you apply a kit you don't have money for
* Confirm `/sw kit` will apply a kit that does require permission
* Confirm `/sw kit` will apply a kit that doesn't require permissions or money
* Confirm `/sw kit` will apply a kit that costs money, and that your money decreases
* Confirm kit is removed after you run out of money (when you join, not just when the game starts)
* Confirm permission kit isn't applied if you loose the permission (when you join, not just when the game starts)
* Kits (`/sws createkit` and `/sw testkit`):
 * Create and apply a kit with named items
 * Create and apply a kit with colored leather armor both in inventory and in armor slots
 * Create and apply a kit with damaged items
 * Create and apply a kit with different spawn eggs.
 * Confirm kit with all of the above works after restarting the server (testing serialization/deserialization)
* Confirm `/me` isn't blocked in-game
* Confirm `/gamemode` is blocked in-game
* Confirm `/sw leave` works from the queue
* Confirm `/sw leave` works from in-game
* Confirm `/sw lobby` works
* Confirm `/sw lobby` is blocked in-game
* Confirm `/sw setlobby` works, and changes are reflected in `/sw lobby`
* Confirm respawn location after leaving game reflects new lobby
* Confirm `/sw setportal` sets a portal that works
* Confirm `/sw delportal` removes the portal
* Confirm `/sw status` accurately displays both people in queue and running games
* Confirm `/sw status` doesn't display games after they have ended
* Confirm `/sw version` accurately displays plugin version
* Create a join sign, and confirm the sign is formatted
 * Confirm clicking on the sign enters you into the queue
 * Confirm the sign is updated when `/sw join` is used to enter the queue.
* Confirm `/sw kitgui` opens a kit GUI.
 * Confirm nothing happens and no errors occur when clicking on an empty spot and when clicking on an item in your inventory.
 * Confirm clicking on an available kit selects it and closes the window.
 * Confirm clicking on an unavailable kit gives an error message in chat and closes the window.
* Confirm in-game death messages display correctly when someone is directly killed
* Confirm in-game death messages display correctly when someone is pushed into the void
* Confirm in-game death messages display correctly when someone kills themself through drowning
* Confirm your score is increased when you win a game
* Confirm your score is increased when you kill someone
* Confirm your score is decreased when you die in a game
* Confirm your score is unaffected when you die outside of a game
* Confirm enderpearls don't cause you to kill yourself
 * It isn't a problem to get damage from enderpearls, only if you receive points from killing yourself.
* Confirm you can't hit someone on your team
* Confirm after you kill everyone the game ends
* Confirm someone disconnecting from the server removes them from a game they're playing
* Confirm it's always daylight in the game
* Config testing:
 * Confirm you keep your inventory after dying with `save-inventory: true`
 * Confirm you loose your inventory after dying with `save-inventory: false`
 * Confirm the `/sw` help message changes after setting `locale: es` (or any other language)
 * Confirm SkyWars starts up correctly with locale set to `cz`, `de`, `dk`, `en`, `es`, `fr`, `nl`, `pl`, `pt` and `ru`
 * Confirm messages.yml updates after changing locale when auto-update is set to true
 * Confirm messages.new.yml is created after changing locale when auto-update is set to false
 * Confirm messages.yml *does not* update after changing locale when auto-update is set to false
 * Confirm `/sw kit` does not display economy-cost kits after setting `economy.enabled: false`
 * Confirm points are not altered with `enable-points: false`
 * Confirm games start correctly with `enable-points: true` and `use-sql: false`
 * Confirm games start correctly with `enable-points: true` and `use-sql: true`
 * Confirm games start correctly with `enable-points: false`
 * Confirm position, game mode, health and hunger are all saved with `save-position-gamemode-health`.
 * Confirm allowFlight is reset when joining game
 * Confirm all team members are given economy and point rewards when using teams.
 * TODO: More encompassing config testing overview
* TODO: Setup testing overview
* Arena creation testing:
 * Create an arena?
 * Confirm the following are correctly stored in chests:
   * Colored leather armor
   * Different spawn eggs
   * Potions with and without custom effects
* Test starting up from a fresh server on Linux and Windows to ensure there are no OS dependencies
 * No need to do a full test on both, just be sure to test that there are no immediate crashes when first loading arenas.

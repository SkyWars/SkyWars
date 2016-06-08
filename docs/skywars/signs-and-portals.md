Join Signs & Portals
====================

### Signs

Joins signs are signs which when clicked, will force the player to join the SkyWars Queue.

Join signs are automatically updated with the number of players in the queue, the number of players needed to start a game, and the name of the next arena which will be played instantly when any of that information changes.

To create a join sign in SkyWars with the default configuration, simply create a new sign with the first line being "[SkyWars]" and it will turn into an auto-updating join sign.

To remove a join sign, simply destroy the sign block. SkyWars has no special protection on destroying join signs, however you will join the queue when you start the destroy it. To stop players from destroying join signs, you should use a protection plugin or spawn protection.

If you want to change the appearance of the join sign, you can change the 'join-sign-lines' setting in main-config.yml. Note that when you change this setting, all old join signs created with the old setting will no longer auto-update, and will no longer function as join signs. They must be re-created in order to function.

Join signs are introduced in SkyWars v2.1.4.

### Portals

Join portals are single blocks which when walked over, will force the player to join the SkyWars queue. They don't have any visible appearance, so it is recommended to only place them in a clearly marked location, such as a different block type with a sign on it.

To create a portal at your current block location, use `/sw setportal`. This will set the current block you are standing on as a join portal.

Since there is no "portal block" to destroy, in order to remove a portal you must use `/sw delportal`. This command does not depend on your current position, and instead removes portals in order of last added. So, to remove a portal you just set, use `/sw delportal`. To remove the last two portals you set, use `/sw delportal` twice. To remove all portals, continue using `/sw delportal` until it reports that no portals exist.

Join Portals were introduced in SkyWars v1.0.0.

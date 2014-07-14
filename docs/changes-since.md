Changes since 1.4.4
-------------------

### SkyWars:
- Update JSON storage to use UUIDs. Add gradual migration from username to UUID as users log in.
- Add SQL storage backend
- Fix players who have had their permissions removed still being able to access kits they have chosen
- Fix SkyWarsBaseWorld being created outside of world container if container is changed

### SkyWars-API:
- Rename SkyInGame to SkyPlayers, as it's not just keeping track of players in-game now.
- Update all API methods using java.io.File to use java.nio.file.Path instead
- Rename all Score methods from XScore to XPoints in SkyConfiguration
- Add score sql settings to SkyConfiguration
- Add hook enabled methods to SkyConfiguration, unused currently
- Update all API methods taking/giving player names to use UUIDs instead.
- Rename Points api to Score api, and update it to use UUID storage instead of player names.
- Each storage backend now needs to provide an extension of AbstractSkyPlayer with getScore/setScore/addScore methods.

### SkyWars-Translations:
- Add 'messages.kits.no-permission' message for when a permission is lost for a kit.


Changes since 1.4.3
-------------------

SkyWars:
* Fix players on the same team being able to hit eachother
* Fix team prefixes not showing on players when teams are enabled
* Fix `/sw kit` failing when the kit being applied doesn't have a permission

SkyWars-Translations:
* Add red color to the error `/sw forcestart` displays if there are less than 2 players in the queue.
* Bump message version to 4 - this will update your messages.yml file with the latest messages

Changes since 1.4.2
-------------------

SkyWars:
* Fix players being able to use kits without permission.

SkyWars-Translations:
* Add german translation (de)

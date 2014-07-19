Changelog
=========

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

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

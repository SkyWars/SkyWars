SkyWars
=======

### Features
* Automatically create new arenas every time enough people join the queue.
* Have *as many arenas* going at the same time as you want!
 * Whenever the configured amount of people in the queue (default to 4), a new arena is created automatically!
 * There is no limit besides your server's capacity to how many arenas SkyWars can create.
* Teleports people to lobby location when they leave or die.
* When players walk near a set portal, they will be automatically added to the queue.
* Clears player inventories when the join a game and when the leave.

### Basic Installation
* Make sure your server is running Java 7.
* Download the correct SkyWars jar file for your server version.
 * 1.6.2 servers should download SkyWars.jar
 * 1.5.2 servers should download SkyWars-1.5.2.jar
* Put the downloaded jar in your plugins/ directory.
* Restart your server. SkyWars will automatically create 2 new worlds. SkyWarsBaseWorld and SkyWarsArenaWorld.
* Go to where you want the lobby to be and use `/sw setlobby`. The lobby must be set in order for games to be started.
Those are the steps for basic configuration.

### Useful links
* [Commands and Permissions](https://github.com/daboross/SkyWars/wiki/Commands-and-Permissions)
* [Worlds](https://github.com/daboross/SkyWars/wiki/Worlds)
* [Common issues](https://github.com/daboross/SkyWars/wiki/Common-Issues)
* [Dev builds - CI Server](http://ci.aemservers.net/job/SkyWars)
* [Source code](https://github.com/daboross/SkyWars)
* [Portuguese Tutorial - Ligação para tutorial AbsintoJ feito](http://www.youtube.com/watch?v=hYTq39Iomz0)
* [MCStats Statistics](http://mcstats.org/plugin/SkyWars)

### Plugin Metrics / Statistics
<center>![- plugin metrics image would be here -](http://api.mcstats.org/signature/SkyWars.png)</center>

### Drawbacks
Players will loose all items in their inventories when the game starts.

Requires Java version 7. Will __not__ work with java 6.

### Bugs, Features, and Questions
We use the github issue tracker for all bugs, features, and questions.
See [submitting a ticket](https://github.com/daboross/SkyWars/wiki/Submitting-a-ticket)

### Full credit for the default map, Skyblock Warriors, goes to [SwipeShot](http://www.youtube.com/user/SwipeShot).
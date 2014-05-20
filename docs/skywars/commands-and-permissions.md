Commands and Permissions
========================

### Main Commands
| Command               | Permission         | Description                                          |
| :-------------------- | :----------------  | :--------------------------------------------------- |
| **/sw**               | N/A                | Lists all available SkyWars commands                 |
| **/sw** **join**      | skywars.join       | Joins the queue for the next game                    |
| **/sw** **leave**     | skywars.leave      | Removes you from the queue and any arenas you are in |
| **/sw** **lobby**     | skywars.lobby      | Teleports you to the lobby location                  |
| **/sw** **setlobby**  | skywars.setlobby   | Sets the lobby location                              |
| **/sw** **setportal** | skywars.setportal  | Sets a new portal at your current location           |
| **/sw** **version**   | skywars.version    | Displays information about this plugin               |
| **/sw** **status**    | skywars.status     | Displays information on the current status           |
| **/sw** **cancel**    | skywars.cancel     | Force stops a currently running game of the given ID |
| **/sw** **report**    | skywars.report     | Generates and submits a report.                      |
| **/sw** **forcestart**| skywars.forcestart | Force starts a game with the people in the queue.    |
| **/sw** **kit**       | skywars.kit        | Lists kits, and lets you choose one                  |

* Note: /sw report will submit Server Software, Server Version, plugin information and all configuration information to gist.github.com, then give you a URL link to the data. This is usually useful for debug information.

### Setup Commands
| Command               | Permission         | Description                                          |
| :-------------------- | :----------------  | :--------------------------------------------------- |
| **/sws**              | skywars.setup      | Lists all available SkyWars setup commands that you can use currently |
| **/sws** **start** `<Name>`   | skywars.setup      | Starts creating a new arena, this will discard all unsaved arenas. |
| **/sws** **setpos1**  | skywars.setup      | Sets the first position for where to copy the arena from. |
| **/sws** **setpos2**  | skywars.setup      | Sets the position opposite from the first position to copy the arena from. |
| **/sws** **addspawn** | skywars.setup      | Adds a spawn position to the arena at your current location. |
| **/sws** **save** | skywars.setup      | Saves the configuration to file under the name you started with. |

* Note: The /sws command descriptions are brief. If you want a guide on how to use them, check out [Setting up a new arena](http://dabo.guru/skywars/creating-an-arena).

### Permissions
| Permission        | Defaults to       |
| :---------------- | :---------------- |
| skywars.join      | Everyone          |
| skywars.leave     | Everyone          |
| skywars.lobby     | Everyone          |
| skywars.version   | Everyone          |
| skywars.status    | Everyone          |
| skywars.kit       | OP Only           |
| skywars.setlobby  | OP Only           |
| skywars.setportal | OP Only           |
| skywars.cancel    | OP Only           |
| skywars.report    | OP Only           |
| skywars.setup     | OP Only           |
| skywars.forcestart| OP Obnly          |

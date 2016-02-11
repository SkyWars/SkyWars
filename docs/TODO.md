SkyWars Tasks
=============

- Better team integration
  - Teams should be told explicitly who their members are
    - A translation message key already exists for this, it just needs to be implemented in code.
  - Creative team names?
    - You, X and Z are on team creeper!
    - Y, A, B are on team lion!
  - Choosing teams before a match?
    - This might need a "custom queue" support, or a specific teamed mode.
- Multiple queues for each arena
- Custom queues (password protected?)
  - Like /sw join mysecretpassword
  - Maybe with /sw custom <arena> <password>?
- Option to only broadcast messages to players in the games.
- Sometimes items dropped in previous games are still there in new games.
  This will need some kind of item-removal thing in each world provider to fix.
- There are some messages which are hardcoded into the command system, such as
  "The subcommand 'x' does not exist for the command '/sw'" which can't be changed
  or translated.
  - Fixing this might require moving the subcommand library into the SkyWars codebase.
- Save EXP along with inventory
- Force start on a timer
- Save location and gamemode along with inventory and EXP
- "Update arena" option for when arenas are stored as .blocks files?
- Import schematics into .blocks files.

Changes since 2.2.0:
--------------------

SkyWars:
- Fix timer not stopping when force started
- Fix errors if lobby location is not set
- Fix console errors which did not affect gameplay but were related to start timer


Changes since 2.1.7
-------------------

SkyWars:
- Add a timer to start games, and a minimum-player configuration for each arena
  - The timer will start when the queue reaches the minimum player count, but will automatically advance to 30 seconds left if the maximum number of players have joined
  - When the queue is full, any extra players who try to join are added to a "secondary queue", and are advanced to the main queue when either a game starts, or another player leaves the main queue.
  - All timings are completely configurable, as well as the times before game start which SkyWars broadcasts start messages.
- Add a piece-by-piece arena copying mechanism, similar to the kind used by AsyncWorldEdit. This greatly reduces server load caused by games starting and stopping, eliminating virtually all server lag caused by SkyWars.
  - The parameters of this method can be tweaked by adjusting the "time-before-start-to-start-arena-copy-operation" and "number-of-blocks-to-copy-at-once" options in the configuration. To disable, set "time-before-start-to-start-arena-copy-operation" to "-1".
  - When enabled, SkyWars will perform the arena operations in evenly spaced operations, starting at "time-before-start-to-start-arena-copy-operation", and ending exactly when the game starts. Each operation will copy "number-of-blocks-to-copy-at-once" blocks, and the number of operations is calculated based on how big each arena is.

While this version only contains two features, there have been many internal changes made to accommodate these two additions, and I hope these two features will greatly expand the use case and usefulness of the plugin.

Changes since 2.1.6
-------------------

SkyWars:
- Fix bug introduced in v2.1.6 where chest randomization does not fill any chests.
- Add secondary plugin statistics system, reporting to https://dabo.guru
  - I meant to wait until v2.2.0 to include plugin-statistics, but this bugfix release needs to happen, and since the statistics is done I thought I might as well include it now.
  - For details on what data is reported, and how to opt out, see statistics section of https://dabo.guru/projects/skywars/, or "report-statistics" option in https://dabo.guru/projects/skywars/configuring-skywars.
  - To see recorded data (the same data I have access to), see https://dabo.guru/statistics/skywars/.

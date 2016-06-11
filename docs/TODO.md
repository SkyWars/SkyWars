SkyWars Tasks
=============

[ ] Better team integration
  [ ] Colors for team names?
  [ ] Choosing teams before a match?
    [ ] This might need a "custom queue" support, or a specific teamed mode.
[ ] Multiple queues for each arena
[ ] Custom queues (password protected?)
  [ ] Like /sw join mysecretpassword
  [ ] Maybe with /sw custom <arena> <password>?
[ ] Option to only broadcast messages to players in the games.
[x] Sometimes items dropped in previous games are still there in new games.
    This will need some kind of item-removal thing in each world provider to fix.
[ ] Force start on a timer
[x] "Update arena" option for when arenas are stored as .blocks files?
[ ] Import schematics into .blocks files.
[x] Respawning immediately
[x] Implement `/sws createkit`
[ ] Figure out what potion extra effect "duration" is counted in.

Future Feature Poll Features
============================

New:

[ ] Option to place a specific block to mark spawn location, rather than using `/sws addspawn`
[ ] Option to allow spectating on ongoing games
    - Note if this is implemented: ensure to update code which limits game start/end/death messages to people in arena.

On last poll, but not yet added:

[ ] Inventory GUI for choosing a kit
[ ] Timer to start arena (w/ setting for minimum player count)
[ ] Voting on what map to play
[ ] Queued players waiting in map
[ ] Limiting arena chat & messages to players playing
[ ] Signs to pick kits (w/ kit cost shown)
[ ] Queued players waiting inside map
[ ] Ability to select large area where stepping will join the queue (currently only 1 block spaces are supported)

Finished:

[x] Signs to join (w/ auto-updated queue information)
[x] Filling chests with random items
[x] Command to view top 10 players ranked by score
[x] Limiting death & game start messages to players playing

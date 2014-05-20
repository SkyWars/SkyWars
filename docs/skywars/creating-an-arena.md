Setting up a new arena
======================

1. Use **/sws start <name you want>** to start setting up an arena. Replace **<name you want>** with a name that you think would fit your new arena.

   Generally this should be all lower-case and contain only 0-9, a-z and -.

2. Stand at one of the corners of the arena, so that your head is at the very edge of the arena on 3 sides.

   At that position, use **/sws setpos1**. This probably will require flying, as you need to be outside of the arena.

3. Stand at the corner *opposite* the corner you just stood at. Then use **/sws setpos2**

4. Go to every place where a player should spawn, and use **/sws addspawn**.

5. When you have added all the spawns you want, use **/sws save** to save your arena.
   This will create a .yml file in the **plugins/SkyWars/arenas** folder.

6. If you want to enable teams, or have a different number of players in the game than spawns there are, you should edit the **plugins/SkyWars/arenas/<arena name>.yml** file. You can enable teams by changing **team-size** to something other than 1.

7. Enable the arena in main-config.yml. This can be done by adding the arena name that you used in step 1 to the **enabled-arenas** list.

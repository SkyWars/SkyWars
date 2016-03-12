Setting up a new arena
======================

1. Use **/sws start <name you want>** to start setting up an arena. Replace **<name you want>** with a name that you think would fit your new arena.

   Generally this should be all lower-case and contain only 0-9, a-z and -.

2. Ensure your arena has some air blocks around it (it isn't connected to the ground). This will make it a lot easier to set up.

3. Fly so that you are all the way past one corner of the arena, and completely under it. Your position will be the lower bound of what is copied. At that position, use **/sws setpos1**.

4. Fly to the opposite corner, and fly so you are completely above the arena. This will be your upper bound. At this position, use **/sws setpos2**.

5. Fly to and stand above/in each location where a player should spawn. Once in each location, use **/sws addspawn**.

6. When you have added all the spawns you want, use **/sws save** to save your arena.

7. To enable teams, or have a small maximum player number than the number of spawns there edit the **plugins/SkyWars/arenas/<arena name>.yml** file. You can enable teams by changing **team-size** to something other than 1.

8. Add the arena name to the **enabled-arenas:** list in **main-config.yml**.

0. Restart the server. Your arena has now been added!

#### Updating

In SkyWars v2.1.0 and above, SkyWars will store a "block cache" .blocks file for all arenas it uses.

This increases speed of starting arenas, and it removes the requirement to keep the arena "template" around, but it has one disadvantage: the arena will stay the same even if you change the blocks where you created it.

This can be remedied by using the `/sws update-arena` command. This command will re-do the process above automatically for an existing arena, so that any changes you've made to the original template are propagated into the arenas used in games. This command has immediate effect.

Note that `/sws update-arena` won't work with the built-in arenas (skyblock-warriors and water-warriors) because they have no template / "place" in your server. If you want to modify them, you'll need to download the world file which stores them (https://github.com/SkyWars/SkyWars/blob/skywars-2.0.1/src/main/worlds/SkyWarsBaseWorld.zip), and add them as custom arenas with different names according to the steps above.

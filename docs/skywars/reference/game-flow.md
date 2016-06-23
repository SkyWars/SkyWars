Game & Queue Flow
-----------------

This is a general idea of how games should (eventually) happen in SkyWars:

- Player joins the queue
  - If there are the maximum number of players (for the biggest map), a game timer starts for 30 seconds
  - If there are at least the minimum number of players (for the biggest map), a game timers starts for 3 minutes
  - In any case, the player is shown a kit-picking GUI, and then a map-picking GUI.
    - Unless they joined by clicking a map-voting sign, in which case only a kit-picking GUI is shown.
- When a game timer starts, any previous game timers are canceled (only the latest matters)
- When a game timer reaches 10 seconds to go, map votes are frozen and the arena is copied/created
- When a game timer reaches zero, all players are transported into the arena, and a start timer is set for 5 seconds
- After the 5-second start timer countdown, the game starts and players are allowed to move

The current status:

- Player joins the queue
  - If there are the maximum number of players, a game is started
  - The player is shown a kit-picking GUI
- When a game starts, the arena is immediately created/copied, then players are teleported
- After teleportation, the game starts immediately.


The first setup shown, the "plan," is generally how I plan on making SkyWars work by v2.2.0.

It should be a good initial plan, but there might be some changes required. One thing I'm unsure about is the behavior for when the multiple maps have different maximum/minimum player counts, and which one players are voting for isn't decided yet. Should the timer behave as though the map with the maximum player count is the chosen one, or would some other behavior be better?

If you have any feedback on this plan, or you just have something to say, please comment!

The SkyWars resource thread on the Spigot forums is a good place to talk, or you can comment below with disqus!

---

[disqus-thread]

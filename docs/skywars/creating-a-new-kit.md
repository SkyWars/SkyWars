Creating a new kit
==================

Note: This feature is not complete, and won't be released until 2.1.0.

See [Configuring Kits](https://dabo.guru/projects/skywars/configuring-kits) on how to manually make a kit by entering config values.

1. Gather all items you want to be in the kit in your inventory. Armor slots will also be used when saving the kit.

2. Decide on a kit name, a cost for the kit, and a permission to access it.

   You can choose to create a kit with a cost, a permission, both or neither. If it doesn't have a cost, it will be free to use. If it doesn't have a permission, anyone will be able to use it.

3. Use **/sws createkit** while you have all the items in your inventory. This will save all items to the kit file, and the kit will be available immediately after you use this successfully.

   Example usages:

   | Completely free kit, usable by everyone            | **/sws createkit SuperAwesomeKit** |
   | Kit which costs $100 per round, usable by everyone | **/sws createkit EvenMoreAwesomeKit 100** |
   | Kit which is free, but requires the **server.donor** permission to use | **/sws createkit DonorKit 0 server.donor** |
   | Kit which requires the **server.donor** permission, and costs $50 to use per round | **/sws createkit DonorPlus 50 server.donor** |

4. You're done!

Bonus:

If you want to remove your kit, or tweak it slightly, you can check out [Configuring Kits](https://dabo.guru/projects/skywars/configuring-kits)

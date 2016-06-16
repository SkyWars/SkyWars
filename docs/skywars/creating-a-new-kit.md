Creating a new kit
==================

See [Configuring Kits](https://dabo.guru/projects/skywars/configuring-kits) on how to manually make a kit by entering config values, and how to add a description to your custom kit.

Warning: Make sure you've restarted the server since the last time you edited `kits.yml`. Using **/sws createkit** will overwrite any changes made to `kits.yml` since the last restart.

1. Gather all items you want to be in the kit in your inventory. Armor slots will also be used when saving the kit.

2. Decide on a kit name, a cost for the kit, and a permission to access it.

   You can choose to create a kit with a cost, a permission, both or neither. If it doesn't have a cost, it will be free to use. If it doesn't have a permission, anyone will be able to use it.

3. Use **/sws createkit** while you have all the items in your inventory. This will save all items to the kit file, and the kit will be available immediately after you use this successfully.

4. If you have the Kit GUI enabled on your server, the item in your main hand will be used as the Kit's totem, the item representing the kit in the GUI. You can modify the description for the kit and change the totem in the configuration, `kits.yml`.

4. You're done! The kit will be available immediately.

Example usages:

* Completely free kit, usable by everyone:
  * **/sws createkit SuperAwesomeKit**
* Kit which costs $100 per round, usable by everyone:
  * **/sws createkit EvenMoreAwesomeKit 100**
* Kit which is free, but requires the **server.donor** permission to use:
  * **/sws createkit DonorKit 0 server.donor**
* Kit which requires the **server.donor** permission, and costs $50 to use per round:
  * **/sws createkit DonorPlus 50 server.donor**


There are also 4 default kits provided in kits.yml. If you want to modify any existing kits, or remove them, check out [Configuring Kits](https://dabo.guru/projects/skywars/configuring-kits)

Configuring Kits - kits.yml
===========================

This file is located at `plugins/SkyWars/kits.yml`.

Note: This document is on how to manually type in new kits into the config.

For a much simpler way to do this in game just using your inventory, check out [Creating a new kit](https://dabo.guru/projects/skywars/creating-a-new-kit). (Note that this is only in 2.1.0, which hasn't been released!)

```yaml
# ####### kits.yml #######
#
# Kit configuration
#
# For documentation, please visit
# https://dabo.guru/projects/skywars/configuring-kits
# #########

# Note that to enable most of these kits, you must enable economy support in main-config.yml.
bowman:
  # This kit has a cost of $100. The cost is charged every game in which it is used.
  cost: 100
  items:
    # You can list any number of items here.
    # Each item has a type section. It also may optionally have an enchantments
    # section and amount value.
    # The type is a material name. You can get a list of these here:
    # https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
    - {type: ARROW, amount: 16}
    # When amount is not given, it defaults to 1.
    - {type: BOW}
whacker:
  cost: 20
  items:
    - {type: STICK, enchantments: {KNOCKBACK: 3}}
potions:
  # This kit demonstrates how to enter potions into kits.
  cost: 100
  items:
    # this is a drinkable invisibility potion
    # duration is given in seconds, default is 120.
    - {type: POTION, amount: 2, effects: [{type: INVISIBILITY, duration: 480}]}
    # this potion is a double edged sword - it gives speed, and poison.
    # however, the poison effect has an extended time, and the speed doesn't.
    - {type: POTION, amount: 2, splash: true, effects: [
                              {type: POISON, duration: 120},
                              {type: SPEED, duration: 80}]}
    # Full heal - the higher the amplifier, the more effect it has.
    - {type: POTION, amount: 4, effects: [{type: HEAL, amplifier: 5}]}

diamond-swordsman:
  # You can define a permission, a cost, or both for a kit.
  # When you define a permission, the kit is only shown to people with that
  # permission. This allows you to make kits only available to donors.
  permission: server.donor
  items:
    - {type: DIAMOND_SWORD, amount: 1}
full-donor-armor:
  # This kit is only available to donors, but it also costs $500 each game!
  permission: server.donor
  cost: 500
  # You can define a helmet, chestplate, leggings and boots.
  # These will automatically be placed in the armor slots of the player.
  helmet: {type: DIAMOND_HELMET}
  chestplate: {type: DIAMOND_CHESTPLATE, enchantments: {PROTECTION_PROJECTILE: 2}}
  leggings: {type: DIAMOND_LEGGINGS}
  # See link above for exact enchantment names
  boots: {type: DIAMOND_BOOTS, enchantments: {PROTECTION_FALL: 2}}
```
Complete list of possible names:
- Materials: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html.
- Enchantments: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html.
- Potions: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html

The links above have more information than needed, but they are continually the most up to date lists of full names.
All you need to use a material, enchantment or potion is the UPPERCASE name.

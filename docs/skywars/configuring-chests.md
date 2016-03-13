Configuring Chests - chests.yml
===============================

#### Overview

SkyWars's chest randomizer works with two configurations: one global, defining all possible items, and one which exists per-arena, defining how many items to put in each chest.

This global configuration is located at `plugins/SkyWars/chests.yml`. The arena configuration is located at `plugins/SkyWars/arena-name.yml` (replacing `arena-name` with the name of the specific arena).

The global configuration, `chests.yml`, defines a number of `levels`. Each level has a `value`, and percentage `chance`. `chests.yml` also defines a number of items which are contained in each level.

In each arena configuration, each chest has the option to enable random filling, a `total-value` setting, and a `min-level` setting. A chest won't contain items with a level lower then the `min-level`, and will contain more items the higher the `total-value` is.

When filling a chest, SkyWars will first pick a random level, using their percentage chance values. It will then add a random item from that level to the chest, and add the level's `item-value` to the total value in the chest. This process is repeated until the total value in the chest reaches the chest's defined "total-value".


#### Example global configuration

```yaml
levels:
  level-1:
    item-value: 1
    chance: 35
  level-2:
    item-value: 3
    chance: 35
  level-3:
    item-value: 5
    chance: 10
  level-4:
    item-value: 7
    chance: 5
  level-5:
    item-value: 10
    chance: 15
items:
  level-1:
  - type: WOOD_SWORD
  - type: LEATHER_HELMET
  - type: LEATHER_CHESTPLATE
  - type: LEATHER_LEGGINGS
  - type: LEATHER_BOOTS
  - type: PORK, amount: 3
  - {type: RAW_FISH, amount: 3}
  - {type: APPLE, amount: 3}
  - {type: ARROW, amount: 7}
  level-2:
  - type: WOOD_SWORD
  - type: GOLD_HELMET
  - type: GOLD_CHESTPLATE
  - type: GOLD_LEGGINGS
  - type: GOLD_BOOTS
  - type: MUSHROOM_SOUP
  - {type: GRILLED_PORK, amount: 3}
  - {type: BOWL, amount: 4}
  - {type: APPLE, amount: 2}
  - {type: RAW_BEEF, amount: 4}
  - {type: COOKED_BEEF, amount: 2}
  - {type: RAW_CHICKEN, amount: 4}
  - {type: COOKED_CHICKEN, amount: 2}
  - {type: ENDER_PEARL, amount: 2}
  level-3:
  - type: IRON_HELMET
  - type: IRON_CHESTPLATE
  - type: IRON_LEGGINGS
  - type: IRON_BOOTS
  - type: GOLD_SWORD
  - type: CHAINMAIL_HELMET
  - type: CHAINMAIL_CHESTPLATE
  - type: CHAINMAIL_BOOTS
  - {type: MELON_BLOCK, amount: 4}
  - {type: GOLDEN_APPLE, amount: 2}
  - type: FLINT_AND_STEEL
  - type: GOLD_SWORD
  level-4:
  - type: IRON_SWORD
  level-5:
  - type: DIAMOND_SWORD
  - type: DIAMOND_HELMET
  - type: DIAMOND_CHESTPLATE
  - type: DIAMOND_LEGGINGS
  - type: DIAMOND_BOOTS
  - {type: CAKE, amount: 2}
```

#### Example arena configuration:

TODO

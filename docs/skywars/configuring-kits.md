Configuring Kits - kits.yml
===========================

This file is located at `plugins/SkyWars/kits.yml`.

```yaml
# ####### kits.yml #######
#
# Kit configuration
#
# For documentation, please visit
# https://dabo.guru/skywars/configuring-kits
# #########

my-first-kit:
  # This kit has a cost of 20.
  # It is a bit overpowered, so you may want to remove it.
  cost: 20
  items:
    # You can list any number of items here.
    # Each item has a type:. It also may optionally have an enchantments:
    # and amount:
    # The type is a material name. You can get a list of these here:
    # http://tiny.cc/BukkitMaterial
    - {type: ARROW, amount: 22}
    # When amount is not given, it defaults to 1.
    - {type: BOW}
  # You can also define a helmet, chestplate, leggings and boots.
  # These will automatically be placed in the armor slots of the player.
  helmet: {type: DIAMOND_HELMET}
  chestplate: {type: DIAMOND_CHESTPLATE}
  leggings: {type: DIAMOND_LEGGINGS}
  # Every item may have an enchantments:. This contains a bunch of enchantments
  # for the item.
  boots: {type: DIAMOND_BOOTS, enchantments: {PROTECTION_FIRE: 2, PROTECTION_FALL: 1}}
donator-kit:
  # You can define a permission, a cost, or both for a kit.
  # When you define a permission, the kit is only shown to people with that
  # permission.
  # This donor kit is not that great, so you may want to change it.
  permission: myserver.donator.kit
  items:
    - {type: DIAMOND_SWORD, amount: 64}
costly-donator-kit:
  # This kit both requires the permission 'myserver.donator.kit2' and costs 200.
  permission: myserver.donator.kit2
  cost: 200
  boots: {type: DIAMOND_BOOTS}
```

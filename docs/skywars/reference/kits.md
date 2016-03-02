Complete Reference: kits.yml
============================

```yaml
KIT_NAME:
    cost: KIT_COST
    permission: KIT_PERMISSION
    items:
        - {
            type: ITEM_TYPE,
            amount: ITEM_AMOUNT,
            enchantments: {
                ENCHANTMENT_NAME: ENCHANTMENT_VALUE,
                ENCHANTMENT_NAME: ENCHANTMENT_VALUE,
                ...
            },
            potion: {
                type: POTION_TYPE,
                level: POTION_LEVEL,
                splash: POTION_SPLASH,
                extended: POTION_EXTENDED
            },
            extra-effects: [
                {type: EXTRA_TYPE, duration: EXTRA_DURATION, amplifier: EXTRA_AMPLIFIER},
                {type: EXTRA_TYPE, duration: EXTRA_DURATION, amplifier: EXTRA_AMPLIFIER},
                ...
            ]
        }
        - {
            type: ITEM_TYPE,
            ...
        }
    helmet: {
        type: ITEM_TYPE,
        amount: ITEM_AMOUNT,
        enchantments: { ... }
    }
    chestplate: {type: ITEM_TYPE, ...}
    leggings: {type: ITEM_TYPE, ...}
    boots: {type: ITEM_TYPE, ...}
KIT_NAME:
    ...
...
```

| Name | Default | Description |
| :--- | :------ | :---------- |
| KIT_NAME | N/A | Name that identifies the kit. Can be any YAML valid string, as long as it can be typed by players using **/sw kit**. |
| KIT_COST | 0   | Cost to use the kit. This is charged to any player using the kit each game they use it. Can be any integer equal to or more than 0. |
| KIT_PERMISSION | N/A | Permission to use the kit. Only players with this permission will be able to choose this kit. Can be any string accepted by your permissions plugin. If not specified, anyone with access to **/sw kit** will be able to use the kit. |
| items: | [] | List of items to be given to players using the kit, in the inventory. Any number of items can be specified. Default is an empty list. |
| ITEM_TYPE | N/A | Material of the item to be included in the kit. Needs to be a valid [Bukkit Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) value. |
| ITEM_AMOUNT | 1 | Size of item stack to be placed in inventory. This can be any integer between 1 and 64, and is not limited by regular stack size of items. |
| enchantments: | {} | Map of enchantment name to value. It isn't valid to have two enchantments of the same name |
| ENCHANTMENT_NAME | N/A | Name of the enchantment to apply. This must be a valid [Bukkit Enchantment Name](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html) value. This can be any enchantment: applying an armor enchantment to a sword, or applying knockback to a stick **is** allowed. |
| ENCHANTMENT_VALUE | N/A | Power of the enchantment. Can be any valid integer above 1, and is not limited by maximum enchantment levels. |
| potion: | {} | Section for the main potion effect. This is only valid for **type: POTION**. |
| POTION_TYPE | N/A | Type of potion to apply to item. This will set the item's "main potion", which will update the item's name and lore to match the potion. This must be a valid [Bukkit Potion Type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionType.html). |
| POTION_LEVEL | 1 | Sets the potion's power level. This can usually only be 1 or 2, but might be restricted to 0 and 1 depending on what **type:** and **extended:** are set to.
| POTION_SPLASH | false | Whether or not the potion "splashes". If true, the potion will be throwable. If false, the potion will be drinkable. |
| POTION_EXTENDED | false | Whether or not the potion has an extended time period. If true, the length of time the potion lasts will be increased. |
| extra-effects: | [] | List of extra potion effects to apply to the potion. Any effects with the same type as the main effect will replace the main effect. None of the effects here will change the item's name, but they will add to it's lore and effect. |
| EXTRA_TYPE | N/A | Type of extra effect to add. This can be a slightly bigger range of values than POTION_TYPE. This must be a valid [Bukkit Potion **Effect** Type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html), which is different from Bukkit Potion Type, what POTION_TYPE is limited to. |
| EXTRA_DURATION | 60 | Sets the duration of the extra potion effect. This can be any valid integer above 0. It is unclear what exact unit this value is in, and how much "duration" is needed for how many seconds depends on what type the effect is. For a SPEED potion, a value of 2000 produces a 1 minute, 14 second long result. |
| EXTRA_AMPLIFIER | 0 | Sets the power amplifier of the extra potion effect. Can be any integer from -128 to 128. A value of 0 represents the default effect power of a level 1 potion. |
| helmet: | {} | Sets an item in the player's helmet armor slot. The structure is the same as for items in the **items:** section. |
| chestplate: | {} | Same as **helmet:**, but sets the chestplate armor slot |
| leggings: | {} | Same as **helmet:**, but sets the leggings armor slot |
| boots: | {} | Same as **helmet:**, but sets the boots armor slot |

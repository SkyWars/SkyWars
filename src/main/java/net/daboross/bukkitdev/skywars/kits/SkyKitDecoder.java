/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.kits.SkyItemMeta;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyExtraEffectsMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyPotionMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class SkyKitDecoder {

    public static SkyKit decodeKit(ConfigurationSection section, String name) throws SkyConfigurationException {
        SkyKitItem[] armor;
        try {
            armor = decodeArmor(section);
        } catch (SkyConfigurationException ex) {
            throw new SkyConfigurationException(String.format("Invalid armor for SkyKit %s: %s", name, ex.getMessage()));
        }
        List<SkyKitItem> items;
        try {
            items = decodeInventory(section);
        } catch (SkyConfigurationException ex) {
            throw new SkyConfigurationException(String.format("Invalid inventory for SkyKit %s: %s", name, ex.getMessage()));
        }
        String permission = section.getString("permission");
        int cost = section.getInt("cost");
        return new SkyKitConfig(items, Arrays.asList(armor), name, cost, permission);
    }

    private static List<SkyKitItem> decodeInventory(ConfigurationSection section) throws SkyConfigurationException {
        List<?> items = section.getList("items");
        if (items != null) {
            List<SkyKitItem> result = new ArrayList<>(items.size());
            for (Object o : items) {
                if (o instanceof Map) {
                    //noinspection unchecked
                    result.add(decodeItemMap((Map<String, Object>) o));
                } else {
                    throw new SkyConfigurationException("Invalid thing in items list '" + o + "'.");
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    private static SkyKitItem[] decodeArmor(ConfigurationSection section) throws SkyConfigurationException {
        SkyKitItem[] armor = new SkyKitItem[4];
        if (section.contains("helmet")) {
            if (!section.isConfigurationSection("helmet")) {
                throw new SkyConfigurationException("Invalid helmet");
            } else {
                try {
                    armor[3] = decodeArmorItemConfig(section.getConfigurationSection("helmet"));
                } catch (SkyConfigurationException ex) {
                    throw new SkyConfigurationException("Invalid helmet: " + ex.getMessage());
                }
            }
        }
        if (section.contains("chestplate")) {
            if (!section.isConfigurationSection("chestplate")) {
                throw new SkyConfigurationException("Invalid chestplate");
            } else {
                try {
                    armor[2] = decodeArmorItemConfig(section.getConfigurationSection("chestplate"));
                } catch (SkyConfigurationException ex) {
                    throw new SkyConfigurationException("Invalid chestplate: " + ex.getMessage());
                }
            }
        }
        if (section.contains("leggings")) {
            if (!section.isConfigurationSection("leggings")) {
                throw new SkyConfigurationException("Invalid leggings");
            } else {
                try {
                    armor[1] = decodeArmorItemConfig(section.getConfigurationSection("leggings"));
                } catch (SkyConfigurationException ex) {
                    throw new SkyConfigurationException("Invalid leggings: " + ex.getMessage());
                }
            }
        }
        if (section.contains("boots")) {
            if (!section.isConfigurationSection("boots")) {
                throw new SkyConfigurationException("Invalid boots");
            } else {
                try {
                    armor[0] = decodeArmorItemConfig(section.getConfigurationSection("boots"));
                } catch (SkyConfigurationException ex) {
                    throw new SkyConfigurationException("Invalid boots: " + ex.getMessage());
                }
            }
        }
        return armor;
    }

    /**
     * This is like decodeItemMap, but is used for armor sections (where the item is a ConfigurationSection instead of a
     * Map).
     * <p/>
     * This method also does not decode potions and other item metadata besides enchantments, as it seems unlikely to be
     * neccessary for armor.
     */
    public static SkyKitItem decodeArmorItemConfig(ConfigurationSection section) throws SkyConfigurationException {
        if (!section.isString("type")) {
            throw new SkyConfigurationException("The item does not define a type");
        }
        String typeString = section.getString("type");
        int amount = section.isInt("amount") ? section.getInt("amount") : 1;
        Material type;
        type = Material.matchMaterial(typeString);
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid material names.");
        }
        Map<Enchantment, Integer> enchantments = null;
        if (section.contains("enchantments")) {
            if (section.isConfigurationSection("enchantments")) {
                ConfigurationSection enchantmentSection = section.getConfigurationSection("enchantments");
                Set<String> keys = enchantmentSection.getKeys(false);
                enchantments = new HashMap<>(keys.size());
                for (String key : keys) {
                    Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                    if (enchantment == null) {
                        throw new SkyConfigurationException("Invalid enchantment '" + key + "'. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid enchantments.");
                    }
                    if (enchantmentSection.isInt(key)) {
                        enchantments.put(enchantment, enchantmentSection.getInt(key));
                    } else {
                        throw new SkyConfigurationException("Invalid enchantment level '" + enchantmentSection.get(key) + "'. Not an integer.");
                    }
                }
            } else {
                throw new SkyConfigurationException("Enchantments invalid!");
            }
        }
        return new SkyKitItemConfig(type, amount, enchantments, Collections.<SkyItemMeta>emptyList());
    }

    @SuppressWarnings("unchecked")
    public static SkyKitItem decodeItemMap(Map<String, Object> map) throws SkyConfigurationException {
        Object typeO = map.get("type");
        if (typeO == null) {
            throw new SkyConfigurationException("The item does not define a type");
        } else if (!(typeO instanceof String)) {
            throw new SkyConfigurationException("The item type is not a string");
        }
        String typeString = (String) typeO;
        int amount = getMapInt(map, "amount", 1, "Item amount is not an integer");
        Material type;
        type = Material.matchMaterial(typeString);
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid material names.");
        }
        Map<Enchantment, Integer> enchantments = null;
        Object enchantmentO = map.get("enchantments");
        if (enchantmentO != null) {
            if (enchantmentO instanceof Map) {
                Map<String, Object> enchantmentMap = (Map<String, Object>) enchantmentO;
                enchantments = new HashMap<>(enchantmentMap.size());
                for (String key : enchantmentMap.keySet()) {
                    Object val = enchantmentMap.get(key);
                    Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                    if (enchantment == null) {
                        throw new SkyConfigurationException("Invalid enchantment '" + key + "'. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid enchantments.");
                    }
                    if (val instanceof Integer) {
                        enchantments.put(enchantment, (Integer) val);
                    } else {
                        throw new SkyConfigurationException("Invalid enchantment level '" + val + "'. Not an integer.");
                    }
                }
            } else {
                throw new SkyConfigurationException("Enchantments invalid (not a section)!");
            }
        }
        List<SkyItemMeta> meta = new ArrayList<>();
        Object potionO = map.get("potion");
        if (potionO != null) {
            if (potionO instanceof Map) {
                meta.add(new SkyPotionMeta(decodePotion((Map) potionO)));
            } else {
                throw new SkyConfigurationException("Potion invalid (not a map)!");
            }
        }
        Object effectsO = map.get("extra-effects");
        if (effectsO != null) {
            List<PotionEffect> effects;
            if (effectsO instanceof List) {
                List<?> effectsList = (List) effectsO;
                effects = new ArrayList<>(effectsList.size());
                for (Object obj : effectsList) {
                    if (obj instanceof Map) {
                        effects.add(decodePotionEffect((Map) obj));
                    } else {
                        throw new SkyConfigurationException("Invalid thing in items list '" + obj + "'.");
                    }
                }
            } else {
                throw new SkyConfigurationException("Effects invalid (not a list)!");
            }
            meta.add(new SkyExtraEffectsMeta(effects));
        }
        return new SkyKitItemConfig(type, amount, enchantments, meta);
    }

    @SuppressWarnings("unchecked")
    public static PotionEffect decodePotionEffect(Map<String, Object> map) throws SkyConfigurationException {
        Object typeO = map.get("type");
        if (typeO == null) {
            throw new SkyConfigurationException("The potion effect does not define a type");
        } else if (!(typeO instanceof String)) {
            throw new SkyConfigurationException("The potion effect type is not a string");
        }
        String typeString = (String) typeO;
        int amplifier = getMapInt(map, "amplifier", 0, "Potion effect power is not an integer");
        int duration = getMapInt(map, "duration", 60, "Potion effect duration is not an integer");

        PotionEffectType type;
        type = PotionEffectType.getByName(typeString.toUpperCase());
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid material names.");
        }
        return type.createEffect(duration, amplifier);
    }

    public static Potion decodePotion(Map<String, Object> map) throws SkyConfigurationException {
        Object potionTypeO = map.get("type");
        if (potionTypeO == null) {
            throw new SkyConfigurationException("The potion does not define a type");
        } else if (!(potionTypeO instanceof String)) {
            throw new SkyConfigurationException("The potion type is not a string");
        }
        String typeName = ((String) potionTypeO).replace(' ', '_').toUpperCase();
        PotionType type = PotionType.getByEffect(PotionEffectType.getByName(typeName));
        if (type == null) {
            type = PotionType.valueOf(typeName);
            if (type == null) {
                throw new SkyConfigurationException("Unknown potion type: " + potionTypeO);
            }
        }
        Object extendedO = map.get("extended");
        boolean extended = extendedO instanceof Boolean ? (Boolean) extendedO : false;
        Object splashO = map.get("splash");
        boolean splash = splashO instanceof Boolean ? (Boolean) splashO : false;
        int level = getMapInt(map, "level", 1, "Potion level is not an integer!");
        if (level < 1 || level > 2) {
            throw new SkyConfigurationException("Potion level must be either 1 or 2. Use extra-effects for effects with higher amplifiers");
        }
        Potion potion;
        try {
            potion = new Potion(type, level);
            if (splash) potion.splash();
            if (extended) potion.extend();
        } catch (IllegalArgumentException ex) {
            throw new SkyConfigurationException("Failed to create potion of type: " + type + " with splash: " + splash + ", extended: " + extended + ", level: " + level, ex);
        }
        return potion;
    }

    private static int getMapInt(Map<String, Object> map, String key, int def, String error) throws SkyConfigurationException {
        Object object = map.get(key);
        int value;
        if (object == null) {
            value = def;
        } else if (object instanceof Integer) {
            value = (Integer) object;
        } else {
            throw new SkyConfigurationException(error);
        }
        return value;
    }
}

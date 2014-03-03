/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

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
                    result.add(decodeItemMap((Map) o));
                } else {
                    throw new SkyConfigurationException("Invalid thing in items list '" + o + "'.");
                }
            }
            return result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static SkyKitItem[] decodeArmor(ConfigurationSection section) throws SkyConfigurationException {
        SkyKitItem[] armor = new SkyKitItem[4];
        if (section.contains("helmet")) {
            if (!section.isConfigurationSection("helmet")) {
                throw new SkyConfigurationException("Invalid helmet");
            } else {
                try {
                    armor[3] = decodeItemConfig(section.getConfigurationSection("helmet"));
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
                    armor[2] = decodeItemConfig(section.getConfigurationSection("chestplate"));
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
                    armor[1] = decodeItemConfig(section.getConfigurationSection("leggings"));
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
                    armor[0] = decodeItemConfig(section.getConfigurationSection("boots"));
                } catch (SkyConfigurationException ex) {
                    throw new SkyConfigurationException("Invalid boots: " + ex.getMessage());
                }
            }
        }
        return armor;
    }

    public static SkyKitItem decodeItemConfig(ConfigurationSection section) throws SkyConfigurationException {
        if (!section.isString("type")) {
            throw new SkyConfigurationException("The item does not define a type");
        }
        String typeString = section.getString("type");
        int amount = section.isInt("amount") ? section.getInt("amount") : 1;
        Material type;
        type = Material.getMaterial(typeString.toUpperCase());
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check http://tiny.cc/BukkitMaterial for a list of valid material names.");
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
                        throw new SkyConfigurationException("Invalid enchantment '" + key + "'. Check http://tiny.cc/BukkitEnchants for a list of valid enchantments.");
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
        return new SkyKitItemConfig(type, amount, enchantments == null ? Collections.EMPTY_MAP : enchantments);
    }

    public static SkyKitItem decodeItemMap(Map<String, Object> map) throws SkyConfigurationException {
        Object typeO = map.get("type");
        if (typeO == null) {
            throw new SkyConfigurationException("The item does not define a type");
        } else if (!(typeO instanceof String)) {
            throw new SkyConfigurationException("The item type is not a string");
        }
        String typeString = (String) typeO;
        Object amountO = map.get("amount");
        int amount;
        if (amountO == null) {
            amount = 1;
        } else if (amountO instanceof Integer) {
            amount = (Integer) amountO;
        } else {
            throw new SkyConfigurationException("Amount in item is not an integer");
        }
        Material type;
        type = Material.getMaterial(typeString.toUpperCase());
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check http://tiny.cc/BukkitMaterial for a list of valid material names.");
        }
        Map<Enchantment, Integer> enchantments = null;
        Object enchantmentO = map.get("enchantments");
        if (enchantmentO != null) {
            if (enchantmentO instanceof Map) {
                Map<String, Object> enchantmentMap = (Map) enchantmentO;
                enchantments = new HashMap<>(enchantmentMap.size());
                for (String key : enchantmentMap.keySet()) {
                    Object val = enchantmentMap.get(key);
                    Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                    if (enchantment == null) {
                        throw new SkyConfigurationException("Invalid enchantment '" + key + "'. Check http://tiny.cc/BukkitEnchants for a list of valid enchantments.");
                    }
                    if (val instanceof Integer) {
                        enchantments.put(enchantment, (Integer) val);
                    } else {
                        throw new SkyConfigurationException("Invalid enchantment level '" + val + "'. Not an integer.");
                    }
                }
            } else {
                throw new SkyConfigurationException("Enchantments invalid!");
            }
        }
        return new SkyKitItemConfig(type, amount, enchantments == null ? Collections.EMPTY_MAP : enchantments);
    }
}

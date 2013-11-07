/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
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
import java.util.List;
import java.util.Map;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class SkyKitDecoder {

    public static SkyKit decodeKit(ConfigurationSection section) throws SkyConfigurationException {
        SkyKitItem[] armor = decodeArmor(section);
        List<SkyKitItem> items = decodeInventory(section);
        String permission = section.getString("permission");
        int cost = section.getInt("cost");
        return new SkyKitConfig(items, Arrays.asList(armor), cost, permission);
    }

    private static List<SkyKitItem> decodeInventory(ConfigurationSection section) throws SkyConfigurationException {
        List<?> items = section.getList("items");
        List<SkyKitItem> result = new ArrayList<>(items.size());
        for (Object o : items) {
            if (o instanceof Map) {
                result.add(decodeItemMap((Map<String, Object>) o));
            } else {
                throw new SkyConfigurationException("Invalid thing in items list '" + o + "'.s");
            }
        }
        return result;
    }

    private static SkyKitItem[] decodeArmor(ConfigurationSection section) throws SkyConfigurationException {
        SkyKitItem[] armor = new SkyKitItem[4];
        if (section.contains("helmet")) {
            if (!section.isConfigurationSection("helmet")) {
                throw new SkyConfigurationException("Invalid helmet");
            } else {
                armor[3] = decodeItemConfig(section.getConfigurationSection("helmet"));
            }
        }
        if (section.contains("chestplate")) {
            if (!section.isConfigurationSection("chestplate")) {
                throw new SkyConfigurationException("Invalid chestplate");
            } else {
                armor[2] = decodeItemConfig(section.getConfigurationSection("helmet"));
            }
        }
        if (section.contains("leggings")) {
            if (!section.isConfigurationSection("leggings")) {
                throw new SkyConfigurationException("Invalid leggings");
            } else {
                armor[1] = decodeItemConfig(section.getConfigurationSection("helmet"));
            }
        }
        if (section.contains("boots")) {
            if (!section.isConfigurationSection("boots")) {
                throw new SkyConfigurationException("Invalid leggings");
            } else {
                armor[0] = decodeItemConfig(section.getConfigurationSection("helmet"));
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
        try {
            type = Material.getMaterial(typeString.toUpperCase());
        } catch (Exception e) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check http://tiny.cc/BukkitMaterial for a list of valid material names.");
        }
        return new SkyKitItemConfig(type, amount);
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
        try {
            type = Material.getMaterial(typeString.toUpperCase());
        } catch (Exception e) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check http://tiny.cc/BukkitMaterial for a list of valid material names.");
        }
        return new SkyKitItemConfig(type, amount);
    }
}

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
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyArmorColorMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyDurabilityMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyExtraEffectsMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyNameLoreMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyPotionMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyRawDataMeta;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class SkyKitDecoder {

    private SkyKitDecoder() {
    }

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
                    result.add(decodeItem(new MapMapSection((Map<String, Object>) o)));
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
                    armor[3] = decodeItem(new ConfigurationMapSection(section.getConfigurationSection("helmet")));
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
                    armor[2] = decodeItem(new ConfigurationMapSection(section.getConfigurationSection("chestplate")));
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
                    armor[1] = decodeItem(new ConfigurationMapSection(section.getConfigurationSection("leggings")));
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
                    armor[0] = decodeItem(new ConfigurationMapSection(section.getConfigurationSection("boots")));
                } catch (SkyConfigurationException ex) {
                    throw new SkyConfigurationException("Invalid boots: " + ex.getMessage());
                }
            }
        }
        return armor;
    }

    @SuppressWarnings("unchecked")
    public static SkyKitItem decodeItem(MapSection map) throws SkyConfigurationException {
        String typeString = map.getTypeString("The item");
        int amount = map.getInt("amount", 1, "Item amount is not an integer");
        Material type;
        type = Material.matchMaterial(typeString);
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid material names.");
        }
        Map<Enchantment, Integer> enchantments = null;
        MapSection enchantmentMap = map.getSection("enchantments", "Enchantments invalid: not a section!");
        if (enchantmentMap != null) {
            enchantments = new HashMap<>(enchantmentMap.size());
            for (String key : enchantmentMap.keySet()) {
                Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                if (enchantment == null) {
                    throw new SkyConfigurationException("Invalid enchantment '" + key + "'. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid enchantments.");
                }
                int value = enchantmentMap.getInt(key, "Invalid enchantment level `%s`: not an integer.");
                enchantments.put(enchantment, value);
            }
        }
        List<SkyItemMeta> meta = new ArrayList<>();

        Integer durability = map.getNullableInt("durability", "Item durability invalid: not an integer!");
        if (durability != null) {
            meta.add(new SkyDurabilityMeta(durability.shortValue()));
        }
        Integer rawData = map.getNullableInt("raw-data", "Item raw data invalid: not an integer!");
        if (rawData != null) {
            meta.add(new SkyRawDataMeta(rawData.byteValue()));
        }

        String name = map.getString("name", "Item name invalid: not a string!");
        List<String> lore = null;
        Object loreObject = map.get("lore");
        if (loreObject != null) {
            if (loreObject instanceof Map) {
                throw new SkyConfigurationException("Item lore invalid: not a list!");
            } else if (loreObject instanceof List) {
                List<Object> loreList = (List<Object>) loreObject;
                lore = new ArrayList<>(loreList.size());
                for (Object listObject : loreList) {
                    if (listObject instanceof Map || listObject instanceof List || listObject == null) {
                        throw new SkyConfigurationException("Item lore list item invalid: not a string!");
                    } else {
                        lore.add(listObject.toString());
                    }
                }
            } else {
                lore = Collections.singletonList(loreObject.toString());
            }
        }

        if (name != null || lore != null) {
            meta.add(new SkyNameLoreMeta(name, lore));
        }

        String armorColor = map.getString("armor-color", "Item armor color invalid: not a string!");
        if (armorColor != null) {
            meta.add(new SkyArmorColorMeta(Color.fromRGB(Integer.parseInt(armorColor, 16))));
        }

        MapSection potionMap = map.getSection("potion", "Item potion section invalid: not a map!");
        if (potionMap != null) {
            meta.add(new SkyPotionMeta(decodePotion(potionMap)));
        }

        List<Object> effectsList = map.getList("extra-effects", "Effects invalid: not a list!");
        if (effectsList != null) {
            List<PotionEffect> effects = new ArrayList<>(effectsList.size());
            for (Object obj : effectsList) {
                if (obj instanceof Map) {
                    effects.add(decodePotionEffect(new MapMapSection((Map) obj)));
                } else {
                    throw new SkyConfigurationException("Invalid thing in items list '" + obj + "'.");
                }
            }
            meta.add(new SkyExtraEffectsMeta(effects));
        }
        return new SkyKitItemConfig(type, amount, enchantments, meta);
    }

    public static PotionEffect decodePotionEffect(MapSection map) throws SkyConfigurationException {
        String typeString = map.getTypeString("The potion effect");
        int amplifier = map.getInt("amplifier", 0, "Potion effect power is not an integer");
        int duration = map.getInt("duration", 60, "Potion effect duration is not an integer");

        PotionEffectType type;
        type = PotionEffectType.getByName(typeString.toUpperCase());
        if (type == null) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid material names.");
        }
        return type.createEffect(duration, amplifier);
    }

    public static Potion decodePotion(MapSection map) throws SkyConfigurationException {
        String typeName = map.getTypeString("The potion");
        PotionType type = PotionType.getByEffect(PotionEffectType.getByName(typeName));
        if (type == null) {
            type = PotionType.valueOf(typeName);
            if (type == null) {
                throw new SkyConfigurationException("Unknown potion type: " + typeName);
            }
        }
        boolean extended = map.getBoolean("extended", false);
        boolean splash = map.getBoolean("splash", false);
        int level = map.getInt("level", 1, "Potion level is not an integer!");
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

    private static abstract class MapSection {

        public abstract int size();

        public abstract Object get(String key);

        public abstract Set<String> keySet();

        public boolean getBoolean(final String key, final boolean def) {
            Object object = get(key);
            return object instanceof Boolean ? (Boolean) object : def;
        }

        public int getInt(final String key, final String error) throws SkyConfigurationException {
            Object object = get(key);
            if (object instanceof Number) {
                return ((Number) object).intValue();
            } else {
                throw new SkyConfigurationException(String.format(error, object));
            }
        }

        public Integer getNullableInt(final String key, final String error) throws SkyConfigurationException {
            Object object = get(key);
            if (object == null) {
                return null;
            } else if (object instanceof Number) {
                return ((Number) object).intValue();
            } else {
                throw new SkyConfigurationException(String.format(error, object));
            }
        }

        public int getInt(final String key, final int def, String error) throws SkyConfigurationException {
            Object object = get(key);
            if (object == null) {
                return def;
            } else if (object instanceof Number) {
                return ((Number) object).intValue();
            } else {
                throw new SkyConfigurationException(String.format(error, object));
            }
        }

        public String getString(final String key, final String error) throws SkyConfigurationException {
            Object object = get(key);
            if (object == null) {
                return null;
            } else if (object instanceof List || object instanceof Map) {
                throw new SkyConfigurationException(String.format(error, object));
            } else {
                return object.toString();
            }
        }

        public abstract MapSection getSection(final String key, String error) throws SkyConfigurationException;

        public List<Object> getList(final String key, final String error) throws SkyConfigurationException {
            Object object = get(key);
            if (object == null) {
                return null;
            } else if (object instanceof List) {
                //noinspection unchecked
                return (List<Object>) object;
            } else {
                throw new SkyConfigurationException(String.format(error, object));
            }
        }

        /**
         * @param sectionName Name to use in error.
         * @return The string stored at "type". If null, throws an exception - this will never return null.
         * @throws SkyConfigurationException "%s does not define a type!" or "%s type is not a string"
         */

        public String getTypeString(final String sectionName) throws SkyConfigurationException {
            Object type = get("type");
            if (type == null) {
                throw new SkyConfigurationException(String.format("%s does not define a type", sectionName));
            } else if (!(type instanceof String)) {
                throw new SkyConfigurationException(String.format("%s type is not a string", sectionName));
            }
            return ((String) type).replace(' ', '_').toUpperCase();
        }
    }

    private static class MapMapSection extends MapSection {

        private final Map<String, Object> map;

        private MapMapSection(final Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public Object get(final String key) {
            return map.get(key);
        }

        @Override
        public Set<String> keySet() {
            return map.keySet();
        }

        @Override
        public MapSection getSection(final String key, String error) throws SkyConfigurationException {
            Object object = get(key);
            if (object == null) {
                return null;
            } else if (object instanceof Map) {
                //noinspection unchecked
                return new MapMapSection((Map<String, Object>) object);
            } else {
                throw new SkyConfigurationException(String.format(error, object));
            }
        }
    }

    private static class ConfigurationMapSection extends MapSection {

        private final ConfigurationSection section;

        private ConfigurationMapSection(final ConfigurationSection section) {
            this.section = section;
        }

        @Override
        public int size() {
            return section.getKeys(false).size();
        }

        @Override
        public Object get(final String key) {
            return section.get(key);
        }

        @Override
        public Set<String> keySet() {
            return section.getKeys(false);
        }

        @Override
        public MapSection getSection(final String key, String error) throws SkyConfigurationException {
            if (!section.contains(key)) {
                return null;
            } else if (section.isConfigurationSection(key)) {
                return new ConfigurationMapSection(section.getConfigurationSection(key));
            } else {
                throw new SkyConfigurationException(String.format(error, section.get(key)));
            }
        }
    }
}

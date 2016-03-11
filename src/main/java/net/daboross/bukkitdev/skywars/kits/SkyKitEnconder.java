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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.daboross.bukkitdev.skywars.api.kits.SkyItemMeta;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyArmorColorMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyDurabilityMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyExtraEffectsMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyNameLoreMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyPotionMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyRawDataMeta;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public class SkyKitEnconder {

    public static void encodeKit(SkyKit config, ConfigurationSection configRoot) {
        ConfigurationSection kitRoot = configRoot.createSection(config.getName());
        encodeArmorToConfig(config.getArmorContents(), kitRoot);
        encodeInventory(config.getInventoryContents(), kitRoot);
        String permission = config.getPermission();
        if (permission != null) {
            kitRoot.set("permission", permission);
        }
        String cost = config.getPermission();
        if (cost != null) {
            kitRoot.set("cost", cost);
        }
    }

    private static void encodeInventory(List<SkyKitItem> inventory, ConfigurationSection kitRoot) {
        if (inventory.isEmpty()) {
            return;
        }
        List<Map<String, Object>> itemList = new ArrayList<>(inventory.size());
        for (SkyKitItem item : inventory) {
            itemList.add(encodeItem(item));
        }
        kitRoot.set("items", itemList);
    }

    private static void encodeArmorToConfig(List<SkyKitItem> armor, ConfigurationSection section) {
        if (armor.get(3) != null) {
            section.set("helmet", encodeItem(armor.get(3)));
        }
        if (armor.get(2) != null) {
            section.set("chestplate", encodeItem(armor.get(2)));
        }
        if (armor.get(1) != null) {
            section.set("leggings", encodeItem(armor.get(1)));
        }
        if (armor.get(0) != null) {
            section.set("boots", encodeItem(armor.get(0)));
        }
    }

    public static Map<String, Object> encodeItem(SkyKitItem item) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", item.getMaterial().name());
        if (item.getAmount() != 1) {
            result.put("amount", item.getAmount());
        }
        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        if (enchantments != null) {
            Map<String, Object> enchantmentMap = new HashMap<>(enchantments.size());
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                enchantmentMap.put(entry.getKey().getName(), entry.getValue());
            }
            result.put("enchantments", enchantmentMap);
        }
        List<SkyItemMeta> metaList = item.getItemMeta();
        for (SkyItemMeta meta : metaList) {
            switch (meta.getType()) {
                case POTION:
                    result.put("potion", encodePotion(((SkyPotionMeta) meta).getPotion()));
                    break;
                case EXTRA_EFFECTS:
                    List<Map<String, Object>> effectsList = new ArrayList<>();
                    for (PotionEffect effect : ((SkyExtraEffectsMeta) meta).getEffects()) {
                        effectsList.add(encodePotionEffect(effect));
                    }
                    result.put("extra-effects", effectsList);
                    break;
                case RAW_DATA:
                    result.put("raw-data", ((SkyRawDataMeta) meta).getData());
                    break;
                case DURABILITY:
                    result.put("durability", ((SkyDurabilityMeta) meta).getDurability());
                    break;
                case NAME_LORE:
                    SkyNameLoreMeta nameLoreMeta = (SkyNameLoreMeta) meta;
                    if (nameLoreMeta.getName() != null) {
                        result.put("name", nameLoreMeta.getName().replace(ChatColor.COLOR_CHAR, '&'));
                    }
                    if (nameLoreMeta.getLore() != null) {
                        List<String> encodedLore = new ArrayList<>(nameLoreMeta.getLore().size());
                        for (String line : nameLoreMeta.getLore()) {
                            encodedLore.add(line.replace(ChatColor.COLOR_CHAR, '&'));
                        }
                        result.put("lore", encodedLore);
                    }
                    break;
                case ARMOR_COLOR:
                    result.put("armor-color", String.format("%06X", (0xFFFFFF & ((SkyArmorColorMeta) meta).getColor().asRGB())));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown meta type: " + meta);
            }
        }
        return result;
    }

    public static Map<String, Object> encodePotionEffect(PotionEffect effect) {
        Map<String, Object> result = new HashMap<>(3); // type, amplifier, duration
        result.put("type", effect.getType().getName());
        if (effect.getAmplifier() != 0) {
            result.put("amplifier", effect.getAmplifier());
        }
        result.put("duration", effect.getDuration());
        return result;
    }

    public static Map<String, Object> encodePotion(Potion potion) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", potion.getType().getEffectType().getName()); // prefer effect type name
        result.put("level", potion.getLevel());
        // don't store default values
        if (potion.hasExtendedDuration()) {
            result.put("extended", true);
        }
        if (potion.isSplash()) {
            result.put("splash", true);
        }
        return result;
    }
}

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
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyExtraEffectsMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyPotionMeta;
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
            itemList.add(encodeItemToMap(item));
        }
        kitRoot.set("items", itemList);
    }

    private static void encodeArmorToConfig(List<SkyKitItem> armor, ConfigurationSection section) {
        if (armor.get(3) != null) {
            encodeArmorItemToConfig(armor.get(3), section.createSection("helmet"));
        }
        if (armor.get(2) != null) {
            encodeArmorItemToConfig(armor.get(2), section.createSection("chestplate"));
        }
        if (armor.get(1) != null) {
            encodeArmorItemToConfig(armor.get(1), section.createSection("leggings"));
        }
        if (armor.get(0) != null) {
            encodeArmorItemToConfig(armor.get(0), section.createSection("boots"));
        }
    }

    /**
     * This is like decodeItemMap, but is used for armor sections (where the item is a ConfigurationSection instead of a
     * Map).
     * <p/>
     * This method also does not decode potions and other item metadata besides enchantments, as it seems unlikely to be
     * neccessary for armor.
     */
    public static void encodeArmorItemToConfig(SkyKitItem item, ConfigurationSection armorSection) {
        armorSection.set("type", item.getMaterial());
        armorSection.set("amount", item.getAmount());

        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        if (enchantments != null) {
            ConfigurationSection enchantmentSection = armorSection.createSection("enchantments");
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                enchantmentSection.set(entry.getKey().getName(), entry.getValue());
            }
        }
    }

    public static Map<String, Object> encodeItemToMap(SkyKitItem item) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", item.getMaterial().name());
        result.put("amount", item.getAmount());
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
                default:
                    throw new IllegalArgumentException("Unknown meta type: " + meta);
            }
        }
        return result;
    }

    public static Map<String, Object> encodePotionEffect(PotionEffect effect) {
        Map<String, Object> result = new HashMap<>(3); // type, amplifier, duration
        result.put("type", effect.getType().getName());
        result.put("amplifier", effect.getAmplifier());
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

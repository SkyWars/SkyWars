/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
import net.daboross.bukkitdev.skywars.api.kits.SkyItemMeta;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.SkyPotionData;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyArmorColorMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyDurabilityMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyExtraEffectsMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyNameLoreMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyPotionMeta;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyRawDataMeta;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.util.CrossVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import static net.daboross.bukkitdev.skywars.kits.KitConstants.DEFAULT_TOTEM;

public class SkyKitBukkitDecode {

    public static SkyKit inventoryToKit(PlayerInventory inventory, String name, String permission, int cost) {
        SkyKitItem[] armor = decodeArmor(inventory.getArmorContents());
        ItemStack[] rawItems = inventory.getContents();
        // In 1.9+, getContents() returns armor contents as well as inventory,
        // and only things with index 36 and below should be saved.
        int size = rawItems.length > 36 ? 36 : rawItems.length;
        List<SkyKitItem> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ItemStack rawItem = rawItems[i];
            if (rawItem != null && rawItem.getType() != Material.AIR) {
                items.add(decodeItem(rawItem));
            }
        }
        ItemStack totem = CrossVersion.getItemInHand(inventory);
        Material totemType = totem == null ? null : totem.getType();
        if (totemType == null) {
            if (items.isEmpty()) {
                for (SkyKitItem item : armor) {
                    if (item != null) {
                        totemType = item.getMaterial();
                        break;
                    }
                }
                if (totemType == null) {
                    totemType = DEFAULT_TOTEM;
                }
            } else {
                totemType = items.get(0).getMaterial();
            }
        }
        String description = SkyTrans.get(TransKey.CONFIG_KIT_DEFAULT_DESCRIPTION, name);
        return new SkyKitConfig(items, Arrays.asList(armor), name, description, totemType, cost, permission);
    }

    private static SkyKitItem[] decodeArmor(ItemStack[] itemStacks) {
        SkyKitItem[] results = new SkyKitItem[4];
        for (int i = 0; i < 4; i++) {
            if (itemStacks[i] != null && itemStacks[i].getType() != Material.AIR) {
                results[i] = decodeItem(itemStacks[i]);
            }
        }
        return results;
    }

    public static SkyKitItem decodeItem(ItemStack itemStack) {
        Material type = itemStack.getType();
        int amount = itemStack.getAmount();
        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        List<SkyItemMeta> skyMetaList = new ArrayList<>();
        byte itemData = itemStack.getData().getData();
        if (itemData != 0) {
            skyMetaList.add(new SkyRawDataMeta(itemData));
        }
        short durability = itemStack.getDurability();
        if (durability != 0) {
            skyMetaList.add(new SkyDurabilityMeta(durability));
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        String name = null;
        if (itemMeta.hasDisplayName()) {
            name = itemMeta.getDisplayName();
        }
        List<String> lore = null;
        if (itemMeta.hasLore()) {
            lore = new ArrayList<>(itemMeta.getLore());
        }
        if (name != null || lore != null) {
            skyMetaList.add(new SkyNameLoreMeta(name, lore));
        }
        if (itemMeta instanceof PotionMeta) {
            SkyPotionData potionData = SkyPotionData.extractData(itemStack);
            if (potionData.getPotionType() != SkyPotionData.FullPotionType.WATER
                    || potionData.isSplash() || potionData.isLingering()) {
                skyMetaList.add(new SkyPotionMeta(potionData));
            }
            if (SkyPotionData.modernApiSupported) {
                if (type == Material.SPLASH_POTION || type == Material.LINGERING_POTION) {
                    // The SkyPotionData will store if it's splash or lingering, and this way
                    // the kit file is backwards compatible with pre-1.9 Minecraft versions.
                    type = Material.POTION;
                }
            }
            // No need for these if the potion data is stored in a SkyPotionMeta.
            skyMetaList.remove(new SkyRawDataMeta(itemData));
            skyMetaList.remove(new SkyDurabilityMeta(durability));
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            if (potionMeta.hasCustomEffects()) {
                skyMetaList.add(new SkyExtraEffectsMeta(potionMeta.getCustomEffects()));
            }
        }
        if (itemMeta instanceof LeatherArmorMeta) {
            skyMetaList.add(new SkyArmorColorMeta(((LeatherArmorMeta) itemMeta).getColor()));
        }
        return new SkyKitItemConfig(type, amount, enchantments, skyMetaList);
    }
}

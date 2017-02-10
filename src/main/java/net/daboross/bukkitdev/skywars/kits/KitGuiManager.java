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
import java.util.List;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitGui;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitGuiManager implements SkyKitGui {

    private static final String COLOR_STRIKETHROUGH_PATTERN = "(?i)(" + ChatColor.COLOR_CHAR + "[0-9a-f])";
    private static final String COLOR_STRIKETHROUGH_REPLACEMENT = "$1" + ChatColor.COLOR_CHAR + "m";
    public static final String SECRET = String.valueOf(ChatColor.RESET) + ChatColor.MAGIC + ChatColor.RESET;
    private final SkyWars plugin;
    private final String kitGuiTitle;

    public KitGuiManager(final SkyWars plugin) {
        this.plugin = plugin;
        // Add three color codes simply to ensure that the title is unique and recognizable by the KitGuiListener.
        this.kitGuiTitle = SkyTrans.get(TransKey.KIT_GUI_TITLE)
                + ChatColor.RESET + ChatColor.MAGIC + ChatColor.RESET;
    }

    @Override
    public boolean openKitGui(Player player) {
        List<SkyKit> kits = plugin.getKits().getAvailableKits(player);
        List<SkyKit> unavailableKits = plugin.getKits().getUnavailableKits(player);

        if (kits.size() <= 0 && (!plugin.getConfiguration().isShowUnavailableKitsInGui() || unavailableKits.size() <= 0)) {
            return false;
        }

        int availableKitsSize = ((kits.size() + 8) / 9) * 9;
        int unavailableKitsSize = ((unavailableKits.size() + 8) / 9) * 9;

        int totalSize;
        if (plugin.getConfiguration().isShowUnavailableKitsInGui()) {
            totalSize = availableKitsSize + 9 + unavailableKitsSize;
        } else {
            totalSize = availableKitsSize;
        }

        Inventory inventory = Bukkit.createInventory(null, totalSize, kitGuiTitle);

        for (SkyKit kit : kits) {
            ItemStack kitItem = new ItemStack(kit.getTotem());
            ItemMeta meta = kitItem.getItemMeta();
            meta.setDisplayName(SECRET + SkyTrans.get(TransKey.KIT_GUI_TOTEM_TITLE, kit.getName()));
            List<String> description = kit.getDisplayDescription();
            List<String> lore = new ArrayList<>(description.size() + 1);
            lore.addAll(description);
            if (kit.getCost() != 0) {
                lore.add(SkyTrans.get(TransKey.KIT_GUI_COST_LORE, kit.getCost()));
            }
            if (lore.isEmpty()) {
                lore.add(SECRET);
            } else {
                lore.set(0, SECRET + lore.get(0));
            }
            meta.setLore(lore);
            kitItem.setItemMeta(meta);
            inventory.addItem(kitItem);
        }

        if (plugin.getConfiguration().isShowUnavailableKitsInGui()) {
            int unavaiableKitStartingPos = availableKitsSize + 9;

            for (int i = 0; i < unavailableKits.size(); i++) {
                SkyKit kit = unavailableKits.get(i);
                boolean hasPermission = kit.getPermission() == null || player.hasPermission(kit.getPermission());
                boolean hasCost = kit.getCost() != 0;
                boolean canAfford = !hasCost || plugin.getEconomyHook().canAfford(player, kit.getCost());
                ItemStack kitItem = new ItemStack(kit.getTotem());
                ItemMeta meta = kitItem.getItemMeta();
                meta.setDisplayName(SECRET + SkyTrans.get(TransKey.KIT_GUI_UNAVAILABLE_TOTEM_TITLE, kit.getName()));
                List<String> description = kit.getDisplayDescription();
                List<String> lore = new ArrayList<>(description.size() + (hasCost ? 1 : 0) + (hasPermission ? 0 : 1) + (canAfford ? 0 : 1));
                for (String line : description) {
                    lore.add(ChatColor.STRIKETHROUGH + line.replaceAll(COLOR_STRIKETHROUGH_PATTERN, COLOR_STRIKETHROUGH_REPLACEMENT));
                }
                if (hasCost) {
                    if (canAfford) {
                        lore.add(SkyTrans.get(TransKey.KIT_GUI_COST_LORE, kit.getCost()));
                    } else {
                        double diff = kit.getCost() - plugin.getEconomyHook().getAmount(player);
                        lore.add(SkyTrans.get(TransKey.KIT_GUI_UNAFFORDABLE_COST_LORE, kit.getCost()));
                        lore.add(SkyTrans.get(TransKey.KIT_GUI_UNAFFORDABLE_COST_LORE_2, diff));
                    }
                }
                if (!hasPermission) {
                    lore.add(SkyTrans.get(TransKey.KIT_GUI_NO_PERMISSION_LORE));
                }
                if (lore.isEmpty()) {
                    lore.add(SECRET);
                } else {
                    lore.set(0, SECRET + lore.get(0));
                }
                meta.setLore(lore);
                kitItem.setItemMeta(meta);
                inventory.setItem(unavaiableKitStartingPos + i, kitItem);
            }
        }

        player.openInventory(inventory);

        return true;
    }

    @Override
    public boolean autoOpenGuiIfApplicable(final Player player) {
        if (plugin.getConfiguration().isShowKitGuiOnJoin() && player.hasPermission("skywars.kitgui")) {
            return openKitGui(player);
        }
        return false;
    }

    @Override
    public String getKitGuiTitle() {
        return kitGuiTitle;
    }
}

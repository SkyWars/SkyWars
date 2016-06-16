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
package net.daboross.bukkitdev.skywars.listeners;

import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.kits.KitGuiManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class KitGuiListener implements Listener {

    private final SkyWars plugin;

    public KitGuiListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClickLow(InventoryClickEvent evt) {
        // This LOWEST handler ensures other plugins will know this event is canceled.
        if (isKitGuiInvolved(evt)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClickHigh(InventoryClickEvent evt) {
        if (isKitGuiInvolved(evt)) {
            evt.setCancelled(true);
        }

        if (evt.getInventory().getTitle().equals(plugin.getKitGui().getKitGuiTitle())) {
            ItemStack currentItem = evt.getCurrentItem();
            if (currentItem == null) {
                return;
            }
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null) {
                return;
            }
            String displayName = meta.getDisplayName();
            if (displayName == null
                    || !displayName.startsWith(KitGuiManager.SECRET)
                    || meta.getLore().isEmpty()
                    || !meta.getLore().get(0).startsWith(KitGuiManager.SECRET)) {
                SkyStatic.debug("Item does not match. displayName: %s, lore: %s", meta.getDisplayName(), meta.getLore());
                return;
            }

            String kitName = meta.getDisplayName().substring(KitGuiManager.SECRET.length());
            // getKit() strips color codes anyways, but the above line is probably a good idea anyways.
            // It isn't strictly needed, but it does make it more clear how the code works.
            SkyKit kit = plugin.getKits().getKit(kitName);

            HumanEntity playerEntity = evt.getWhoClicked();
            if (!(playerEntity instanceof Player)) {
                return; // Just ignore these, I guess?
            }
            Player player = (Player) playerEntity;

            // Honestly, whatever we do for this we're going to close the inventory afterwards.
            closeInventorySoon(evt.getWhoClicked());

            if (kit == null) {
                player.sendMessage(SkyTrans.get(TransKey.KIT_GUI_KIT_DISAPPEARED));
                SkyStatic.log(Level.WARNING, "Kit `" + ChatColor.stripColor(kitName).toLowerCase() + "` could not be found.");
                SkyStatic.log(Level.WARNING, "This could be caused by having non-color-char characters in the `totem-title` translation in messages.yml, or by a changing kit configuration.");
                return;
            }

            // After this is mostly all copied from the KitCommand class.

            if (kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
                player.sendMessage(SkyTrans.get(TransKey.CMD_KIT_NO_ACCESS, kit.getName()));
                return;
            }

            int cost = kit.getCost();
            if (cost == 0 || plugin.getEconomyHook().canAfford(player, cost)) {
                SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(player);
                skyPlayer.setSelectedKit(kit);
                if (cost == 0) {
                    player.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CHOSE_KIT, kit.getName()));
                } else {
                    player.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CHOSE_KIT_WITH_COST, kit.getName(), kit.getCost()));
                }
            } else {
                double diff = cost - plugin.getEconomyHook().getAmount(player);
                player.sendMessage(SkyTrans.get(TransKey.CMD_KIT_NOT_ENOUGH_MONEY, plugin.getEconomyHook().getCurrencySymbolWord(diff), kit.getName(), diff));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDragLow(InventoryDragEvent evt) {
        // This LOWEST handler ensures other plugins will know this event is canceled.
        if (isKitGuiInvolved(evt)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDragHigh(InventoryDragEvent evt) {
        if (isKitGuiInvolved(evt)) {
            evt.setCancelled(true);
        }
    }

    private boolean isKitGuiInvolved(InventoryEvent evt) {
        Inventory inventory = evt.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Player) {
            if (((Player) holder).getOpenInventory().getTitle().equals(plugin.getKitGui().getKitGuiTitle())) {
                // This ensure that a player is unable to manipulate their own items when a kit GUI is open.
                return true;
            }
        }
        return inventory.getTitle().equals(plugin.getKitGui().getKitGuiTitle());
    }

    private void closeInventorySoon(final HumanEntity player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTask(plugin);
    }
}

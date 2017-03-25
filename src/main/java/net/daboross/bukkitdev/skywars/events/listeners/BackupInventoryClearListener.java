/*
 * Copyright (C) 2017 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.events.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Mostly insurance, in case inventory save plugins save inventories from past games.
 */
public class BackupInventoryClearListener {

    private final SkyWars plugin;

    public BackupInventoryClearListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onGameStart(GameStartInfo info) {
        boolean workaround = plugin.isMultiinvWorkaroundEnabled();
        if (workaround) {
            final List<UUID> uuids = new ArrayList<>(info.getPlayers().size());
            for (Player player : info.getPlayers()) {
                uuids.add(player.getUniqueId());
            }
            SkyStatic.debug("Delayed clearing player inventories (MultiInv workaround). [ClearOnEnterGame.onGameStart]");
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    for (UUID uuid : uuids) {
                        Player p = plugin.getServer().getPlayer(uuid);
                        if (p != null) {
                            SkyStatic.debug("Clearing %s's inventory. [ClearOnEnterGameListener.onGameStart, delayed]", p.getUniqueId());
                            PlayerInventory inv = p.getInventory();
                            inv.clear();
                            inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
                        }
                    }
                }
            }, 4); // Wait 4 because the 'KitApplyListener' waits 5.
        } else {
            for (Player p : info.getPlayers()) {
                SkyStatic.debug("Clearing %s's inventory. [ClearOnEnterGameListener.onGameStart]", p.getUniqueId());
                PlayerInventory inv = p.getInventory();
                inv.clear();
                inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
            }
        }
    }
}

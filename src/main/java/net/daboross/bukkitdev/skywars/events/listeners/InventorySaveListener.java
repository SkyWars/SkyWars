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
package net.daboross.bukkitdev.skywars.events.listeners;

import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.players.SkySavedInventory;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import net.daboross.bukkitdev.skywars.player.SavedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventorySaveListener {

    private final SkyWars plugin;

    public InventorySaveListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onGameStart(GameStartInfo info) {
        boolean save = plugin.getConfiguration().isInventorySaveEnabled();
        for (Player p : info.getPlayers()) {
            if (save) {
                SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(p);
                skyPlayer.setSavedInventory(new SavedInventory(p));
            }
            PlayerInventory inv = p.getInventory();
            inv.clear();
            inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
        }
    }

    public void onPlayerRespawn(PlayerRespawnAfterGameEndInfo info) {
        boolean save = plugin.getConfiguration().isInventorySaveEnabled();
        Player player = info.getPlayer();
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
        if (save) {
            SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(player);
            SkySavedInventory savedInventory = skyPlayer.getSavedInventory();
            if (savedInventory != null) {
                savedInventory.apply(player);
            }
        }
    }
}

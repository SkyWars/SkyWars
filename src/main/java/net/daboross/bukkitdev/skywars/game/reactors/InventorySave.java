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
package net.daboross.bukkitdev.skywars.game.reactors;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.PlayerRespawnAfterGameEndInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@RequiredArgsConstructor
public class InventorySave {

    private final Map<String, InventorySaveInfo> inventorySaveInfo = new HashMap<>();
    private final SkyWars skywars;

    public void onGameStart(GameStartInfo info) {
        boolean save = skywars.getConfiguration().isInventorySaveEnabled();
        for (Player p : info.getPlayers()) {
            PlayerInventory inv = p.getInventory();
            if (save) {
                inventorySaveInfo.put(p.getName().toLowerCase(Locale.ENGLISH), new InventorySaveInfo(inv));
            }
            inv.clear();
            inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
        }
    }

    public void onPlayerRespawn(PlayerRespawnAfterGameEndInfo info) {
        PlayerInventory inv = info.getPlayer().getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
        InventorySaveInfo save = inventorySaveInfo.remove(info.getPlayer().getName().toLowerCase(Locale.ENGLISH));
        if (save != null) {
            save.apply(inv);
        }
    }
}

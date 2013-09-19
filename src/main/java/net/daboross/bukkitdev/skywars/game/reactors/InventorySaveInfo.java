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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 */
public class InventorySaveInfo {

    private final ItemStack[] items;
    private final ItemStack[] armor;

    public InventorySaveInfo( PlayerInventory inv ) {
        ItemStack[] contents = inv.getContents();
        items = new ItemStack[ contents.length ];
        ItemStack[] armorContents = inv.getArmorContents();
        armor = new ItemStack[ armorContents.length ];
        for ( int i = 0 ; i < contents.length ; i++ ) {
            items[i] = contents[i] == null ? null : contents[i].clone();
        }
        for ( int i = 0 ; i < armorContents.length ; i++ ) {
            armor[i] = armorContents[i] == null ? null : armorContents[i].clone();
        }
    }

    public void apply( PlayerInventory inv ) {
        inv.setContents( items );
        inv.setArmorContents( armor );
    }
}

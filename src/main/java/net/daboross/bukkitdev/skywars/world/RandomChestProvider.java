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
package net.daboross.bukkitdev.skywars.world;

import net.daboross.bukkitdev.bukkitstorageprotobuf.ChestProvider;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaChest;
import net.daboross.bukkitdev.skywars.api.config.RandomChests;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import org.bukkit.inventory.ItemStack;

public class RandomChestProvider implements ChestProvider {

    private final RandomChests configuration;
    private final SkyArena arena;

    public RandomChestProvider(final RandomChests configuration, final SkyArena arena) {
        this.configuration = configuration;
        this.arena = arena;
    }

    @Override
    public ItemStack[] getInventory(final int size, final int x, final int y, final int z) {
        SkyArenaChest chest = null;
        for (SkyArenaChest testChest : arena.getChests()) {
            if (testChest.getLocation().equals(new SkyBlockLocation(x, y, z, null))) {
                chest = testChest;
            }
        }
        // No filling? this should only happen if things are deleted from the config!
        if (chest == null) {
            return null;
        }
        if (!chest.isRandomizationEnabled()) {
            SkyStatic.debug("Not randomly filling chest at x: %s, y: %s, z: %s", x, y, z);
            return null;
        }
        SkyStatic.debug("Filling chest at x: %s, y: %s, z: %s", x, y, z);
        return configuration.getItems(size, chest.getChestLevel(), chest.getMinItemValue(), chest.getMaxItemValue());
    }
}

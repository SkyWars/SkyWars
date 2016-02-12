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
package net.daboross.bukkitdev.skywars.world.providers;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import net.daboross.bukkitdev.bukkitstorageprotobuf.MemoryBlockArea;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WorldEditProtobufStorageProvider extends ProtobufStorageProvider {

    protected BukkitWorld editWorld;

    public WorldEditProtobufStorageProvider(final SkyWars plugin) {
        super(plugin);
    }

    @Override
    public void copyArena(final World arenaWorld, final SkyArena arena, final SkyBlockLocation target) {
        Validate.isTrue(target.world.equals(arenaWorld.getName()), "Destination world not arena world.");

        if (editWorld == null) {
            editWorld = new BukkitWorld(arenaWorld);
        }

        MemoryBlockArea area = cache.get(arena.getArenaName());
        Validate.notNull(area, "Arena " + arena.getArenaName() + " not loaded.");


        area.applyWorldEdit(arenaWorld, editWorld, target.x, target.y, target.z);
    }

    @Override
    public void destroyArena(final World arenaWorld, final SkyArena arena, final SkyBlockLocation target) {
        Validate.isTrue(target.world.equals(arenaWorld.getName()), "Destination world not arena world.");

        if (editWorld == null) {
            editWorld = new BukkitWorld(arenaWorld);
        }

        SkyBlockLocationRange clearingArea = arena.getBoundaries().getClearing();
        SkyBlockLocation clearingMin = new SkyBlockLocation(target.x + clearingArea.min.x, target.y + clearingArea.min.y, target.z + clearingArea.min.z, null);
        SkyBlockLocation clearingMax = new SkyBlockLocation(target.x + clearingArea.max.x, target.y + clearingArea.max.y, target.z + clearingArea.max.z, null);

        for (int x = clearingMin.x; x <= clearingMax.x; x++) {
            for (int y = clearingMin.y; y <= clearingMax.y; y++) {
                for (int z = clearingMin.z; z <= clearingMax.z; z++) {
                    editWorld.setBlockType(new Vector(x, y, z), 0); // 0 = hardcoded Material.AIR
                }
            }
        }
        // TODO: do this part with WorldEdit too
        SkyBlockLocation halfDistance = new SkyBlockLocation((clearingMax.x - clearingMin.x) / 2, (clearingMax.y - clearingMin.y) / 2, (clearingMax.z - clearingMin.z) / 2, null);
        Location center = clearingMin.add(halfDistance).toLocationWithWorldObj(arenaWorld);
        for (Entity entity : arenaWorld.getNearbyEntities(center, halfDistance.x, halfDistance.y, halfDistance.z)) {
            entity.remove();
        }
    }
}

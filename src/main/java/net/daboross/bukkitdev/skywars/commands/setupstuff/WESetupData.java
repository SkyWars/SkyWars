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
package net.daboross.bukkitdev.skywars.commands.setupstuff;

import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * SetupData class with integration with WorldEdit
 */
public class WESetupData extends SetupData {

    public WESetupData(final SkyWars plugin) {
        super(plugin);
    }

    @Override
    protected SkyBlockLocationRange calculateOrigin(SkyBlockLocation min, SkyBlockLocation max) {
        World bukkitWorld = Bukkit.getWorld(min.world);
        if (bukkitWorld == null) {
            throw new IllegalStateException("Origin world '" + min.world + "' no longer loaded.");
        }
        LocalWorld world = new BukkitWorld(bukkitWorld);

        int minX = min.x;
        int minY = min.y;
        int minZ = min.z;
        int maxX = max.x;
        int maxY = max.y;
        int maxZ = max.z;

        // Each of these loops will reduce the empty space around the chosen area,
        //  by checking each plane of space and reducing the min/max of that direction if the space is clear.
        System.out.println("Calculating 1 minX:" + minX + " maxX:" + maxX);
        // Reducing minX
        while (minX < maxX
                && isClear(minX, minY, minZ, minX, maxY, maxZ, world)) {
            minX += 1;
        }
        System.out.println("Calculating 2 minY:" + minY + " maxY:" + maxY);
        // Reducing minY
        while (minY < maxY
                && isClear(minX, minY, minZ, maxX, minY, maxZ, world)) {
            minY += 1;
        }
        System.out.println("Calculating 3 minZ:" + minZ + " maxZ:" + maxZ);
        // Reducing minZ
        while (minZ < maxZ
                && isClear(minX, minY, minZ, maxX, maxY, minZ, world)) {
            minZ += 1;
        }
        System.out.println("Calculating 4 minX:" + minX + " maxX:" + maxX);
        // Reducing maxX
        while (maxX > minX
                && isClear(maxX, minY, minZ, maxX, maxY, maxZ, world)) {
            maxX -= 1;
        }
        System.out.println("Calculating 5");
        // Reducing maxY
        while (maxY > minY
                && isClear(minX, maxY, minZ, maxX, maxY, maxZ, world)) {
            maxY -= 1;
        }
        System.out.println("Calculating 6");
        // Reducing maxZ
        while (maxZ > minZ
                && isClear(minX, minY, maxZ, maxX, maxY, maxZ, world)) {
            maxZ -= 1;
        }
        System.out.println("Done");
        return new SkyBlockLocationRange(new SkyBlockLocation(minX, minY, minZ, world.getName()), new SkyBlockLocation(maxX, maxY, maxZ, world.getName()), world.getName());
    }

    private static boolean isClear(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, LocalWorld world) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (world.getBlockType(new Vector(x, y, z)) != 0) { // Material.AIR.getId() == 0
                        System.out.println("Not clear! block:" + world.getBlockType(new Vector(x, y, z)) + " minX:" + minX + " minY:" + minY + " minZ:" + minZ + " maxX:" + maxX + " maxY:" + maxY + " maxZ:" + maxZ);
                        return false;
                    }
                }
            }
        }
        System.out.println("It's clear! minX:" + minX + " minY:" + minY + " minZ:" + minZ + " maxX:" + maxX + " maxY:" + maxY + " maxZ:" + maxZ);
        return true;
    }
}

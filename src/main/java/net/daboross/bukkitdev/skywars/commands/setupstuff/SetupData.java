/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyBoundaries;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyBoundariesConfig;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

public class SetupData {

    private final SkyWars plugin;
    private String arenaName;
    private Path saveFile;
    private SkyBlockLocation originPos1;
    private SkyBlockLocation originPos2;
    private SkyBlockLocationRange originRange;
    private final List<SkyPlayerLocation> spawns = new ArrayList<>();

    public SetupData(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void setOriginPos1(SkyBlockLocation originPos1) {
        this.originPos1 = originPos1;
    }

    public void setOriginPos2(SkyBlockLocation originPos2) {
        this.originPos2 = originPos2;
    }

    public void setOriginBoundaries() {
        if (originPos1 != null && originPos2 != null) {
            SkyBlockLocation originMin = SkyBlockLocation.min(originPos1, originPos2);
            SkyBlockLocation originMax = SkyBlockLocation.max(originPos1, originPos2);
            originRange = calculateOrigin(originMin, originMax);
        }
    }

    public SkyArenaConfig convertToArenaConfig() {
        setOriginBoundaries();
        if (originRange == null) {
            throw new IllegalStateException("Origin not defined.");
        }
        SkyBoundariesConfig boundaries = new SkyBoundariesConfig(originRange);
        List<SkyPlayerLocation> processedSpawns = new ArrayList<>();
        for (SkyPlayerLocation spawn : spawns) {
            spawn = spawn.subtract(originRange.min);
            spawn = new SkyPlayerLocation(Math.floor(spawn.x) + 0.5, spawn.y, Math.floor(spawn.z) + 0.5, 0, 0, null);
            processedSpawns.add(spawn);
        }
        return new SkyArenaConfig(arenaName,
                processedSpawns,
                spawns.size(), // Number of teams
                1, // Team size
                20, // Placement Y
                boundaries);
    }

    protected SkyBlockLocationRange calculateOrigin(SkyBlockLocation min, SkyBlockLocation max) {
        World world = Bukkit.getWorld(min.world);
        if (world == null) {
            throw new IllegalStateException("Origin world '" + min.world + "' no longer loaded.");
        }
        int minX = min.x;
        int minY = min.y;
        int minZ = min.z;
        int maxX = max.x;
        int maxY = max.y;
        int maxZ = max.z;

        // Each of these loops will reduce the empty space around the chosen area,
        //  by checking each plane of space and reducing the min/max of that direction if the space is clear.
        // Reducing minX
        while (minX < maxX
                && isClear(minX, minY, minZ, minX, maxY, maxZ, world)) {
            minX += 1;
        }
        // Reducing minY
        while (minY < maxY
                && isClear(minX, minY, minZ, maxX, minY, maxZ, world)) {
            minY += 1;
        }
        // Reducing minZ
        while (minZ < maxZ
                && isClear(minX, minY, minZ, maxX, maxY, minZ, world)) {
            minZ += 1;
        }
        // Reducing maxX
        while (maxX > minX
                && isClear(maxX, minY, minZ, maxX, maxY, maxZ, world)) {
            maxX -= 1;
        }
        // Reducing maxY
        while (maxY > minY
                && isClear(minX, maxY, minZ, maxX, maxY, maxZ, world)) {
            maxY -= 1;
        }
        // Reducing maxZ
        while (maxZ > minZ
                && isClear(minX, minY, maxZ, maxX, maxY, maxZ, world)) {
            maxZ -= 1;
        }
        return new SkyBlockLocationRange(new SkyBlockLocation(minX, minY, minZ, world.getName()), new SkyBlockLocation(maxX, maxY, maxZ, world.getName()), world.getName());
    }

    private static boolean isClear(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public String getArenaName() {
        return arenaName;
    }

    public Path getSaveFile() {
        return saveFile;
    }

    public SkyBlockLocation getOriginPos1() {
        return originPos1;
    }

    public SkyBlockLocation getOriginPos2() {
        return originPos2;
    }

    public List<SkyPlayerLocation> getSpawns() {
        return spawns;
    }

    public void setArenaName(final String arenaName) {
        this.arenaName = arenaName;
    }

    public void setSaveFile(final Path saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public String toString() {
        return "SetupData{" +
                "plugin=" + plugin +
                ", arenaName='" + arenaName + '\'' +
                ", saveFile=" + saveFile +
                ", originPos1=" + originPos1 +
                ", originPos2=" + originPos2 +
                ", originRange=" + originRange +
                ", spawns=" + spawns +
                '}';
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SetupData)) return false;

        SetupData data = (SetupData) o;

        if (!arenaName.equals(data.arenaName)) return false;
        if (originRange != null ? !originRange.equals(data.originRange) : data.originRange != null) return false;
        if (originPos1 != null ? !originPos1.equals(data.originPos1) : data.originPos1 != null) return false;
        if (originPos2 != null ? !originPos2.equals(data.originPos2) : data.originPos2 != null) return false;
        if (saveFile != null ? !saveFile.equals(data.saveFile) : data.saveFile != null) return false;
        if (!spawns.equals(data.spawns)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arenaName.hashCode();
        result = 31 * result + (saveFile != null ? saveFile.hashCode() : 0);
        result = 31 * result + (originPos1 != null ? originPos1.hashCode() : 0);
        result = 31 * result + (originPos2 != null ? originPos2.hashCode() : 0);
        result = 31 * result + (originRange != null ? originRange.hashCode() : 0);
        result = 31 * result + spawns.hashCode();
        return result;
    }
}

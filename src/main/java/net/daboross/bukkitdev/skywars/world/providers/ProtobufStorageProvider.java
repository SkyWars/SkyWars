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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.daboross.bukkitdev.bukkitstorageprotobuf.ProtobufStorage;
import net.daboross.bukkitdev.bukkitstorageprotobuf.compiled.BlockStorage;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.world.VoidGenerator;
import net.daboross.bukkitdev.skywars.world.WorldProvider;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class ProtobufStorageProvider implements WorldProvider {

    private final Map<String, BlockStorage.BlockArea> cache = new HashMap<>();
    private final SkyWars plugin;

    public ProtobufStorageProvider(final SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadArena(final SkyArena arena) throws IOException {
        Path cachePath = plugin.getArenaPath().resolve(arena.getArenaName() + ".blocks");
        try (InputStream inputStream = new FileInputStream(cachePath.toFile())) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
                cache.put(arena.getArenaName(), BlockStorage.BlockArea.parseFrom(gzipInputStream));
            }
        } catch (FileNotFoundException e) {
            BlockStorage.BlockArea area = createCache(arena);
            cache.put(arena.getArenaName(), area);
            try (OutputStream outputStream = new FileOutputStream(cachePath.toFile())) {
                try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
                    area.writeTo(gzipOutputStream);
                }
            }
        }
    }

    private BlockStorage.BlockArea createCache(SkyArena source) {
        SkyBlockLocationRange origin = source.getBoundaries().getOrigin();
        String worldName = origin.world;
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            plugin.getLogger().log(Level.INFO, "Loading the ''{0}'' world to create a cache for the ''{1}'' arena. This only happens on the first startup after adding an arena, or when `/sw rebuildcache` is used.", new Object[]{worldName, source.getArenaName()});
            WorldCreator baseWorldCreator = new WorldCreator(worldName);
            baseWorldCreator.generateStructures(false);
            baseWorldCreator.generator(new VoidGenerator());
            baseWorldCreator.type(WorldType.FLAT);
            baseWorldCreator.seed(0);
            world = baseWorldCreator.createWorld();
        }
        BlockStorage.BlockArea area = ProtobufStorage.encode(world, origin.min.x, origin.min.y, origin.min.z, origin.max.x - origin.min.x, origin.max.y - origin.min.y, origin.max.z - origin.min.z, true);
        Bukkit.unloadWorld(world, false);
        plugin.getLogger().log(Level.INFO, "Done creating cache for arena ''{0}''", source.getArenaName());
        return area;
    }

    @Override
    public void clearLoadedArenas() {
        cache.clear();
    }

    @Override
    public void copyArena(final SkyArena arena, final SkyBlockLocation target) {
        BlockStorage.BlockArea storedArea = cache.get(arena.getArenaName());
        Validate.isTrue(storedArea != null, "Arena " + arena.getArenaName() + " not loaded.");

        World world = Bukkit.getWorld(target.world);

        Validate.isTrue(world != null, "Destination world not loaded.");

        ProtobufStorage.apply(storedArea, world, target.x, target.y, target.z);
    }

    @Override
    public void destroyArena(SkyArena arena, SkyBlockLocation target) {
        World world = Bukkit.getWorld(target.world);
        Validate.notNull(world, "Destionation world not loaded");

        SkyBlockLocationRange clearingArea = arena.getBoundaries().getClearing();
        SkyBlockLocation clearingMin = new SkyBlockLocation(target.x + clearingArea.min.x, target.y + clearingArea.min.y, target.z + clearingArea.min.z, null);
        SkyBlockLocation clearingmax = new SkyBlockLocation(target.x + clearingArea.max.x, target.y + clearingArea.max.y, target.z + clearingArea.max.z, null);

        for (int x = clearingMin.x; x <= clearingmax.x; x++) {
            for (int y = clearingMin.y; y <= clearingmax.y; y++) {
                for (int z = clearingMin.z; z <= clearingmax.z; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }
}

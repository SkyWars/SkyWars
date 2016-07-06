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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.daboross.bukkitdev.bukkitstorageprotobuf.MemoryBlockArea;
import net.daboross.bukkitdev.bukkitstorageprotobuf.ProtobufStorage;
import net.daboross.bukkitdev.bukkitstorageprotobuf.compiled.BlockStorage;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaChest;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaChestConfig;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.util.CrossVersion;
import net.daboross.bukkitdev.skywars.world.RandomChestProvider;
import net.daboross.bukkitdev.skywars.world.VoidGenerator;
import net.daboross.bukkitdev.skywars.world.WorldProvider;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;

public class ProtobufStorageProvider implements WorldProvider {

    protected final Map<String, MemoryBlockArea> cache = new HashMap<>();
    protected final SkyWars plugin;

    public ProtobufStorageProvider(final SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadArena(final SkyArenaConfig arena, final boolean forceReload) throws IOException {
        if (forceReload || cache.containsKey(arena.getArenaName())) {
            plugin.getLogger().log(Level.WARNING, "Updating arena blocks cache for arena ''{0}''.", arena.getArenaName());
        }
        boolean createdNewCache = false;
        Path cachePath = plugin.getArenaPath().resolve(arena.getArenaName() + ".blocks");
        BlockStorage.BlockArea area = null;
        if (!forceReload) {
            try (InputStream inputStream = new FileInputStream(cachePath.toFile())) {
                try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
                    area = BlockStorage.BlockArea.parseFrom(gzipInputStream);
                }
            } catch (FileNotFoundException ignored) {
            }
        }
        if (area == null) {
            try {
                area = createCache(arena);
                createdNewCache = true;
            } catch (IllegalStateException ex1) {
                if (ex1.getMessage().contains("Origin location not listed in configuration")) {
                    try (InputStream inputStream = plugin.getResourceAsStream("arenas/" + arena.getArenaName() + ".blocks")) {
                        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
                            area = BlockStorage.BlockArea.parseFrom(gzipInputStream);
                        }
                        plugin.getLogger().log(Level.INFO, "Loaded pre-built blocks cache file for arena {0}.", arena.getArenaName());
                    } catch (FileNotFoundException ex) {
                        throw new IOException("No origin listed in configuration, but no blocks file found in SkyWars jar file either!", ex);
                    }
                } else {
                    throw ex1;
                }
            }
            try (OutputStream outputStream = new FileOutputStream(cachePath.toFile())) {
                try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
                    area.writeTo(gzipOutputStream);
                }
            }
        }

        // We turn the BlockStorage.BlockArea into a StoredBlockArea here, not above, because StoredBlockArea can't write to a file.
        MemoryBlockArea memoryBlockArea = new MemoryBlockArea(area);

        if (createdNewCache || arena.getChestConfiguration() == null) {
            loadChests(arena, memoryBlockArea);
        }
        cache.put(arena.getArenaName(), memoryBlockArea);
    }

    @SuppressWarnings("deprecation")
    private void loadChests(final SkyArenaConfig arena, final MemoryBlockArea area) {
        SkyStatic.debug("Creating chest configuration for arena %s.", arena.getArenaName());
        List<SkyArenaChest> originalChests = arena.getChests();
        List<SkyArenaChest> chests = new ArrayList<>();
        for (int y = 0; y < area.lengthY; y++) {
            for (int x = 0; x < area.lengthX; x++) {
                length_z:
                for (int z = 0; z < area.lengthZ; z++) {
                    BlockStorage.Block block = area.blocks[y][x][z];
                    if (block.getId() == Material.CHEST.getId()
                            || block.getId() == Material.TRAPPED_CHEST.getId()) {
                        SkyBlockLocation location = new SkyBlockLocation(x, y, z, null);

                        // Check for existing configurations for this chest, and keep them if they exist.
                        for (SkyArenaChest testOldChest : originalChests) {
                            if (location.equals(testOldChest.getLocation())) {
                                chests.add(testOldChest);
                                continue length_z;
                            }
                        }
                        // If there isn't an existing configuration, just add a new one with default values
                        chests.add(new SkyArenaChestConfig(location));
                    }
                }
            }
        }
        arena.setChests(chests);
        plugin.getConfiguration().saveArena(arena);
    }

    private BlockStorage.BlockArea createCache(SkyArena source) {
        SkyBlockLocationRange origin = source.getBoundaries().getOrigin();
        if (origin == null) {
            // this message needs to contain "Origin location" as it is checked for in the UpdateArena command.
            // "Origin location not listed in configuration" is checked for in the method above as well.
            throw new IllegalStateException("Failed to load arena " + source.getArenaName() + ": Origin location not listed in configuration.");
        }
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
        BlockStorage.BlockArea area = ProtobufStorage.encode(world, origin.min.x, origin.min.y, origin.min.z, origin.max.x - origin.min.x + 1, origin.max.y - origin.min.y + 1, origin.max.z - origin.min.z + 1, true);
        Bukkit.unloadWorld(world, false);
        plugin.getLogger().log(Level.INFO, "Done creating cache for arena ''{0}''", source.getArenaName());
        return area;
    }

    @Override
    public void clearLoadedArenas() {
        cache.clear();
    }

    @Override
    public void copyArena(final World arenaWorld, final SkyArena arena, final SkyBlockLocation target) {
        Validate.isTrue(target.world.equals(arenaWorld.getName()), "Destination world is not arena world.");

        MemoryBlockArea area = cache.get(arena.getArenaName());
        Validate.notNull(area, "Arena " + arena.getArenaName() + " not loaded.");

        area.apply(arenaWorld, target.x, target.y, target.z, new RandomChestProvider(plugin.getChestRandomizer(), arena));
    }

    @Override
    public void destroyArena(final World arenaWorld, final SkyArena arena, final SkyBlockLocation target) {
        Validate.isTrue(target.world.equals(arenaWorld.getName()), "Destination world is not arena world.");

        SkyBlockLocationRange clearingArea = arena.getBoundaries().getClearing();
        SkyBlockLocation clearingMin = new SkyBlockLocation(target.x + clearingArea.min.x, target.y + clearingArea.min.y, target.z + clearingArea.min.z, null);
        SkyBlockLocation clearingMax = new SkyBlockLocation(target.x + clearingArea.max.x, target.y + clearingArea.max.y, target.z + clearingArea.max.z, null);

        for (int x = clearingMin.x; x <= clearingMax.x; x++) {
            for (int y = clearingMin.y; y <= clearingMax.y; y++) {
                for (int z = clearingMin.z; z <= clearingMax.z; z++) {
                    arenaWorld.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
        SkyBlockLocation halfDistance = new SkyBlockLocation((clearingMax.x - clearingMin.x) / 2, (clearingMax.y - clearingMin.y) / 2, (clearingMax.z - clearingMin.z) / 2, null);
        Location center = clearingMin.add(halfDistance).toLocationWithWorldObj(arenaWorld);
        for (Entity entity : CrossVersion.getNearbyEntities(center, halfDistance.x, halfDistance.y, halfDistance.z)) {
            entity.remove();
        }
    }
}

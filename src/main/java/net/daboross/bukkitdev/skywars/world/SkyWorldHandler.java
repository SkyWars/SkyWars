/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.world;

import java.util.Arrays;
import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/**
 *
 * @author daboross
 */
public class SkyWorldHandler {

    private final World world;
    private final World warriors;

    public SkyWorldHandler() {
        this.world = createWorld();
        this.warriors = createWarriorsWorld();
    }

    private World createWorld() {
        WorldCreator wc = new WorldCreator("SkyWarsArenaWorld");
        wc.generateStructures(false);
        wc.generator(new VoidGenerator());
        wc.type(WorldType.FLAT);
        wc.seed(0);
        return wc.createWorld();
    }

    private World createWarriorsWorld() {
        WorldCreator wc = new WorldCreator("SkyblockWarriors");
        wc.generateStructures(false);
        wc.generator(new VoidGenerator());
        wc.type(WorldType.FLAT);
        wc.seed(0);
        return wc.createWorld();
    }

    public World getWorld() {
        return world;
    }

    /**
     * @param id The ID for the arena
     * @return A list of player spawn positions
     */
    public Location[] createArena(int id) {
        Location[] spawnLocations = new Location[4];
        int modX = (id % 2) * 200;
        int modZ = (id / 2) * 200;
        int modY = 100;
        Location center = new Location(world, modX, modY, modZ);
        center.getBlock().getRelative(0, -2, 0).setType(Material.STONE);
        WorldCopier.copyArena(new SkyLocation(modX, modY, modZ, world.getName()));
        Arrays.fill(spawnLocations, new Location(world, modX, modY, modZ));
        return spawnLocations;
    }
}

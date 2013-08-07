/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.world;

import java.util.Random;
import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class SkyWorldHandler implements Listener {

    private final World world;

    public SkyWorldHandler() {
        this.world = createWorld();
        createWarriorsWorld();
    }

    private World createWorld() {
        WorldCreator wc = new WorldCreator(Statics.ARENA_WORLD_NAME);
        wc.generateStructures(false);
        wc.generator(new VoidGenerator());
        wc.type(WorldType.FLAT);
        wc.seed(0);
        return wc.createWorld();
    }

    private void createWarriorsWorld() {
        WorldCreator wc = new WorldCreator(Statics.BASE_WORLD_NAME);
        wc.generateStructures(false);
        wc.generator(new VoidGenerator());
        wc.type(WorldType.FLAT);
        wc.seed(0);
        wc.createWorld();
    }

    @EventHandler
    public void onGameStart(GameStartEvent evt) {
        Location[] spawns = createArena(evt.getId());
        for (int i = 0; i < 4; i++) {
            evt.getPlayers()[i].teleport(spawns[i]);
        }
    }

    /**
     * @param id The ID for the arena
     * @return A list of player spawn positions
     */
    private Location[] createArena(int id) {
        int modX = (id % 2) * 200;
        int modZ = (id / 2) * 200;
        int modY = 100;
        WorldCopier.copyArena(new SkyLocation(modX, modY, modZ, world.getName()));
        return getSpawnLocations(modX, modY, modZ);
    }

    private Location[] getSpawnLocations(int modX, int modY, int modZ) {
        Random r = new Random();
        Location[] finalLocations = new Location[4];
        boolean[] taken = new boolean[4];
        for (int i = 0; i < 4; i++) {
            int next = r.nextInt(4);
            while (taken[next]) {
                next = r.nextInt(4);
            }
            taken[next] = true;
            finalLocations[next] = Statics.RELATIVE_SPAWNS[next].add(modX, modY, modZ).toLocation();
        }
        return finalLocations;
    }
}

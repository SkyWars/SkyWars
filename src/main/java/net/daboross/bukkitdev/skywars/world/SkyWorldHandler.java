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
package net.daboross.bukkitdev.skywars.world;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.game.ArenaGame;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class SkyWorldHandler {

    private final SkyWars plugin;
    private final WorldCopier copier;

    public SkyWorldHandler(@NonNull SkyWars plugin) {
        this.plugin = plugin;
        this.copier = new WorldCopier(plugin);
    }

    public void findAndLoadRequiredWorlds() {
        for (SkyArena arena : plugin.getConfiguration().getEnabledArenas()) {
            loadWorld(arena.getBoundaries().getOrigin().world, arena.getArenaName());
        }
    }

    public void create() {
        if (plugin.getServer().getWorld(Statics.ARENA_WORLD_NAME) == null) {
            plugin.getLogger().info("Loading world '" + Statics.ARENA_WORLD_NAME + "'.");
            WorldCreator arenaWorldCreator = new WorldCreator(Statics.ARENA_WORLD_NAME);
            arenaWorldCreator.generateStructures(false);
            arenaWorldCreator.generator(new VoidGenerator());
            arenaWorldCreator.type(WorldType.FLAT);
            arenaWorldCreator.seed(0);
            arenaWorldCreator.createWorld();
            plugin.getLogger().info("Done loading world '" + Statics.ARENA_WORLD_NAME + "'.");
        } else {
            plugin.getLogger().info("The world '" + Statics.ARENA_WORLD_NAME + "' was already loaded.");
        }
    }

    public void loadWorld(String worldName, String arenaNameRequiring) {
        if (plugin.getServer().getWorld(worldName) == null) {
            plugin.getLogger().log(Level.INFO, "The arena ''{1}'' requires the world ''{0}'' to be loaded. Loading it now.", new Object[]{worldName, arenaNameRequiring});
            WorldCreator baseWorldCreator = new WorldCreator(worldName);
            baseWorldCreator.generateStructures(false);
            baseWorldCreator.generator(new VoidGenerator());
            baseWorldCreator.type(WorldType.FLAT);
            baseWorldCreator.seed(0);
            baseWorldCreator.createWorld();
            plugin.getLogger().log(Level.INFO, "Done loading world ''{0}''.", worldName);
        } else {
            plugin.getLogger().log(Level.INFO, "The arena ''{1}'' requires the world ''{0}'' to be loaded. It is already loaded.", new Object[]{worldName, arenaNameRequiring});
        }
    }

    public void onGameStart(GameStartInfo info) {
        ArenaGame game = info.getGame();
        SkyBlockLocation min = getMinLocation(game);
        game.setMin(min);
        copier.copyArena(min, game.getArena().getBoundaries().getOrigin());
        Player[] players = info.getPlayers();
        List<SkyPlayerLocation> spawns = game.getArena().getSpawns();
        Collections.shuffle(spawns);
        for (int i = 0, currentSpawn = 0; i < players.length; i++) {
            players[i].teleport(min.add(spawns.get(currentSpawn++)).toLocation());
            if (currentSpawn > spawns.size()) {
                currentSpawn = 0;
            }
        }
    }

    public void onGameEnd(GameEndInfo info) {
        SkyBlockLocation center = getMinLocation(info.getGame());
        copier.destroyArena(center, info.getGame().getArena().getBoundaries().getClearing().add(info.getGame().getMin()));
    }

    private SkyBlockLocation getMinLocation(SkyGame game) {
        return getMinLocation(game.getId(), game.getArena());
    }

    private SkyBlockLocation getMinLocation(int id, SkyArena arena) {
        int distanceApart = arena.getPlacement().getDistanceApart();
        int modX = (id % 2) * distanceApart;
        int modZ = (id / 2) * distanceApart;
        int modY = arena.getPlacement().getPlacementY();
        return new SkyBlockLocation(modX, modY, modZ, Statics.ARENA_WORLD_NAME);
    }
}

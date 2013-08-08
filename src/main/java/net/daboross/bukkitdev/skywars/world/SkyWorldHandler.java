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

import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class SkyWorldHandler implements Listener {

    public void create() {
        WorldCreator baseWorldCreator = new WorldCreator(Statics.BASE_WORLD_NAME);
        baseWorldCreator.generateStructures(false);
        baseWorldCreator.generator(new VoidGenerator());
        baseWorldCreator.type(WorldType.FLAT);
        baseWorldCreator.seed(0);
        baseWorldCreator.createWorld();
        WorldCreator arenaWorldCreator = new WorldCreator(Statics.ARENA_WORLD_NAME);
        arenaWorldCreator.generateStructures(false);
        arenaWorldCreator.generator(new VoidGenerator());
        arenaWorldCreator.type(WorldType.FLAT);
        arenaWorldCreator.seed(0);
        arenaWorldCreator.createWorld();
    }

    @EventHandler
    public void onGameStart(GameStartEvent evt) {
        SkyLocation center = getCenterLocation(evt.getId());
        WorldCopier.copyArena(center);
        Player[] players = evt.getPlayers();
        for (int i = 0; i < 4; i++) {
            players[i].teleport(Statics.RELATIVE_SPAWNS[i].add(center).toLocation());
        }
    }

    private SkyLocation getCenterLocation(int id) {
        int modX = (id % 2) * 200;
        int modZ = (id / 2) * 200;
        int modY = 100;
        return new SkyLocation(modX, modY, modZ, Statics.ARENA_WORLD_NAME);
    }
}

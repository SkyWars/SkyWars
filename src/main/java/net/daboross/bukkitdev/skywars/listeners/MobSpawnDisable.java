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
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.world.Statics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnDisable implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent evt) {
        switch (evt.getSpawnReason()) {
            case NATURAL:
            case JOCKEY:
            case CHUNK_GEN:
            case SPAWNER:
            case BUILD_WITHER:
            case VILLAGE_DEFENSE:
            case VILLAGE_INVASION:
            case REINFORCEMENTS:
            case NETHER_PORTAL:
                if (Statics.ARENA_WORLD_NAME.equals(evt.getLocation().getWorld().getName())) {
                    evt.setCancelled(true);
                }
                break;
            default:
                break;
        }
    }
}

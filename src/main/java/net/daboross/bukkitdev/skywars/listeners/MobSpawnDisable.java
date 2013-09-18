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
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.world.Statics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 *
 */
public class MobSpawnDisable implements Listener {

    @EventHandler
    public void onMobSpawn( CreatureSpawnEvent evt ) {
        if ( evt.getSpawnReason() == SpawnReason.DEFAULT ) {
            String world = evt.getLocation().getWorld().getName();
            if ( Statics.ARENA_WORLD_NAME.equalsIgnoreCase( world ) || Statics.BASE_WORLD_NAME.equals( world ) ) {
                evt.setCancelled( true );
            }
        }
    }
}

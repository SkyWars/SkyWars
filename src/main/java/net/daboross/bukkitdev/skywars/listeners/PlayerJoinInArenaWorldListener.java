/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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

import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.world.Statics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinInArenaWorldListener implements Listener {

    private final SkyWarsPlugin plugin;

    public PlayerJoinInArenaWorldListener(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        if (evt.getPlayer().getWorld().getName().equalsIgnoreCase(Statics.ARENA_WORLD_NAME)) {
            String name = evt.getPlayer().getName();
            SkyStatic.log(Level.SEVERE, "A player has logged in and is in the arena world! ({0})", name);
            SkyStatic.log(Level.SEVERE, "This should not happen under normal circumstances, and is likely the result of a server crash.");
            SkyStatic.log(Level.SEVERE, "SkyWars does not save inventories to disk, so if you have set SkyWars to save inventories, {0}''s inventory has likely been lost", name);
            SkyStatic.log(Level.SEVERE, "Teleporting {0} to the lobby location.", name);
            evt.getPlayer().teleport(plugin.getLocationStore().getLobbyPosition().toLocation());
            plugin.getResetHealth().resetHealth(evt.getPlayer());
        }
    }
}

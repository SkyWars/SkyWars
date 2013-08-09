/*
 * Copyright (C) 2013 daboross
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

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.GameEndEvent;
import net.daboross.bukkitdev.skywars.api.GameStartEvent;
import net.daboross.bukkitdev.skywars.api.LeaveGameEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairPlayerLeaveGameEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class EventForwardListener implements Listener {

    private final SkyWarsPlugin plugin;

    public EventForwardListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepairPlayerLeaveGame(PrepairPlayerLeaveGameEvent evt) {
        plugin.getServer().getPluginManager().callEvent(new LeaveGameEvent(plugin, evt.getId(), evt.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepairGameStart(PrepairGameStartEvent evt) {
        plugin.getServer().getPluginManager().callEvent(new GameStartEvent(plugin, evt.getPlayers(), evt.getId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepairGameEnd(PrepairGameEndEvent evt) {
        plugin.getServer().getPluginManager().callEvent(new GameEndEvent(plugin, evt.getPlayers(), evt.getId()));
    }
}

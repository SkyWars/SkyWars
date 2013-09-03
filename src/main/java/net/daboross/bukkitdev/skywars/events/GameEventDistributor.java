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
package net.daboross.bukkitdev.skywars.events;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.events.GameEndEvent;
import net.daboross.bukkitdev.skywars.api.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.api.events.LeaveGameEvent;

/**
 *
 * @author daboross
 */
public class GameEventDistributor {

    private final SkyWarsPlugin plugin;

    public GameEventDistributor(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void distribute(GameStartInfo info) {
        // -- Normal --
        plugin.getIDHandler().onGameStart(info);
        plugin.getCurrentGameTracker().onGameStart(info);
        plugin.getWorldHandler().onGameStart(info);
        plugin.getResetInventoryHealth().onGameStart(info); // Should be after WorldHandler
        plugin.getBroadcaster().broadcastStart(info);
        // -- After --
        plugin.getServer().getPluginManager().callEvent(new GameStartEvent(plugin, info.getGame(), info.getPlayers()));
    }

    public void distribute(GameEndInfo info) {
        // -- Initial --
        plugin.getIDHandler().onGameEnd(info);
        // -- Normal --
        plugin.getBroadcaster().broadcastEnd(info);
        // -- High --
        plugin.getWorldHandler().onGameEnd(info);
        // -- After --
        plugin.getServer().getPluginManager().callEvent(new GameEndEvent(plugin, info.getGame(), info.getAlivePlayers()));
    }

    public void distribute(PlayerLeaveGameInfo info) {
        // -- Normal --
        plugin.getCurrentGameTracker().onPlayerLeaveGame(info);
        plugin.getAttackerStorage().onPlayerLeaveGame(info);
        plugin.getResetInventoryHealth().onPlayerLeave(info);
        // -- After --
        plugin.getServer().getPluginManager().callEvent(new LeaveGameEvent(plugin, info.getId(), info.getPlayer()));
    }
}

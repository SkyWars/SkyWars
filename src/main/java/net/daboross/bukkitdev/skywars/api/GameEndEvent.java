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
package net.daboross.bukkitdev.skywars.api;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class GameEndEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final SkyWarsPlugin plugin;
    private final Player[] players;
    private final int id;

    public GameEndEvent(SkyWarsPlugin plugin, Player[] players, int id) {
        if (players == null || players.length != 4) {
            throw new IllegalArgumentException();
        }
        this.plugin = plugin;
        this.players = players;
        this.id = id;
    }

    public SkyWarsPlugin getPlugin() {
        return plugin;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getId() {
        return id;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

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
package net.daboross.bukkitdev.skywars.internalevents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class PrepairGameEndEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String[] playerNames;
    private final Player[] players = new Player[4];
    private final int id;
    private final boolean broadcast;

    public PrepairGameEndEvent(String[] names, int id, boolean broadcast) {
        if (names == null || names.length != 4) {
            throw new IllegalArgumentException();
        }
        this.playerNames = names;
        this.id = id;
        this.broadcast = broadcast;
        for (int i = 0; i < 4; i++) {
            if (names[i] != null) {
                Player p = Bukkit.getPlayer(names[i]);
                if (p == null) {
                    throw new IllegalArgumentException();
                }
                players[i] = p;
            }
        }
    }

    public int getId() {
        return id;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public Player[] getPlayers() {
        return players;
    }

    public boolean shouldBroadcast() {
        return broadcast;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

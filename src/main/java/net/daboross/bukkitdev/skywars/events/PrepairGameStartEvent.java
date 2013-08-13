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
package net.daboross.bukkitdev.skywars.events;

import net.daboross.bukkitdev.skywars.game.ArenaGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class PrepairGameStartEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String[] names;
    private final Player[] players = new Player[4];
    private ArenaGame game;
    private int id;

    public PrepairGameStartEvent(String[] names) {
        if (names == null || names.length != 4) {
            throw new IllegalArgumentException();
        }
        this.names = names;
        for (int i = 0; i < 4; i++) {
            Player p = Bukkit.getPlayer(names[i]);
            if (p == null) {
                throw new IllegalArgumentException();
            }
            players[i] = p;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGame(ArenaGame game) {
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public ArenaGame getGame() {
        return game;
    }

    public Player[] getPlayers() {
        return players;
    }

    public String[] getNames() {
        return names;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

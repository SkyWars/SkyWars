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
package net.daboross.bukkitdev.skywars.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;

/**
 *
 * @author daboross
 */
public class ArenaGame implements SkyGame {

    private final int id;
    private final List<String> alivePlayers;
    private final List<String> deadPlayers;

    public ArenaGame(int id, String[] originalPlayers) {
        this.id = id;
        this.alivePlayers = new ArrayList<>(Arrays.asList(originalPlayers));
        this.deadPlayers = new ArrayList<>(originalPlayers.length);
    }

    public void removePlayer(String playerName) {
        playerName = playerName.toLowerCase();
        if (!alivePlayers.remove(playerName)) {
            throw new IllegalArgumentException("Player not in game.");
        }
        deadPlayers.add(playerName);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public List<String> getAlivePlayers() {
        return Collections.unmodifiableList(alivePlayers);
    }

    @Override
    public List<String> getDeadPlayers() {
        return Collections.unmodifiableList(deadPlayers);
    }
}

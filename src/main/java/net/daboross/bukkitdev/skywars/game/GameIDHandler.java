/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;

public class GameIDHandler implements SkyIDHandler {

    private final Map<Integer, ArenaGame> currentGames = new HashMap<>();
    private int nextId;

    @Override
    public boolean gameRunning(int id) {
        return currentGames.containsKey(id);
    }

    @Override
    public ArenaGame getGame(int id) {
        return currentGames.get(id);
    }

    int getNextId() {
        return nextId++;
    }

    public void onGameStart(GameStartInfo info) {
        ArenaGame game = info.getGame();
        currentGames.put(game.getId(), game);
    }

    public void onGameEnd(GameEndInfo info) {
        Integer idInteger = info.getGame().getId();
        currentGames.remove(idInteger);
    }

    public void saveAndUnload(SkyWarsPlugin plugin) {
        SkyGameHandler handler = plugin.getGameHandler();
        while (!currentGames.isEmpty()) {
            int id = currentGames.keySet().iterator().next();
            handler.endGame(id, false, true);
        }
    }

    @Override
    public Collection<Integer> getCurrentIDs() {
        return Collections.unmodifiableCollection(currentGames.keySet());
    }
}

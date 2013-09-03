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
package net.daboross.bukkitdev.skywars.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;

/**
 *
 * @author daboross
 */
public class GameIDHandler implements SkyIDHandler {

    private final Map<Integer, ArenaGame> currentGames = new HashMap<>();
    private final List<Integer> currentIDs = new ArrayList<Integer>();

    @Override
    public boolean gameRunning(int id) {
        return currentGames.containsKey(id);
    }

    @Override
    public ArenaGame getGame(int id) {
        return currentGames.get(id);
    }

    int getNextId() {
        int id = 0;
        while (currentGames.containsKey(id)) {
            id++;
        }
        return id;
    }

    public void onGameStart(GameStartInfo info) {
        ArenaGame game = info.getGame();
        currentGames.put(game.getID(), game);
        currentIDs.add(game.getID());
    }

    public void onGameEnd(GameEndInfo info) {
        int id = info.getGame().getID();
        currentGames.remove(id);
        currentIDs.remove(id);
    }

    public void saveAndUnload(SkyWarsPlugin plugin) {
        GameHandler handler = plugin.getGameHandler();
        while (!currentIDs.isEmpty()) {
            int id = currentIDs.get(0);
            if (getGame(id) != null) {
                handler.endGame(id, false);
            }
        }
    }

    @Override
    public List<Integer> getCurrentIDs() {
        return Collections.unmodifiableList(currentIDs);
    }
}

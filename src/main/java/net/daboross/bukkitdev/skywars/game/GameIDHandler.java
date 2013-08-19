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
//import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.events.PrepairGameStartEvent;
//import net.daboross.bukkitdev.skywars.events.UnloadListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class GameIDHandler implements Listener,
//        UnloadListener, 
        SkyIDHandler {

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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameStart(PrepairGameStartEvent evt) {
        int id = 0;
        while (currentGames.containsKey(id)) {
            id++;
        }
        ArenaGame game = new ArenaGame(id, evt.getNames());
        currentGames.put(id, game);
        currentIDs.add(id);
        evt.setId(id);
        evt.setGame(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameEnd(PrepairGameEndEvent evt) {
        int id = evt.getGame().getID();
        currentGames.remove(id);
        currentIDs.remove(id);
    }

//    @Override
//    public void saveAndUnload(SkyWarsPlugin plugin) {
//        GameHandler handler = plugin.getGameHandler();
//        while (!currentIDs.isEmpty()) {
//            int id = currentIDs.get(0);
//            if (getGame(id) != null) {
//                handler.endGame(id, false);
//            }
//        }
//    }

    @Override
    public List<Integer> getCurrentIDs() {
        return Collections.unmodifiableList(currentIDs);
    }
}

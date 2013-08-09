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
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.internalevents.UnloadListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class GameIdHandler implements Listener, UnloadListener {

    private final Map<Integer, String[]> currentGames = new HashMap<Integer, String[]>();
    private final List<Integer> currentIds = new ArrayList<Integer>();

    public String[] getPlayers(int id) {
        return currentGames.get(id);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameStart(PrepairGameStartEvent evt) {
        int id = 0;
        while (currentGames.containsKey(id)) {
            id++;
        }
        currentGames.put(id, evt.getNames());
        currentIds.add(id);
        evt.setId(id);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameEnd(PrepairGameEndEvent evt) {
        currentGames.remove(evt.getId());
        currentIds.remove(evt.getId());
    }

    @Override
    public void saveAndUnload(SkyWarsPlugin plugin) {
        GameHandler handler = plugin.getGameHandler();
        while (!currentIds.isEmpty()) {
            int id = currentIds.get(0);
            if (getPlayers(id) != null) {
                handler.endGame(id, false);
            }
        }
    }

    public List<Integer> getCurrentIds() {
        return Collections.unmodifiableList(currentIds);
    }
}

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
import java.util.List;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class GameQueue implements Listener {

    private final SkyWarsPlugin plugin;
    private final List<String> currentlyQueued;

    public GameQueue(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.currentlyQueued = new ArrayList<String>(4);
    }

    public boolean inQueue(String player) {
        return currentlyQueued.contains(player.toLowerCase());
    }

    public void queuePlayer(String player) {
        player = player.toLowerCase();
        if (!currentlyQueued.contains(player)) {
            currentlyQueued.add(player);
        }
        if (currentlyQueued.size() == 4) {
            plugin.getGameHandler().startNewGame();
            currentlyQueued.clear();
        }
    }

    public void removePlayer(String player) {
        currentlyQueued.remove(player.toLowerCase());
    }

    public String[] getQueueCopy() {
        return currentlyQueued.toArray(new String[currentlyQueued.size()]);
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.Randomation;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.game.SkyGameQueue;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameQueue implements SkyGameQueue {

    private final SkyWarsPlugin plugin;
    private List<String> currentlyQueued;
    private SkyArena nextArena;
    private int nextArenaOrderedNumber = 0;

    public GameQueue(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        prepareNextArena();
    }

    @Override
    public boolean inQueue(String player) {
        return currentlyQueued.contains(player.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void queuePlayer(Player player) {
        String playerName = player.getName().toLowerCase(Locale.ENGLISH);
        if (!currentlyQueued.contains(playerName)) {
            currentlyQueued.add(playerName);
        }
        plugin.getDistributor().distribute(new PlayerJoinQueueInfo(player));
        if (currentlyQueued.size() >= nextArena.getNumPlayers()) {
            plugin.getDistributor().distribute(new GameStartInfo(getNextGame()));
        }
    }

    @Override
    public void queuePlayer(String playerName) {
        queuePlayer(Bukkit.getPlayerExact(playerName));
    }

    @Override
    public void removePlayer(String playerName) {
        removePlayer(Bukkit.getPlayerExact(playerName));
    }

    @Override
    public void removePlayer(Player player) {
        currentlyQueued.remove(player.getName().toLowerCase(Locale.ENGLISH));
        plugin.getDistributor().distribute(new PlayerLeaveQueueInfo(player));
    }

    public ArenaGame getNextGame() {
        if (currentlyQueued.size() < 2) {
            throw new IllegalStateException("Queue size smaller than 2");
        }
        Collections.shuffle(currentlyQueued);
        String[] queueCopy = currentlyQueued.toArray(new String[currentlyQueued.size()]);
        int id = plugin.getIDHandler().getNextId();
        ArenaGame game = new ArenaGame(nextArena, id, queueCopy);
        prepareNextArena();
        return game;
    }

    private void prepareNextArena() {
        SkyConfiguration config = plugin.getConfiguration();
        List<? extends SkyArena> enabledArenas = config.getEnabledArenas();
        switch (config.getArenaOrder()) {
            case ORDERED:
                if (nextArenaOrderedNumber >= enabledArenas.size()) {
                    nextArenaOrderedNumber = 0;
                }
                nextArena = enabledArenas.get(nextArenaOrderedNumber);
                break;
            case RANDOM:
                nextArena = Randomation.getRandom(enabledArenas);
                break;
            default:
                plugin.getLogger().log(Level.WARNING, "[GameQueue] Invalid ArenaOrder found in config!");
                nextArena = null;
                throw new IllegalStateException("Invalid ArenaOrder found in config");
        }
        currentlyQueued = new ArrayList<>(nextArena.getNumPlayers());
    }

    @Override
    public String[] getCopy() {
        return currentlyQueued.toArray(new String[currentlyQueued.size()]);
    }

    @Override
    public int getNumPlayersInQueue() {
        return currentlyQueued.size();
    }
}

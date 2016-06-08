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
import java.util.UUID;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.Randomation;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
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
    private List<UUID> currentlyQueued;
    private SkyArena nextArena;
    private int nextArenaOrderedNumber = 0;

    public GameQueue(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        prepareNextArena();
    }

    @Override
    public boolean inQueue(UUID uuid) {
        return currentlyQueued.contains(uuid);
    }

    @Override
    public void queuePlayer(Player playerUuid) {
        UUID uuid = playerUuid.getUniqueId();
        if (!currentlyQueued.contains(uuid)) {
            currentlyQueued.add(uuid);
        }
        plugin.getDistributor().distribute(new PlayerJoinQueueInfo(playerUuid));
        if (currentlyQueued.size() >= nextArena.getNumPlayers()) {
            plugin.getDistributor().distribute(new GameStartInfo(getNextGame()));
        }
    }

    @Override
    public void queuePlayer(UUID uuid) {
        queuePlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public void removePlayer(UUID uuid) {
        removePlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public void removePlayer(Player player) {
        currentlyQueued.remove(player.getUniqueId());
        plugin.getDistributor().distribute(new PlayerLeaveQueueInfo(player));
    }

    public ArenaGame getNextGame() {
        if (currentlyQueued.size() < 2) {
            throw new IllegalStateException("Queue size smaller than 2");
        }
        Collections.shuffle(currentlyQueued);
        UUID[] queueCopy = currentlyQueued.toArray(new UUID[currentlyQueued.size()]);
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
                nextArena = enabledArenas.get(nextArenaOrderedNumber++);
                SkyStatic.debug("ORDERED: Choosing arena %s", nextArena.getArenaName());
                break;
            case RANDOM:
                nextArena = Randomation.getRandom(enabledArenas);
                SkyStatic.debug("RANDOM: Choosing arena %s", nextArena.getArenaName());
                break;
            default:
                plugin.getLogger().log(Level.WARNING, "[GameQueue] Invalid ArenaOrder found in config!");
                nextArena = null;
                throw new IllegalStateException("Invalid ArenaOrder found in config");
        }
        currentlyQueued = new ArrayList<>(nextArena.getNumPlayers());
    }

    @Override
    public UUID[] getCopy() {
        return currentlyQueued.toArray(new UUID[currentlyQueued.size()]);
    }

    @Override
    public int getNumPlayersInQueue() {
        return currentlyQueued.size();
    }

    @Override
    public SkyArena getPlannedArena() {
        return nextArena;
    }
}

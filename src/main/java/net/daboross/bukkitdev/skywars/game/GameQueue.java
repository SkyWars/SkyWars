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
import java.util.Collection;
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
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinSecondaryQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveSecondaryQueueInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameQueue implements SkyGameQueue {

    private final SkyWarsPlugin plugin;
    private List<UUID> queueNext;
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
    public boolean inSecondaryQueue(final UUID uuid) {
        return queueNext.contains(uuid);
    }

    @Override
    public boolean queuePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!currentlyQueued.contains(uuid)) {
            if (isQueueFull()) {
                queueNext.add(uuid);
                plugin.getDistributor().distribute(new PlayerJoinSecondaryQueueInfo(player));
                return false;
            } else {
                currentlyQueued.add(uuid);
                plugin.getDistributor().distribute(new PlayerJoinQueueInfo(player, isQueueFull(), areMinPlayersPresent()));
                return true;
            }
        }
        return true;
    }

    @Override
    public void removePlayer(Player player) {
        if (queueNext.remove(player.getUniqueId())) {
            plugin.getDistributor().distribute(new PlayerLeaveSecondaryQueueInfo(player));
        } else if (currentlyQueued.remove(player.getUniqueId())) {
            plugin.getDistributor().distribute(new PlayerLeaveQueueInfo(player, areMinPlayersPresent()));
            if (!queueNext.isEmpty()) {
                Player p = Bukkit.getPlayer(queueNext.remove(0));
                plugin.getDistributor().distribute(new PlayerLeaveSecondaryQueueInfo(player));
                queuePlayer(p);
            }
        }
    }

    public ArenaGame getNextGame() {
        if (currentlyQueued.size() < nextArena.getMinPlayers()) {
            throw new IllegalStateException("Queue size smaller than minimum player count (" + currentlyQueued.size() + " < " + nextArena.getMinPlayers() + ")");
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
        final List<UUID> joinNext = queueNext;
        this.queueNext = new ArrayList<>();
        if (joinNext != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (UUID uuid : joinNext) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION));
                            queuePlayer(p);
                        }
                    }
                }
            }.runTask(plugin);
        }
    }

    @Override
    public UUID[] getCopy() {
        return currentlyQueued.toArray(new UUID[currentlyQueued.size()]);
    }

    public UUID[] getSecondaryCopy() {
        return queueNext.toArray(new UUID[queueNext.size()]);
    }

    @Override
    public Collection<UUID> getInQueue() {
        return Collections.unmodifiableCollection(currentlyQueued);
    }

    @Override
    public Collection<UUID> getInSecondaryQueue() {
        return Collections.unmodifiableCollection(queueNext);
    }

    @Override
    public int getNumPlayersInQueue() {
        return currentlyQueued.size();
    }

    @Override
    public SkyArena getPlannedArena() {
        return nextArena;
    }

    @Override
    public boolean isQueueFull() {
        return currentlyQueued.size() >= nextArena.getNumPlayers();
    }

    @Override
    public boolean areMinPlayersPresent() {
        return currentlyQueued.size() >= nextArena.getMinPlayers();
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameQueue implements SkyGameQueue {

    private final SkyWarsPlugin plugin;
    /**
     * Primary storage for what players are in what queues.
     */
    private final Map<String, List<UUID>> queues;
    /**
     * Primary storage for what players are in what secondary queues.
     */
    private final Map<String, List<UUID>> secondaryQueues;
    /**
     * Quick access for what queue a player is in (always updated to be in sync with queues).
     */
    private final Map<UUID, String> playerQueues = new HashMap<>();
    /**
     * Quick access for what secondary queue a player is in (always updated to be in sync with queues).
     */
    private final Map<UUID, String> playerSecondaryQueues = new HashMap<>();
    /**
     * List of next arenas
     */
    private final Map<String, SkyArena> nextArenas;
    private final Map<String, Integer> nextArenaOrderNumbers;

    private final List<String> loadedQueueNames;

    public GameQueue(SkyWarsPlugin plugin) {
        this.plugin = plugin;

        Set<String> queueNames = plugin.getConfiguration().getQueueNames();

        this.loadedQueueNames = new ArrayList<>(queueNames.size());
        this.queues = new HashMap<>(queueNames.size());
        this.secondaryQueues = new HashMap<>(queueNames.size());
        this.nextArenas = new HashMap<>(queueNames.size());
        this.nextArenaOrderNumbers = new HashMap<>(queueNames.size());
        for (String queueName : queueNames) {
            SkyStatic.debug("[GameQueue] Loading queue %s", queueName);
            loadedQueueNames.add(queueName);
            nextArenaOrderNumbers.put(queueName, 0);
            prepareNextArena(queueName);
        }
    }

    @Override
    public boolean isQueueNameValid(String queueName) {
        return queues.containsKey(queueName);
    }

    @Override
    public boolean inQueue(UUID uuid) {
        return playerQueues.containsKey(uuid);
    }

    @Override
    public String getPlayerQueue(final UUID playerUuid) {
        return playerQueues.get(playerUuid);
    }

    @Override
    public boolean inSecondaryQueue(final UUID uuid) {
        return playerSecondaryQueues.containsKey(uuid);
    }

    @Override
    public String getPlayerSecondaryQueue(final UUID playerUuid) {
        return playerSecondaryQueues.get(playerUuid);
    }

    @Override
    public boolean queuePlayer(Player player, String queueName) {
        if (!nextArenas.containsKey(queueName)) {
            throw new IllegalArgumentException("Invalid queue: " + queueName);
        }
        UUID uuid = player.getUniqueId();
        if (playerQueues.containsKey(uuid)) {
            if (Objects.equals(queueName, playerQueues.get(uuid))) {
                return true;
            } else {
                removePlayer(player);
            }
        } else if (playerSecondaryQueues.containsKey(uuid)) {
            if (Objects.equals(queueName, playerSecondaryQueues.get(uuid))) {
                return false;
            } else {
                removePlayer(player);
            }
        }
        if (isQueueFull(queueName)) {
            secondaryQueues.get(queueName).add(uuid);
            playerSecondaryQueues.put(uuid, queueName);
            plugin.getDistributor().distribute(new PlayerJoinSecondaryQueueInfo(player, queueName));
            return false;
        } else {
            queues.get(queueName).add(uuid);
            playerQueues.put(uuid, queueName);
            plugin.getDistributor().distribute(new PlayerJoinQueueInfo(player, queueName, isQueueFull(queueName), areMinPlayersPresent(queueName)));
            return true;
        }
    }

    @Override
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerSecondaryQueues.containsKey(uuid)) {
            String queueName = playerSecondaryQueues.get(uuid);
            secondaryQueues.get(queueName).remove(uuid);
            playerSecondaryQueues.remove(uuid);
            plugin.getDistributor().distribute(new PlayerLeaveSecondaryQueueInfo(player, queueName));
        } else if (playerQueues.containsKey(uuid)) {
            String queueName = playerQueues.get(uuid);
            queues.get(queueName).remove(uuid);
            playerQueues.remove(uuid);
            plugin.getDistributor().distribute(new PlayerLeaveQueueInfo(player, queueName, areMinPlayersPresent(queueName)));
            List<UUID> secondaryQueue = secondaryQueues.get(queueName);
            if (!secondaryQueue.isEmpty()) {
                UUID nextUuid = secondaryQueue.remove(0);
                playerSecondaryQueues.remove(nextUuid);
                Player nextPlayer = Bukkit.getPlayer(nextUuid);
                plugin.getDistributor().distribute(new PlayerLeaveSecondaryQueueInfo(nextPlayer, queueName));
                queuePlayer(nextPlayer, queueName);
            }
        }
    }

    public ArenaGame getNextGame(String queueName) {
        List<UUID> queue = queues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        SkyArena nextArena = getPlannedArena(queueName);
        if (queue.size() < nextArena.getMinPlayers()) {
            throw new IllegalStateException("Queue size smaller than minimum player count (" + queue.size() + " < " + nextArena.getMinPlayers() + ")");
        }
        Collections.shuffle(queue);
        UUID[] queueCopy = queue.toArray(new UUID[queue.size()]);
        for (UUID uuid : queue) {
            playerQueues.remove(uuid);
        }
        int id = plugin.getIDHandler().getNextId();
        ArenaGame game = new ArenaGame(nextArena, id, queueCopy);
        prepareNextArena(queueName);
        return game;
    }

    private void prepareNextArena(final String queueName) {
        SkyConfiguration config = plugin.getConfiguration();
        List<? extends SkyArena> enabledArenas = config.getArenasForQueue(queueName);
        Validate.notEmpty(enabledArenas, "[GameQueue] Invalid queue: no arenas specified!");
        int nextArenaOrderedNumber = nextArenaOrderNumbers.get(queueName);
        SkyArena nextArena;
        switch (config.getArenaOrder()) {
            case ORDERED:
                if (nextArenaOrderedNumber >= enabledArenas.size()) {
                    nextArenaOrderedNumber = 0;
                }
                nextArena = enabledArenas.get(nextArenaOrderedNumber++);
                SkyStatic.debug("[GameQueue] Chose random arena %s", nextArena.getArenaName());
                break;
            case RANDOM:
                nextArena = Randomation.getRandom(enabledArenas);
                SkyStatic.debug("[GameQueue] Chose ordered arena %s", nextArena.getArenaName());
                break;
            default:
                plugin.getLogger().log(Level.WARNING, "[GameQueue] Invalid ArenaOrder found in config: {0}!", config.getArenaOrder());
                throw new IllegalStateException("Invalid ArenaOrder found in config");
        }

        queues.put(queueName, new ArrayList<UUID>(nextArena.getNumPlayers()));
        nextArenaOrderNumbers.put(queueName, nextArenaOrderedNumber);
        nextArenas.put(queueName, nextArena);

        final List<UUID> joinNext = secondaryQueues.get(queueName);
        secondaryQueues.put(queueName, new ArrayList<UUID>());
        if (joinNext != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (UUID uuid : joinNext) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION));
                            queuePlayer(p, queueName);
                        }
                    }
                }
            }.runTask(plugin);
        }
    }

    @Override
    public UUID[] getCopy(String queueName) {
        List<UUID> queue = queues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        return queue.toArray(new UUID[queue.size()]);
    }

    public UUID[] getSecondaryCopy(String queueName) {
        List<UUID> secondaryQueue = secondaryQueues.get(queueName);
        Validate.notNull(secondaryQueue, "Invalid queue name.");
        return secondaryQueue.toArray(new UUID[secondaryQueue.size()]);
    }

    @Override
    public Collection<UUID> getInQueue(String queueName) {
        List<UUID> queue = queues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        return Collections.unmodifiableCollection(queue);
    }

    @Override
    public Collection<UUID> getInSecondaryQueue(String queueName) {
        List<UUID> queue = secondaryQueues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        return Collections.unmodifiableCollection(queue);
    }

    @Override
    public Collection<String> getQueueNames() {
        return Collections.unmodifiableCollection(loadedQueueNames);
    }

    @Override
    public int getNumPlayersInQueue(String queueName) {
        List<UUID> queue = queues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        return queue.size();
    }

    @Override
    public SkyArena getPlannedArena(String queueName) {
        SkyArena arena = nextArenas.get(queueName);
        Validate.notNull(arena, "Invalid queue name.");
        return arena;
    }

    @Override
    public boolean isQueueFull(String queueName) {
        List<UUID> queue = queues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        return queue.size() >= getPlannedArena(queueName).getNumPlayers();
    }

    @Override
    public boolean areMinPlayersPresent(String queueName) {
        List<UUID> queue = queues.get(queueName);
        Validate.notNull(queue, "Invalid queue name.");
        return queue.size() >= getPlannedArena(queueName).getMinPlayers();
    }
}

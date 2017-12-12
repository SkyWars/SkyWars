/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import org.bukkit.Bukkit;

public class GameQueueTimer {

    private final SkyWarsPlugin plugin;
    private final Map<String, GenericTimer> startTimers;

    public GameQueueTimer(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.startTimers = new HashMap<>();

        Set<String> queueNames = plugin.getConfiguration().getQueueNames();
        for (String queueName : queueNames) {
            List<Long> timesToMessage = plugin.getConfiguration().getStartTimerMessageTimes();
            List<GenericTimer.TaskDefinition> tasks = new ArrayList<>(timesToMessage.size() + 2);
            tasks.add(new GenericTimer.TaskDefinition(0, new StartGameRunnable(queueName)));
            tasks.add(new GenericTimer.TaskDefinition(plugin.getConfiguration().getTimeBeforeGameStartToCopyArena(),
                    new ArenaCopyRunnable(queueName)));
            for (Long timeTillStart : timesToMessage) {
                tasks.add(new GenericTimer.TaskDefinition(timeTillStart, new MessageRunnable(timeTillStart, queueName)));
            }
            this.startTimers.put(queueName, new GenericTimer(plugin, tasks, false));
        }
    }

    private class StartGameRunnable implements Runnable {

        private final String queueName;

        private StartGameRunnable(final String queueName) {
            this.queueName = queueName;
        }

        @Override
        public void run() {
            if (plugin.getGameQueue().areMinPlayersPresent(queueName)) {
                plugin.getGameHandler().startNewGame(queueName);
            }
        }
    }

    private class ArenaCopyRunnable implements Runnable {

        private final String queueName;

        private ArenaCopyRunnable(final String queueName) {
            this.queueName = queueName;
        }

        @Override
        public void run() {
            startArenaCopy(queueName);
        }
    }

    /**
     * Gets a timer from startTimers and throws an IllegalArgumentException if it does not exist.
     */
    private GenericTimer getTimer(String queueName) {
        GenericTimer timer = startTimers.get(queueName);
        if (timer == null) {
            throw new IllegalArgumentException("timer not yet built for queue " + queueName + "! This is unexpected!");
        }
        return timer;
    }

    public void onJoinQueue(PlayerJoinQueueInfo info) {
        if (info.isQueueFull()) {
            getTimer(info.getQueueName()).startIn(plugin.getConfiguration().getTimeTillStartAfterMaxPlayers());
            if (plugin.getConfiguration().getTimeTillStartAfterMaxPlayers() < plugin.getConfiguration().getTimeBeforeGameStartToCopyArena()) {
                // Already passed this time, start copying immediately.
                startArenaCopy(info.getQueueName());
            }
        } else if (info.areMinPlayersPresent()) {
            GenericTimer startTimer = getTimer(info.getQueueName());
            if (!startTimer.isRunning()) {
                startTimer.startIn(plugin.getConfiguration().getTimeTillStartAfterMinPlayers());
                if (plugin.getConfiguration().getTimeTillStartAfterMinPlayers() < plugin.getConfiguration().getTimeBeforeGameStartToCopyArena()) {
                    startArenaCopy(info.getQueueName());
                }
            }
        }
    }

    public void onLeaveQueue(PlayerLeaveQueueInfo info) {
        GenericTimer startTimer = getTimer(info.getQueueName());
        if (info.areMinPlayersPresent()) {
            startTimer.startIn(plugin.getConfiguration().getTimeTillStartAfterMinPlayers());
        } else {
            startTimer.cancelAll();
            // TODO: Maybe broadcast a "game canceled" message here?
        }
    }

    public void onGameStart(GameStartInfo info) {
        getTimer(info.getQueueName()).cancelAll(); // in case of force start
    }

    private void startArenaCopy(String queueName) {
        SkyStatic.debug("[Timer] Starting arena copy for %s.", plugin.getGameQueue().getPlannedArena(queueName).getArenaName());
        plugin.getWorldHandler().startCopyingArena(plugin.getGameQueue().getPlannedArena(queueName),
                plugin.getConfiguration().getTimeBeforeGameStartToCopyArena());
    }

    private class MessageRunnable implements Runnable {

        private final boolean displayInMinutes;
        private final long displayTime;
        private final String queueName;

        private MessageRunnable(long timeTillStart, final String queueName) {
            this.queueName = queueName;
            if (timeTillStart % 60 == 0) {
                displayInMinutes = true;
                displayTime = timeTillStart / 60;
            } else {
                displayInMinutes = false;
                displayTime = timeTillStart;
            }
        }

        @Override
        public void run() {
            if (!plugin.getGameQueue().areMinPlayersPresent(queueName)) {
                SkyStatic.debug("[Timer] Canceling timer as min players are not present.");
                getTimer(queueName).cancelAll();
                return;
            }
            TransKey transKey;
            if (displayInMinutes) {
                transKey = TransKey.GAME_TIMER_STARTING_IN_MINUTES;
            } else {
                transKey = TransKey.GAME_TIMER_STARTING_IN_SECONDS;
            }
            String message = SkyTrans.get(transKey, displayTime);
            if (plugin.getConfiguration().shouldLimitStartTimerMessagesToArenaPlayers()) {
                for (UUID uuid : plugin.getGameQueue().getInQueue(queueName)) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
                for (UUID uuid : plugin.getGameQueue().getInSecondaryQueue(queueName)) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
                Bukkit.getConsoleSender().sendMessage(message);
            } else {
                Bukkit.broadcastMessage(message);
            }
        }
    }
}

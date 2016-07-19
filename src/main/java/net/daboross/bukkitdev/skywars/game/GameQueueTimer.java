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
import java.util.List;
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
    private final GenericTimer startTimer;

    public GameQueueTimer(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
        List<Long> timesToMessage = plugin.getConfiguration().getStartTimerMessageTimes();
        List<GenericTimer.TaskDefinition> tasks = new ArrayList<>(timesToMessage.size() + 2);
        tasks.add(new GenericTimer.TaskDefinition(0, new Runnable() {
            @Override
            public void run() {
                plugin.getGameHandler().startNewGame();
            }
        }));
        tasks.add(new GenericTimer.TaskDefinition(plugin.getConfiguration().getTimeBeforeGameStartToCopyArena(), new Runnable() {
            @Override
            public void run() {
                startArenaCopy();
            }
        }));
        for (Long timeTillStart : timesToMessage) {
            tasks.add(new GenericTimer.TaskDefinition(timeTillStart, new MessageRunnable(timeTillStart)));
        }
        this.startTimer = new GenericTimer(plugin, tasks, false);
    }

    public void onJoinQueue(PlayerJoinQueueInfo info) {
        if (info.isQueueFull()) {
            startTimer.startIn(plugin.getConfiguration().getTimeTillStartAfterMaxPlayers());
            if (plugin.getConfiguration().getTimeTillStartAfterMaxPlayers() < plugin.getConfiguration().getTimeBeforeGameStartToCopyArena()) {
                // Already passed this time, start copying immediately.
                startArenaCopy();
            }
        } else if (info.areMinPlayersPresent() && !startTimer.isRunning()) {
            startTimer.startIn(plugin.getConfiguration().getTimeTillStartAfterMinPlayers());
            if (plugin.getConfiguration().getTimeTillStartAfterMinPlayers() < plugin.getConfiguration().getTimeBeforeGameStartToCopyArena()) {
                startArenaCopy();
            }
        }
    }

    public void onLeaveQueue(PlayerLeaveQueueInfo info) {
        if (info.areMinPlayersPresent()) {
            startTimer.startIn(plugin.getConfiguration().getTimeTillStartAfterMinPlayers());
        } else {
            startTimer.cancelAll();
            // TODO: Maybe broadcast a "game canceled" message here?
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void onGameStart(GameStartInfo info) {
        startTimer.cancelAll(); // in case of force start
    }

    private void startArenaCopy() {
        SkyStatic.debug("[Timer] Starting arena copy for %s.", plugin.getGameQueue().getPlannedArena().getArenaName());
        plugin.getWorldHandler().startCopyingArena(plugin.getGameQueue().getPlannedArena(),
                plugin.getConfiguration().getTimeBeforeGameStartToCopyArena());
    }

    private class MessageRunnable implements Runnable {

        private final boolean displayInMinutes;
        private final long displayTime;

        private MessageRunnable(long timeTillStart) {
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
            TransKey transKey;
            if (displayInMinutes) {
                transKey = TransKey.GAME_TIMER_STARTING_IN_MINUTES;
            } else {
                transKey = TransKey.GAME_TIMER_STARTING_IN_SECONDS;
            }
            String message = SkyTrans.get(transKey, displayTime);
            if (plugin.getConfiguration().shouldLimitStartTimerMessagesToArenaPlayers()) {
                for (UUID uuid : plugin.getGameQueue().getInQueue()) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
                for (UUID uuid : plugin.getGameQueue().getInSecondaryQueue()) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
                Bukkit.getConsoleSender().sendMessage(message);
            } else {
                Bukkit.broadcastMessage(message);
            }
        }
    }
}

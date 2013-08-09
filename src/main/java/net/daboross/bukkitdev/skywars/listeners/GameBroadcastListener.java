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
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairPlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author daboross
 */
public class GameBroadcastListener implements Listener {

    private final SkyWarsPlugin plugin;

    public GameBroadcastListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGameStart(PrepairGameStartEvent evt) {
        Player[] players = evt.getPlayers();
        StringBuilder playerNames = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i == 4) {
                playerNames.append(ColorList.BROADCAST).append(" and ").append(ColorList.NAME).append(players[i].getName());
            } else if (i > 0) {
                playerNames.append(ColorList.BROADCAST).append(", ").append(ColorList.NAME).append(players[i].getName());
            } else {
                playerNames.append(ColorList.NAME).append(players[i].getName());
            }
        }
        Bukkit.broadcastMessage(String.format(Messages.GAME_STARTING, playerNames.toString()));
    }

    @EventHandler
    public void onGameEnd(PrepairGameEndEvent evt) {
        if (evt.shouldBroadcast()) {
            String winner = null;
            for (Player player : evt.getPlayers()) {
                if (player != null) {
                    Bukkit.getPluginManager().callEvent(new PrepairPlayerLeaveGameEvent(evt.getId(), player));
                    if (winner == null) {
                        winner = player.getName();
                    } else {
                        winner += ", " + player.getName();
                    }
                }
            }
            final String message;
            if (winner == null) {
                message = Messages.NONE_WON;
            } else if (winner.contains(", ")) {
                message = String.format(Messages.MULTI_WON, winner);
            } else {
                message = String.format(Messages.SINGLE_WON, winner);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage(message);
                }
            }.runTask(plugin);
        }
    }
}

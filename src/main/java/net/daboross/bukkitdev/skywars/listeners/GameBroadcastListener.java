/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.events.GameEndEvent;
import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameEvent;
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
    public void onGameStart(GameStartEvent evt) {
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
    public void onGameEnd(GameEndEvent evt) {
        if (evt.shouldBroadcast()) {
            String winner = null;
            for (Player player : evt.getPlayers()) {
                if (player != null) {
                    Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(evt.getId(), player));
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

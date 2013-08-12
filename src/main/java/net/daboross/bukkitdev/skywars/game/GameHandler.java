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

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyCurrentGameTracker;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.events.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.events.PrepairPlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class GameHandler implements SkyGameHandler {

    private final SkyWarsPlugin plugin;

    public GameHandler(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startNewGame() {
        String[] queued = plugin.getGameQueue().clearAndGetQueue();
        if (queued.length != 4) {
            throw new IllegalStateException("Queue size is not 4");
        }
        PrepairGameStartEvent evt = new PrepairGameStartEvent(queued);
        plugin.getServer().getPluginManager().callEvent(evt);
    }

    @Override
    public void endGame(int id, boolean broadcast) {
        SkyIDHandler idHandler = plugin.getIDHandler();
        if (!idHandler.gameRunning(id)) {
            throw new IllegalArgumentException("Invalid id " + id);
        }
        PrepairGameEndEvent evt = new PrepairGameEndEvent(plugin.getIDHandler().getPlayers(id), id, broadcast);
        Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
        for (Player player : evt.getPlayers()) {
            if (player != null) {
                plugin.getServer().getPluginManager().callEvent(new PrepairPlayerLeaveGameEvent(id, player));
                player.teleport(lobby);
            }
        }
        plugin.getServer().getPluginManager().callEvent(evt);
    }

    @Override
    public void removePlayerFromGame(String playerName, boolean teleport, boolean broadcast) {
        playerName = playerName.toLowerCase();
        SkyCurrentGameTracker cg = plugin.getCurrentGameTracker();
        int id = cg.getGameID(playerName);
        if (id == -1) {
            throw new IllegalArgumentException("Player not in game");
        }
        GameIDHandler idh = plugin.getIDHandler();
        String[] players = idh.getPlayers(id);
        int playersLeft = 0;
        for (int i = 0; i < 4; i++) {
            if (players[i] != null) {
                if (players[i].equalsIgnoreCase(playerName)) {
                    players[i] = null;
                } else {
                    playersLeft++;
                }
            }
        }
        Player player = Bukkit.getPlayerExact(playerName);
        plugin.getServer().getPluginManager().callEvent(new PrepairPlayerLeaveGameEvent(id, player));
        if (teleport) {
            player.teleport(plugin.getLocationStore().getLobbyPosition().toLocation());
        }
        if (broadcast) {
            Bukkit.broadcastMessage(KillBroadcaster.getMessage(player.getName(), plugin.getAttackerStorage().getKiller(playerName), KillBroadcaster.KillReason.LEFT));
        }
        if (playersLeft < 2) {
            endGame(id, true);
        }
    }
}

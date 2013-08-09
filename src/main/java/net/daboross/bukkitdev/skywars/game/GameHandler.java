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

import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairPlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 *
 * @author daboross
 */
public class GameHandler {

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

    public void endGame(int id, boolean broadcast) {
        GameIdHandler idh = plugin.getIdHandler();
        String[] names = idh.getPlayers(id);
        PrepairGameEndEvent evt = new PrepairGameEndEvent(names, id, broadcast);
        plugin.getServer().getPluginManager().callEvent(evt);
        Player[] players = evt.getPlayers();
        Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
        for (Player player : players) {
            if (player != null) {
                plugin.getServer().getPluginManager().callEvent(new PrepairPlayerLeaveGameEvent(id, player));
                player.teleport(lobby);
            }
        }
    }

    public void removePlayerFromGame(String playerName, boolean teleport, boolean broadcast) {
        playerName = playerName.toLowerCase();
        CurrentGames cg = plugin.getCurrentGames();
        Integer id = cg.getGameID(playerName);
        if (id == null) {
            return;
        }
        GameIdHandler idh = plugin.getIdHandler();
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
        if (teleport || broadcast) {
            if (teleport) {
                Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
                player.teleport(lobby);
            }
            if (broadcast) {
                EntityDamageEvent ede = player.getLastDamageCause();
                Entity damager = null;
                if (ede instanceof EntityDamageByEntityEvent) {
                    damager = ((EntityDamageByEntityEvent) ede).getDamager();
                }
                if (damager == null) {
                    Bukkit.broadcastMessage(String.format(Messages.FORFEITED, player.getName()));
                } else {
                    String damagerName = (damager instanceof LivingEntity) ? ((LivingEntity) damager).getCustomName() : damager.getType().getName();
                    Bukkit.broadcastMessage(String.format(Messages.FORFEITED_BY, damagerName, playerName));
                }
            }
        }
        if (playersLeft < 2) {
            endGame(id, true);
        }
    }
}

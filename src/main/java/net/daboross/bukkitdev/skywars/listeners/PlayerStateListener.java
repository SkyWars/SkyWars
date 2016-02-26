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

import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerStateListener implements Listener {

    private final SkyWars plugin;

    public PlayerStateListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        plugin.getPlayers().loadPlayer(evt.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        Player player = evt.getPlayer();
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(player);
        switch (skyPlayer.getState()) {
            case IN_QUEUE:
                plugin.getGameQueue().removePlayer(player);
                break;
            case IN_RUNNING_GAME:
                plugin.getGameHandler().removePlayerFromGame(player, true, true);
                break;
            case WAITING_FOR_RESPAWN:
                plugin.getGameHandler().respawnPlayer(player);
                break;
            default:
                break;
        }
        plugin.getPlayers().unloadPlayer(player.getUniqueId());
    }
}

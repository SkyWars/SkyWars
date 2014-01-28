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
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private final SkyWarsPlugin plugin;

    public QuitListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        SkyPlayer skyPlayer = plugin.getInGame().getPlayer(evt.getPlayer());
        if (skyPlayer != null) {
            switch (skyPlayer.getState()) {
                case IN_QUEUE:
                    plugin.getGameQueue().removePlayer(evt.getPlayer());
                    break;
                case IN_RUNNING_GAME:
                    plugin.getGameHandler().removePlayerFromGame(evt.getPlayer(), true, true);
                    break;
                case WAITING_FOR_RESPAWN:
                    plugin.getGameHandler().respawnPlayer(evt.getPlayer());
                    break;
            }
        }
    }
}

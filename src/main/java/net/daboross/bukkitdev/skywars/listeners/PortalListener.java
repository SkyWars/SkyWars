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

import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class PortalListener implements Listener {

    private final SkyWarsPlugin plugin;

    public PortalListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Location location = evt.getTo();
        for (SkyBlockLocation loc : plugin.getLocationStore().getPortals()) {
            if (loc.isNear(location)) {
                Player p = evt.getPlayer();
                String name = p.getName().toLowerCase();
                if (!plugin.getCurrentGameTracker().isInGame(name) && !plugin.getGameQueue().inQueue(name)) {
                    p.sendMessage(Messages.Join.CONFIRMATION);
                    plugin.getGameQueue().queuePlayer(name);
                }
            }
        }
    }
}

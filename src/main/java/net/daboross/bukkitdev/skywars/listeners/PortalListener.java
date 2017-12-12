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

import java.util.UUID;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyGameQueue;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyPortalData;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PortalListener implements Listener {

    private final SkyWarsPlugin plugin;

    public PortalListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Location location = evt.getTo();
        for (SkyPortalData data : plugin.getLocationStore().getPortals()) {
            if (data.location.isNear(location)) {
                Player p = evt.getPlayer();
                UUID uuid = p.getUniqueId();
                SkyGameQueue gameQueue = plugin.getGameQueue();
                if (!plugin.getCurrentGameTracker().isInGame(uuid)
                        && !gameQueue.inQueue(uuid) && !gameQueue.inSecondaryQueue(uuid)) {
                    if (gameQueue.isQueueFull(data.queueName)) {
                        p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_JOINED_SECONDARY_QUEUE));
                        p.sendMessage(SkyTrans.get(TransKey.SECONDARY_QUEUE_EXPLANATION));
                    } else {
                        p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION));
                    }
                    plugin.getGameQueue().queuePlayer(p, data.queueName);
                }
            }
        }
    }
}

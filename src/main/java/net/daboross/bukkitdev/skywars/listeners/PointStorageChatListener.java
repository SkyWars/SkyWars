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

import lombok.RequiredArgsConstructor;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.points.SkyPoints;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class PointStorageChatListener implements Listener {

    private static final String REPLACEMENT = "(?i)\\{skywars\\.userpoints\\}";
    private final SkyWars plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat( AsyncPlayerChatEvent evt ) {
        SkyPoints points = plugin.getPoints();
        if ( points != null ) {
            evt.setFormat( evt.getFormat().replaceAll( REPLACEMENT, String.valueOf( points.getScore( evt.getPlayer().getName() ) ) ) );
        }
    }
}

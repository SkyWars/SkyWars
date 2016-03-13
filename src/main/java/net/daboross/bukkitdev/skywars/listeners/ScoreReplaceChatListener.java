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

import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ScoreReplaceChatListener implements Listener {

    private static final String SCORE_TRIGGER = "(?i)\\{SKYWARS\\.USER(SCORE|POINTS)\\}";
    private static final String RANK_TRIGGER = "(?i)\\{SKYWARS\\.USERRANK\\}";
    private final SkyWars plugin;

    public ScoreReplaceChatListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent evt) {
        SkyPlayers players = plugin.getPlayers();
        if (players.storageEnabled()) {
            evt.setFormat(evt.getFormat().replaceAll(SCORE_TRIGGER, String.valueOf(players.getPlayer(evt.getPlayer()).getScore())));
            evt.setFormat(evt.getFormat().replaceAll(RANK_TRIGGER, String.valueOf(players.getPlayer(evt.getPlayer()).getRank())));
        }
    }
}

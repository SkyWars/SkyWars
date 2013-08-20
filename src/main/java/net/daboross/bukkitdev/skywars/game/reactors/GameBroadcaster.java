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
package net.daboross.bukkitdev.skywars.game.reactors;

import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class GameBroadcaster {

    public void broadcastStart(GameStartInfo info) {
        Player[] players = info.getPlayers();
        StringBuilder playerNames = new StringBuilder();
        for (int i = 0; i < players.length; i++) {
            if (i == players.length - 1) {
                playerNames.append(ColorList.BROADCAST).append(" and ").append(ColorList.NAME).append(players[i].getName());
            } else if (i > 0) {
                playerNames.append(ColorList.BROADCAST).append(", ").append(ColorList.NAME).append(players[i].getName());
            } else {
                playerNames.append(ColorList.NAME).append(players[i].getName());
            }
        }
        Bukkit.broadcastMessage(String.format(Messages.GAME_STARTING, playerNames.toString()));
    }

    public void broadcastEnd(GameEndInfo info) {
        if (info.shouldBroadcast()) {
            final String message;
            List<Player> winners = info.getAlivePlayers();
            if (winners.isEmpty()) {
                message = Messages.NONE_WON;
            } else if (winners.size() == 1) {
                message = String.format(Messages.SINGLE_WON, winners.get(0).getName());
            } else {
                StringBuilder winnerBuilder = new StringBuilder(winners.get(0).getName());
                for (int i = 0; i < winners.size(); i++) {
                    if (i == winners.size() - 1) {
                        winnerBuilder.append(" and ");
                    } else {
                        winnerBuilder.append(", ");
                    }
                    winnerBuilder.append(winners.get(i).getName());
                }
                message = String.format(Messages.MULTI_WON, winnerBuilder);
            }
            Bukkit.broadcastMessage(message);
        }
    }
}

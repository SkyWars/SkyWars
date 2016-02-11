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
package net.daboross.bukkitdev.skywars.events.listeners;

import java.util.List;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameBroadcaster {

    public void broadcastStart(GameStartInfo info) {
        List<Player> players = info.getPlayers();
        StringBuilder playerNames = new StringBuilder(players.get(0).getName());
        for (int i = 1; i < players.size(); i++) {
            if (i == players.size() - 1) {
                playerNames.append(SkyTrans.get(TransKey.GAME_STARTING_GAMESTARTING_FINAL_COMMA));
            } else {
                playerNames.append(SkyTrans.get(TransKey.GAME_STARTING_GAMESTARTING_COMMA));
            }
            playerNames.append(players.get(i).getName());
        }
        Bukkit.broadcastMessage(SkyTrans.get(TransKey.GAME_STARTING_GAMESTARTING, playerNames));
    }

    public void broadcastEnd(GameEndInfo info) {
        if (info.isBroadcast()) {
            final String message;
            List<Player> winners = info.getAlivePlayers();
            if (winners.isEmpty()) {
                message = SkyTrans.get(TransKey.GAME_WINNING_NONE_WON);
            } else if (winners.size() == 1) {
                message = SkyTrans.get(TransKey.GAME_WINNING_SINGLE_WON, winners.get(0).getName());
            } else {
                StringBuilder winnerBuilder = new StringBuilder(winners.get(0).getName());
                for (int i = 1; i < winners.size(); i++) {
                    if (i == winners.size() - 1) {
                        winnerBuilder.append(SkyTrans.get(TransKey.GAME_WINNING_MULTI_WON_FINAL_COMMA));
                    } else {
                        winnerBuilder.append(SkyTrans.get(TransKey.GAME_WINNING_MULTI_WON_COMMA));
                    }
                    winnerBuilder.append(winners.get(i).getName());
                }
                message = SkyTrans.get(TransKey.GAME_WINNING_MULTI_WON, winnerBuilder);
            }
            Bukkit.broadcastMessage(message);
        }
    }
}

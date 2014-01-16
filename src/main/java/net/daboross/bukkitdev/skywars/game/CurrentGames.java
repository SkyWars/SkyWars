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
package net.daboross.bukkitdev.skywars.game;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.daboross.bukkitdev.skywars.api.game.SkyCurrentGameTracker;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;

public class CurrentGames implements SkyCurrentGameTracker {

    private final Map<String, Integer> currentlyInGame = new HashMap<>();

    private void setGameID(String player, int gameID) {
        currentlyInGame.put(player.toLowerCase(Locale.ENGLISH), gameID);
    }

    @Override
    public boolean isInGame(String player) {
        return currentlyInGame.containsKey(player.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public int getGameID(String player) {
        Integer val = currentlyInGame.get(player.toLowerCase(Locale.ENGLISH));
        return val == null ? -1 : val.intValue();
    }

    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        currentlyInGame.remove(info.getPlayer().getName().toLowerCase(Locale.ENGLISH));
    }

    public void onGameStart(GameStartInfo info) {
        SkyGame game = info.getGame();
        int id = game.getId();
        for (String name : game.getAlivePlayers()) {
            setGameID(name, id);
        }
    }
}

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
import java.util.Map;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.game.SkyCurrentGameTracker;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;

public class CurrentGames implements SkyCurrentGameTracker {

    private final Map<UUID, Integer> currentlyInGame = new HashMap<>();

    private void setGameId(UUID uuid, int gameId) {
        currentlyInGame.put(uuid, gameId);
    }

    @Override
    public boolean isInGame(UUID uuid) {
        return currentlyInGame.containsKey(uuid);
    }

    @Override
    public int getGameId(UUID uuid) {
        Integer val = currentlyInGame.get(uuid);
        return (val == null) ? -1 : val;
    }

    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        currentlyInGame.remove(info.getPlayer().getUniqueId());
    }

    public void onGameStart(GameStartInfo info) {
        SkyGame game = info.getGame();
        int id = game.getId();
        for (UUID uuid : game.getAlivePlayers()) {
            setGameId(uuid, id);
        }
    }
}

/*
 * Copyright (C) 2014 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.player;

import java.util.HashMap;
import java.util.Map;
import net.daboross.bukkitdev.skywars.api.ingame.SkyInGame;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayerState;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import org.bukkit.entity.Player;

public class CurrentlyInGame implements SkyInGame {

    private final Map<String, PlayerInfo> map;

    public CurrentlyInGame() {
        map = new HashMap<>();
    }

    public void onJoinQueue(PlayerJoinQueueInfo info) {
        PlayerInfo player = new PlayerInfo(info.getPlayer());
        player.setState(SkyPlayerState.IN_QUEUE);
        map.put(info.getPlayer().getName().toLowerCase(), player);
    }

    public void onLeaveQueue(PlayerLeaveQueueInfo info) {
        map.remove(info.getPlayer().getName().toLowerCase());
    }

    public void onGameStart(GameStartInfo info) {
        for (Player player : info.getPlayers()) {
            PlayerInfo playerInfo = getPlayer(player);
            playerInfo.setState(SkyPlayerState.IN_RUNNING_GAME);
        }
    }

    public void onLeaveGame(PlayerLeaveGameInfo info) {
        map.remove(info.getPlayer().getName().toLowerCase());
    }

    @Override
    public PlayerInfo getPlayer(Player player) {
        return map.get(player.getName().toLowerCase());
    }

    @Override
    public PlayerInfo getPlayer(String name) {
        return map.get(name.toLowerCase());
    }

    @Override
    public boolean isInGame(String name) {
        return map.containsKey(name.toLowerCase());
    }
}

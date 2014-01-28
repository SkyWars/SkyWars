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
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayerState;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import org.bukkit.entity.Player;

public class CurrentlyInGame implements SkyInGame {

    private final Map<String, PlayerInfo> map;

    public CurrentlyInGame() {
        map = new HashMap<>();
    }

    public void onJoinQueue(PlayerJoinQueueInfo info) {
        PlayerInfo player = getPlayer(info.getPlayer());
        if (player == null) {
            player = new PlayerInfo(info.getPlayer());
            map.put(player.getName(), player);
        }
        player.setState(SkyPlayerState.IN_QUEUE);
    }

    public void onLeaveQueue(PlayerLeaveQueueInfo info) {
        PlayerInfo player = getPlayer(info.getPlayer());
        player.setState(SkyPlayerState.NOT_IN_GAME);
        player.setGameId(-1);
    }

    public void onGameStart(GameStartInfo info) {
        for (Player player : info.getPlayers()) {
            PlayerInfo playerInfo = getPlayer(player);
            playerInfo.setState(SkyPlayerState.IN_RUNNING_GAME);
            playerInfo.setGameId(info.getGame().getId());
        }
    }

    public void onLeaveGame(PlayerLeaveGameInfo info) {
        PlayerInfo playerInfo = getPlayer(info.getPlayer());
        playerInfo.setState(SkyPlayerState.WAITING_FOR_RESPAWN);
    }

    public void onRespawn(PlayerRespawnAfterGameEndInfo info) {
        PlayerInfo playerInfo = getPlayer(info.getPlayer());
        playerInfo.setState(SkyPlayerState.NOT_IN_GAME);
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
    public SkyPlayer getPlayerForce(final Player player) {
        PlayerInfo info = getPlayer(player);
        if (info == null) {
            info = new PlayerInfo(player);
            map.put(info.getName(), info);
        }
        return info;
    }

}

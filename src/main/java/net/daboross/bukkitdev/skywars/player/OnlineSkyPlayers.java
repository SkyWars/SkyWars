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
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.game.LeaveGameReason;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayerState;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayers;
import net.daboross.bukkitdev.skywars.api.storage.SkyInternalPlayer;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import org.bukkit.entity.Player;

public class OnlineSkyPlayers implements SkyPlayers {

    private final Map<UUID, SkyInternalPlayer> map = new HashMap<>();
    private final SkyWars plugin;
    private final boolean storageEnabled;

    public OnlineSkyPlayers(final SkyWars plugin) {
        this.plugin = plugin;
        this.storageEnabled = plugin.getConfiguration().isEnableScore();
    }

    public void onJoinQueue(PlayerJoinQueueInfo info) {
        SkyInternalPlayer skyPlayer = getPlayer(info.getPlayer());
        skyPlayer.setState(SkyPlayerState.IN_QUEUE);
    }

    public void onLeaveQueue(PlayerLeaveQueueInfo info) {
        SkyInternalPlayer skyPlayer = getPlayer(info.getPlayer());
        skyPlayer.setState(SkyPlayerState.NOT_IN_GAME);
        skyPlayer.setGameId(-1);
    }

    public void onGameStart(GameStartInfo info) {
        for (Player player : info.getPlayers()) {
            SkyInternalPlayer skyPlayer = getPlayer(player);
            skyPlayer.setState(SkyPlayerState.IN_RUNNING_GAME);
            skyPlayer.setGameId(info.getGame().getId());
        }
    }

    public void onLeaveGame(PlayerLeaveGameInfo info) {
        SkyInternalPlayer skyPlayer = getPlayer(info.getPlayer());
        if (info.getReason() == LeaveGameReason.DIED) {
            skyPlayer.setState(SkyPlayerState.DEAD_WAITING_FOR_RESPAWN);
        } else {
            skyPlayer.setState(SkyPlayerState.WAITING_FOR_RESPAWN);
        }
    }

    public void onRespawn(PlayerRespawnAfterGameEndInfo info) {
        SkyInternalPlayer skyPlayer = getPlayer(info.getPlayer());
        skyPlayer.setState(SkyPlayerState.NOT_IN_GAME);
    }

    @Override
    public boolean storageEnabled() {
        return storageEnabled;
    }

    @Override
    public SkyInternalPlayer getPlayer(Player player) {
        return map.get(player.getUniqueId());
    }

    @Override
    public SkyInternalPlayer getPlayer(UUID uuid) {
        return map.get(uuid);
    }

    @Override
    public void loadPlayer(Player player) {
        SkyInternalPlayer skyPlayer;
        if (storageEnabled) {
            skyPlayer = plugin.getScore().loadPlayer(player);
        } else {
            skyPlayer = new NoStorageSkyPlayer(player);
        }
        map.put(player.getUniqueId(), skyPlayer);
    }

    @Override
    public void unloadPlayer(UUID uuid) {
        SkyInternalPlayer skyPlayer = map.get(uuid);
        skyPlayer.loggedOut();
        map.remove(uuid);
    }

    private class NoStorageSkyPlayer extends AbstractSkyPlayer {

        public NoStorageSkyPlayer(final Player player) {
            super(player);
        }

        @Override
        public void loggedOut() {

        }

        @Override
        public int getScore() {
            throw new UnsupportedOperationException("Score storage is not enabled.");
        }

        @Override
        public void setScore(final int score) {
            throw new UnsupportedOperationException("Score storage is not enabled.");
        }

        @Override
        public void addScore(final int diff) {
            throw new UnsupportedOperationException("Score storage is not enabled.");
        }

        @Override
        public int getRank() {
            throw new UnsupportedOperationException("Score storage is not enabled.");
        }
    }
}

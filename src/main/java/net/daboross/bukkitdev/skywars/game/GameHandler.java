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
package net.daboross.bukkitdev.skywars.game;

import java.util.Locale;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyCurrentGameTracker;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.PlayerRespawnAfterGameEndInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 */
public class GameHandler implements SkyGameHandler {

    private final SkyWarsPlugin plugin;

    public GameHandler(@NonNull SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startNewGame() {
        plugin.getDistributor().distribute(new GameStartInfo(plugin.getGameQueue().getNextGame()));
    }

    @Override
    public void endGame(int id, boolean broadcast) {
        SkyIDHandler idHandler = plugin.getIDHandler();
        if (!idHandler.gameRunning(id)) {
            throw new IllegalArgumentException("Invalid id " + id);
        }
        GameEndInfo info = new GameEndInfo(plugin.getIDHandler().getGame(id), broadcast);
        for (Player player : info.getAlivePlayers()) {
            plugin.getDistributor().distribute(new PlayerLeaveGameInfo(id, player));
            respawnPlayer(player);
        }
        plugin.getDistributor().distribute(info);
    }

    @Override
    public void removePlayerFromGame(@NonNull String playerName, boolean respawn, boolean broadcast) {
        Player p = plugin.getServer().getPlayerExact(playerName);
        if (p == null) {
            throw new IllegalArgumentException("Player " + playerName + " isn't online");
        }
        this.removePlayerFromGame(p, respawn, broadcast);
    }

    @Override
    public void removePlayerFromGame(@NonNull Player player, boolean respawn, boolean broadcast) {
        String playerName = player.getName().toLowerCase(Locale.ENGLISH);
        SkyCurrentGameTracker cg = plugin.getCurrentGameTracker();
        final int id = cg.getGameID(playerName);
        if (id == -1) {
            throw new IllegalArgumentException("Player not in game");
        }
        GameIDHandler idh = plugin.getIDHandler();
        ArenaGame game = idh.getGame(id);
        game.removePlayer(playerName);
        plugin.getDistributor().distribute(new PlayerLeaveGameInfo(id, player));
        if (respawn) {
            respawnPlayer(player);
        }
        if (broadcast) {
            Bukkit.broadcastMessage(KillBroadcaster.getMessage(player.getName(), plugin.getAttackerStorage().getKiller(playerName), KillBroadcaster.KillReason.LEFT, game.getArena()));
        }
        if (game.getAlivePlayers().size() < 2) {
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    endGame(id, true);
                }
            });
        }
    }

    @Override
    public void respawnPlayer(@NonNull String playerName) {
        Player p = plugin.getServer().getPlayerExact(playerName);
        if (p == null) {
            throw new IllegalArgumentException("Player " + playerName + " isn't online");
        }
        this.respawnPlayer(p);
    }

    @Override
    public void respawnPlayer(@NonNull Player p) {
        p.teleport(plugin.getLocationStore().getLobbyPosition().toLocation());
        plugin.getDistributor().distribute(new PlayerRespawnAfterGameEndInfo(p));
    }
}

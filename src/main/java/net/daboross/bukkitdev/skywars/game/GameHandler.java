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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyCurrentGameTracker;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameHandler implements SkyGameHandler {

    private final Set<Integer> gamesCurrentlyEnding = new HashSet<>();
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
        ArenaGame game = plugin.getIDHandler().getGame(id);
        GameEndInfo gameEndInfo = new GameEndInfo(game, broadcast);
        for (Player player : gameEndInfo.getAlivePlayers()) {
            plugin.getDistributor().distribute(new PlayerLeaveGameInfo(id, player));
            respawnPlayer(player);
        }
        // All PlayerLeaveGameInfos MUST be distributed before the GameEndInfo is
        plugin.getDistributor().distribute(gameEndInfo);
        gamesCurrentlyEnding.remove(id);
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
            Bukkit.broadcastMessage(KillMessages.getMessage(player.getName(), plugin.getAttackerStorage().getKiller(playerName), KillMessages.KillReason.LEFT, game.getArena()));
        }
        if ((!gamesCurrentlyEnding.contains(id)) && isGameWon(game)) {
            gamesCurrentlyEnding.add(id);
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    endGame(id, true);
                }
            });
        }
    }

    private boolean isGameWon(SkyGame game) {
        List<String> alivePlayers = game.getAlivePlayers();
        int size = alivePlayers.size();
        if (size < 2) {
            return true;
        } else if (!game.areTeamsEnabled()
                || size > game.getArena().getTeamSize()) {
            return false;
        } else {
            int knownTeam = -1;
            for (String player : alivePlayers) {
                int thisTeam = game.getTeamNumber(player);
                if (knownTeam == -1) {
                    knownTeam = thisTeam;
                } else if (thisTeam != knownTeam) {
                    return false;
                }
            }
            return true;
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

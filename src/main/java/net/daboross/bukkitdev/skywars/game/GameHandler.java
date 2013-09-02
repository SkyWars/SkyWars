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

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyCurrentGameTracker;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class GameHandler implements SkyGameHandler {

    private final SkyWarsPlugin plugin;

    public GameHandler(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

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
        Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
        for (Player player : info.getAlivePlayers()) {
            plugin.getDistributor().distribute(new PlayerLeaveGameInfo(id, player));
            player.teleport(lobby);
        }
        plugin.getDistributor().distribute(info);
    }

    @Override
    public void removePlayerFromGame(String playerName, boolean teleport, boolean broadcast) {
        playerName = playerName.toLowerCase();
        SkyCurrentGameTracker cg = plugin.getCurrentGameTracker();
        int id = cg.getGameID(playerName);
        if (id == -1) {
            throw new IllegalArgumentException("Player not in game");
        }
        GameIDHandler idh = plugin.getIDHandler();
        ArenaGame game = idh.getGame(id);
        game.removePlayer(playerName);
        Player player = Bukkit.getPlayerExact(playerName);
        plugin.getDistributor().distribute(new PlayerLeaveGameInfo(id, player));
        if (teleport) {
            player.teleport(plugin.getLocationStore().getLobbyPosition().toLocation());
        }
        if (broadcast) {
            Bukkit.broadcastMessage(KillBroadcaster.getMessage(player.getName(), plugin.getAttackerStorage().getKiller(playerName), KillBroadcaster.KillReason.LEFT, game.getArena()));
        }
        if (game.getAlivePlayers().size() < 2) {
            endGame(id, true);
        }
    }
}

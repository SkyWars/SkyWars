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
package net.daboross.bukkitdev.skywars.scoreboards;

import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamScoreboardListener {

    public void onGameStart(GameStartInfo info) {
        SkyGame game = info.getGame();
        if (game.areTeamsEnabled()) {
            Server server = Bukkit.getServer();
            Scoreboard board = server.getScoreboardManager().getNewScoreboard();
            for (int teamNum = 0, max = game.getNumTeams(); teamNum < max; teamNum++) {
                String teamName = "Team " + teamNum;
                Team team = board.registerNewTeam(teamName);
                team.setAllowFriendlyFire(false);
                team.setCanSeeFriendlyInvisibles(true);
                team.setPrefix(ChatColor.GRAY + "[" + ChatColor.DARK_RED + teamNum + ChatColor.GRAY + "]");
                for (String name : game.getAllPlayersInTeam(teamNum)) {
                    team.addPlayer(server.getPlayerExact(name));
                }
            }
        }
    }

    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        // TODO: Teams shouldn't last forever
    }
}

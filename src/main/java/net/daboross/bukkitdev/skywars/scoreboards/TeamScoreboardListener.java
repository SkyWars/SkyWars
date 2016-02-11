/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import static net.daboross.bukkitdev.skywars.api.game.SkyGame.SkyGameTeam;

public class TeamScoreboardListener {

    private final Map<UUID, Team> teams = new HashMap<>();
    private final Map<Integer, Scoreboard> scoreboards = new HashMap<>();

    public void onGameStart(GameStartInfo info) {
        SkyGame game = info.getGame();
        if (game.areTeamsEnabled()) {
            SkyStatic.debug("Setting up teams for game %s", game.getId());
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            scoreboards.put(game.getId(), board);
            for (int teamId = 0, max = game.getNumTeams(); teamId < max; teamId++) {
                SkyGameTeam gameTeam = game.getTeam(teamId);
                String teamName = gameTeam.getName();
                Team team = board.registerNewTeam(teamName);
                team.setAllowFriendlyFire(false);
                team.setCanSeeFriendlyInvisibles(true);
                team.setPrefix(ChatColor.GRAY + "[" + ChatColor.DARK_RED + teamName + ChatColor.GRAY + "]" + ChatColor.WHITE + " ");

                for (UUID uuid : gameTeam.getAlive()) {
                    SkyStatic.debug("Adding (uuid: %s) to scoreboard team %s", uuid, teamName);
                    Player player = Bukkit.getPlayer(uuid);
                    team.addPlayer(player);
                    teams.put(uuid, team);
                    player.setScoreboard(board);
                }
            }
        }
    }

    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        Team team = teams.remove(info.getPlayer().getUniqueId());
        if (team != null) {
            team.removePlayer(info.getPlayer());
        }
    }

    public void onGameEnd(GameEndInfo info) {
        Scoreboard scoreboard = scoreboards.remove(info.getGame().getId());
        if (scoreboard != null) {
            for (Team team : scoreboard.getTeams()) {
                team.unregister();
            }
        }
    }
}

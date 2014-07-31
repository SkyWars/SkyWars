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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import org.apache.commons.lang.Validate;

public class ArenaGame implements SkyGame {

    private final int id;
    private final List<UUID> alivePlayers;
    private final List<UUID> deadPlayers;
    private final SkyArena arena;
    private SkyBlockLocation min;
    private SkyBlockLocationRange boundaries;
    private final boolean teamsEnabled;
    private final Map<UUID, Integer> playerTeams;
    private final Team[] teams;
    private final int numTeams;

    public ArenaGame(SkyArena arena, int id, UUID[] originalPlayers) {
        Validate.notNull(arena, "Arena cannot be null");
        Validate.noNullElements(originalPlayers, "No players can be null");
        this.arena = arena;
        this.id = id;
        this.alivePlayers = new ArrayList<>(Arrays.asList(originalPlayers));
        this.deadPlayers = new ArrayList<>(originalPlayers.length);
        int maxTeamNumber = arena.getNumTeams();
        if (arena.getTeamSize() > 1) { // if teams are enabled (there is more than one person per team)
            numTeams = maxTeamNumber > alivePlayers.size() ? alivePlayers.size() : maxTeamNumber;

            this.teamsEnabled = true;
            this.playerTeams = new HashMap<>(alivePlayers.size());
            this.teams = new Team[numTeams];
            for (int i = 0; i < numTeams; i++) {
                teams[i] = new Team(i, String.valueOf(i + 1));
            }
            int nextTeam = 0;
            for (UUID uuid : alivePlayers) {
                playerTeams.put(uuid, nextTeam);
                this.teams[nextTeam].addPlayer(uuid);
                nextTeam += 1;
                if (nextTeam >= numTeams) {
                    nextTeam = 0;
                }
            }
        } else {
            playerTeams = null;
            teams = null;
            teamsEnabled = false;
            numTeams = -1;
        }
    }

    public void removePlayer(UUID uuid) {
        Validate.isTrue(alivePlayers.remove(uuid), "Player (uuid: %s) not alive in game", uuid);
        deadPlayers.add(uuid);
    }

    public void setMin(SkyBlockLocation min) {
        this.min = min;
        this.boundaries = arena.getBoundaries().getBuilding().add(min);
    }

    @Override
    public SkyBlockLocation getMin() {
        return min;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<UUID> getAlivePlayers() {
        return Collections.unmodifiableList(alivePlayers);
    }

    @Override
    public List<UUID> getDeadPlayers() {
        return Collections.unmodifiableList(deadPlayers);
    }

    @Override
    public SkyArena getArena() {
        return arena;
    }

    @Override
    public SkyBlockLocationRange getBuildingBoundaries() {
        return boundaries;
    }

    @Override
    public boolean areTeamsEnabled() {
        return teamsEnabled;
    }

    @Override
    public int getTeamNumber(UUID uuid) {
        if (!teamsEnabled) {
            throw new IllegalStateException("Teams aren't enabled");
        }
        Integer team = playerTeams.get(uuid);
        Validate.isTrue(team != null, "Player (uuid: %s) not in game", uuid);
        return team;
    }

    @Override
    public List<UUID> getAlivePlayersInTeam(int teamId) {
        if (!teamsEnabled) {
            throw new IllegalStateException("Teams aren't enabled");
        }
        if (teamId < 0 || teamId >= numTeams) {
            throw new IllegalArgumentException("Invalid team id");
        }
        return teams[teamId].getAlive();
    }

    @Override
    public List<UUID> getAllPlayersInTeam(int teamId) {
        if (!teamsEnabled) {
            throw new IllegalStateException("Teams aren't enabled");
        }
        if (teamId < 0 || teamId >= numTeams) {
            throw new IllegalArgumentException("Invalid team id");
        }
        return teams[teamId].getPlayers();
    }

    @Override
    public SkyGameTeam getTeam(final int teamId) {
        if (!teamsEnabled) {
            throw new IllegalStateException("Teams aren't enabled");
        }
        if (teamId < 0 || teamId >= numTeams) {
            throw new IllegalArgumentException("Invalid team id");
        }
        return teams[teamId];
    }

    @Override
    public int getNumTeams() {
        return numTeams;
    }

    public class Team implements SkyGameTeam {

        private final List<UUID> players;
        private final int id;
        private final String name;

        public Team(int id, String name) {
            this.players = new ArrayList<>();
            this.id = id;
            this.name = name;
        }

        private void addPlayer(UUID uuid) {
            players.add(uuid);
        }

        @Override
        public List<UUID> getAlive() {
            List<UUID> alive = new ArrayList<>(arena.getTeamSize());
            for (UUID uuid : players) {
                if (alivePlayers.contains(uuid)) {
                    alive.add(uuid);
                }
            }
            return alive;
        }

        @Override
        public List<UUID> getPlayers() {
            return Collections.unmodifiableList(players);
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}

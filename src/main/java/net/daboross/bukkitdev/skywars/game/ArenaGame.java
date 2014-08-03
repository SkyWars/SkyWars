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
    private final Map<Integer, List<UUID>> teamPlayers;
    private final int numTeams;

    public ArenaGame(SkyArena arena, int id, UUID[] originalPlayers) {
        Validate.notNull(arena, "Arena cannot be null");
        Validate.noNullElements(originalPlayers, "No players can be null");
        this.arena = arena;
        this.id = id;
        this.alivePlayers = new ArrayList<>(Arrays.asList(originalPlayers));
        this.deadPlayers = new ArrayList<>(originalPlayers.length);
        int teamSize = arena.getTeamSize();
        if (teamSize > 1) {
            teamsEnabled = true;
            this.playerTeams = new HashMap<>(alivePlayers.size());
            this.teamPlayers = new HashMap<>(alivePlayers.size() / teamSize);
            int team = 0;
            List<UUID> currentTeamList = null;
            for (int i = 0, lastTeam = -1; i < alivePlayers.size(); i++) {
                team = i / teamSize;
                if (team != lastTeam) {
                    currentTeamList = new ArrayList<>(teamSize);
                    teamPlayers.put(team, currentTeamList);
                    lastTeam = team;
                }
                UUID uuid = alivePlayers.get(i);
                playerTeams.put(uuid, team);
                // This won't produce an NPE, because team!=lastTeam at the start of this loop.
                //noinspection ConstantConditions
                currentTeamList.add(uuid);
            }
            numTeams = team + 1;
        } else {
            playerTeams = null;
            teamPlayers = null;
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
        Validate.notNull(team, String.format("Player (uuid: %s) not in game", uuid));
        return team;
    }

    @Override
    public List<UUID> getAlivePlayersInTeam(int teamNumber) {
        if (!teamsEnabled) {
            throw new IllegalStateException("Teams aren't enabled");
        }
        List<UUID> alive = new ArrayList<>(arena.getTeamSize());
        List<UUID> all = teamPlayers.get(teamNumber);
        if (all == null) {
            return null;
        }
        for (UUID uuid : all) {
            if (alivePlayers.contains(uuid)) {
                alive.add(uuid);
            }
        }
        return Collections.unmodifiableList(alive);
    }

    @Override
    public List<UUID> getAllPlayersInTeam(int teamNumber) {
        if (!teamsEnabled) {
            throw new IllegalStateException("Teams aren't enabled");
        }
        List<UUID> alive = teamPlayers.get(teamNumber);
        return alive == null ? null : Collections.unmodifiableList(alive);
    }

    @Override
    public int getNumTeams() {
        return numTeams;
    }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class ArenaGame implements SkyGame {

    private final int id;
    private final List<String> alivePlayers;
    private final List<String> deadPlayers;
    private final SkyArena arena;
    private SkyBlockLocation min;
    private SkyBlockLocationRange boundaries;

    public ArenaGame( @NonNull SkyArena arena, int id, @NonNull String[] originalPlayers ) {
        this.arena = arena;
        this.id = id;
        this.alivePlayers = new ArrayList<>( Arrays.asList( originalPlayers ) );
        this.deadPlayers = new ArrayList<>( originalPlayers.length );
    }

    public void removePlayer( String playerName ) {
        playerName = playerName.toLowerCase( Locale.ENGLISH );
        if ( !alivePlayers.remove( playerName ) ) {
            throw new IllegalArgumentException( "Player not in game." );
        }
        deadPlayers.add( playerName );
    }

    public void setMin( SkyBlockLocation min ) {
        this.min = min;
        this.boundaries = arena.getBoundaries().getBuilding().add( min );
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
    public List<String> getAlivePlayers() {
        return Collections.unmodifiableList( alivePlayers );
    }

    @Override
    public List<String> getDeadPlayers() {
        return Collections.unmodifiableList( deadPlayers );
    }

    @Override
    public SkyArena getArena() {
        return arena;
    }

    @Override
    public SkyBlockLocationRange getBuildingBoundaries() {
        return boundaries;
    }
}

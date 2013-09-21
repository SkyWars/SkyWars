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
package net.daboross.bukkitdev.skywars.points;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.StartupFailedException;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.points.SkyPoints;
import net.daboross.bukkitdev.skywars.api.points.PointStorageBackend;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.PlayerDeathInArenaInfo;
import net.daboross.bukkitdev.skywars.events.PlayerKillPlayerInfo;
import org.bukkit.entity.Player;

public class PointStorageListener extends SkyPoints {

    private final SkyWarsPlugin plugin;
    private final PointStorageBackend backend;

    @SuppressWarnings("UseSpecificCatch")
    public PointStorageListener( SkyWarsPlugin plugin ) {
        this.plugin = plugin;
        Class<? extends PointStorageBackend> backendClass = getBackend();
        if ( backendClass == null ) {
            backendClass = PointStorageJSONBackend.class;
        }
        try {
            Constructor<? extends PointStorageBackend> constructor = backendClass.getConstructor( SkyWars.class );
            this.backend = constructor.newInstance( plugin );
        } catch ( Exception ex ) {
            throw new StartupFailedException( "Unable to initialize storage backend", ex );
        }
    }

    public void onKill( PlayerKillPlayerInfo info ) {
        SkyConfiguration config = plugin.getConfiguration();
        addScore( info.getKillerName(), config.getKillPointDiff() );
    }

    public void onDeath( PlayerDeathInArenaInfo info ) {
        SkyConfiguration config = plugin.getConfiguration();
        addScore( info.getKilled().getName(), config.getDeathPointDiff() );
    }

    public void onGameEnd( GameEndInfo info ) {
        SkyConfiguration config = plugin.getConfiguration();
        List<Player> alive = info.getAlivePlayers();
        if ( alive.size() == 1 ) {
            addScore( alive.get( 0 ).getName(), config.getWinPointDiff() );
        }
    }

    @Override
    public void addScore( String name, int diff ) {
        backend.addScore( name, diff );
    }

    @Override
    public int getScore( String name ) {
        return backend.getScore( name );
    }

    public void save() throws IOException {
        backend.save();
    }
}

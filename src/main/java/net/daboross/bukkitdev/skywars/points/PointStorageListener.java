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
import java.util.List;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.points.PointStorageBackend;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.PlayerKillPlayerInfo;
import org.bukkit.entity.Player;

public class PointStorageListener {

    private final SkyWarsPlugin plugin;
    private final PointStorageBackend backend;

    public PointStorageListener( SkyWarsPlugin plugin ) throws IOException {
        this.plugin = plugin;
        this.backend = new PointStorageJSONBackend( plugin );
        // TODO Make backend configurable
    }

    public void onKill( PlayerKillPlayerInfo info ) {
        SkyConfiguration config = plugin.getConfiguration();
        addScoreOnline( info.getKilled(), config.getDeathPointDiff() );
        addScorePossiblyOnline( info.getKillerName(), config.getKillPointDiff() );
    }

    public void onGameEnd( GameEndInfo info ) {
        SkyConfiguration config = plugin.getConfiguration();
        List<Player> alive = info.getAlivePlayers();
        if ( alive.size() == 1 ) {
            addScoreOnline( alive.get( 0 ), config.getWinPointDiff() );
        }
    }

    public void addScoreOffline( String name, int diff ) {
        backend.addScore( name, diff );
    }

    public void addScorePossiblyOnline( String name, int diff ) {
        Player p = plugin.getServer().getPlayerExact( name );
        addScoreOffline( name, diff );
        if ( p != null ) {
            updateScore( p );
        }
    }

    public void addScoreOnline( Player p, int diff ) {
        addScoreOffline( p.getName(), diff );
        updateScore( p );
    }

    private void updateScore( Player p ) {
        if ( plugin.getConfiguration().isPrefixChat() ) {
            p.setDisplayName( String.format( plugin.getConfiguration().getChatPrefix(), backend.getScore( p.getName() ) ) + p.getName() );
        }
    }
}

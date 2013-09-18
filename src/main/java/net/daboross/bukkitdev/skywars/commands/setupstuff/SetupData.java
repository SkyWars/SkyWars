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
package net.daboross.bukkitdev.skywars.commands.setupstuff;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyBoundaries;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyPlacementConfig;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;

/**
 *
 */
@Data
public class SetupData {

    private String arenaName;
    private File saveFile;
    private SkyBlockLocation originPos1;
    private SkyBlockLocation originPos2;
    private SkyBlockLocation originMin;
    private SkyBlockLocation originMax;
    private List<SkyPlayerLocation> spawns = new ArrayList<>();

    public void setOriginPos1( SkyBlockLocation originPos1 ) {
        this.originPos1 = originPos1;
        setOriginMin();
        setOriginMax();
    }

    public void setOriginPos2( SkyBlockLocation originPos2 ) {
        this.originPos2 = originPos2;
        setOriginMin();
        setOriginMax();
    }

    public void setOriginMin() {
        if ( originPos1 != null && originPos2 != null ) {
            originMin = SkyBlockLocation.min( originPos1, originPos2 );
        }
    }

    public void setOriginMax() {
        if ( originPos1 != null && originPos2 != null ) {
            originMax = SkyBlockLocation.max( originPos1, originPos2 );
        }
    }

    public SkyArenaConfig convertToArenaConfig() {
        SkyArenaConfig config = new SkyArenaConfig();
        config.setArenaName( arenaName );
        config.setFile( saveFile );
        SkyBlockLocationRange origin = new SkyBlockLocationRange( originMin, originMax, originMin.world );
        SkyBlockLocationRange clearing = calculateClearing( origin );
        SkyBlockLocationRange building = calculateBuilding( clearing );
        SkyBoundaries boundaries = config.getBoundaries();
        boundaries.setOrigin( origin );
        boundaries.setClearing( clearing );
        boundaries.setBuilding( building );
        SkyPlacementConfig placement = config.getPlacement();
        placement.setPlacementY( 20 );
        placement.setDistanceApart( 200 );
        return config;
    }

    public static SkyBlockLocationRange calculateClearing( SkyBlockLocationRange origin ) {
        SkyBlockLocation min = new SkyBlockLocation( -1, -20, -1, null );
        SkyBlockLocation max = new SkyBlockLocation( origin.max.x - origin.min.x + 1,
                origin.max.y - origin.min.y + 1,
                origin.max.z - origin.min.z + 1, null );
        return new SkyBlockLocationRange( min, max, null );
    }

    public static SkyBlockLocationRange calculateBuilding( SkyBlockLocationRange clearing ) {
        SkyBlockLocation min = clearing.min.add( -1, 0, -1 );
        SkyBlockLocation max = clearing.max.add( 1, 1, 1 );
        return new SkyBlockLocationRange( min, max, null );
    }
}

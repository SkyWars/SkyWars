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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;

/**
 *
 */
@Data
public class SetupData {

    private SkyBlockLocation originPos1;
    private SkyBlockLocation originPos2;
    private SkyBlockLocation originMin;
    private List<SkyBlockLocation> spawns = new ArrayList<>();

    public void setOriginPos1( SkyBlockLocation originPos1 ) {
        this.originPos1 = originPos1;
        calculateMin();
    }

    public void setOriginPos2( SkyBlockLocation originPos2 ) {
        this.originPos2 = originPos2;
        calculateMin();
    }

    public void calculateMin() {
        if ( originPos1 != null && originPos2 != null ) {
            originMin = SkyBlockLocation.min( originPos1, originPos2 );
        }
    }
}

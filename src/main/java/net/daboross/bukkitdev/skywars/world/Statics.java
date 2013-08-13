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
package net.daboross.bukkitdev.skywars.world;

import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;

/**
 *
 * @author daboross
 */
public class Statics {

    public static final String BASE_WORLD_NAME = "SkyWarsBaseWorld";
    public static final String ARENA_WORLD_NAME = "SkyWarsArenaWorld";
    public static final SkyPlayerLocation[] RELATIVE_SPAWNS = {
        new SkyPlayerLocation(18.5, 6, -14.5, ARENA_WORLD_NAME),
        new SkyPlayerLocation(-17.5, 6, -14.5, ARENA_WORLD_NAME),
        new SkyPlayerLocation(-17.5, 6, 15.5, ARENA_WORLD_NAME),
        new SkyPlayerLocation(18.5, 6, 15.5, ARENA_WORLD_NAME)
    };
    public static final SkyBlockLocation ARENA_MIN = new SkyBlockLocation(-28, 97, -29, BASE_WORLD_NAME);
    public static final SkyBlockLocation ARENA_MAX = new SkyBlockLocation(28, 104, 29, BASE_WORLD_NAME);
}

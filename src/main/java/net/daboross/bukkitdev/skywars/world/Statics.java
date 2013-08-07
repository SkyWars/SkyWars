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

import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import net.daboross.bukkitdev.skywars.storage.SkyLocationAccurate;

/**
 *
 * @author daboross
 */
public class Statics {

    public static final String BASE_WORLD_NAME = "SkyWarsBaseWorld";
    public static final String ARENA_WORLD_NAME = "SkyWarsArenaWorld";
    public static final SkyLocationAccurate[] RELATIVE_SPAWNS = {
        new SkyLocationAccurate(18.5, 6, -14.5, ARENA_WORLD_NAME),
        new SkyLocationAccurate(-17.5, 6, -14.5, ARENA_WORLD_NAME),
        new SkyLocationAccurate(-17.5, 6, 15.5, ARENA_WORLD_NAME),
        new SkyLocationAccurate(18.5, 6, 15.5, ARENA_WORLD_NAME)
    };
//    public static final SkyLocation ARENA_MIN = new SkyLocation(-205, 183, 78, BASE_WORLD_NAME);
//    public static final SkyLocation ARENA_MAX = new SkyLocation(-149, 190, 136, BASE_WORLD_NAME);
    public static final SkyLocation ARENA_MIN = new SkyLocation(0, 0, 0, BASE_WORLD_NAME);
    public static final SkyLocation ARENA_MAX = new SkyLocation(56, 200, 68, BASE_WORLD_NAME);
}

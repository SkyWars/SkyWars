/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
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
    public static final SkyLocation ARENA_MIN = new SkyLocation(-142, 183, 142, BASE_WORLD_NAME);
    public static final SkyLocation ARENA_MAX = new SkyLocation(-142, 190, 142, BASE_WORLD_NAME);
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;

/**
 *
 * @author daboross
 */
public class Messages {

    public static final String PREFIX = String.format(ColorList.BROADCAST_NAME_FORMAT, "SkyWars");
    public static final String KILLED_VOID = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has pushed " + ColorList.NAME + "%s" + ColorList.BROADCAST + " into the void of doom!";
    public static final String SUICIDE_VOID = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " jumped into the void of doom!";
    public static final String KILLED = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has killed " + ColorList.NAME + "%s" + ColorList.BROADCAST + " with a " + ColorList.DATA + "%s!";
    public static final String SUICIDE = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " was killed!";
    public static final String FORFEITED_BY = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " was forced for forfeit by " + ColorList.NAME + "%s";
    public static final String FORFEITED = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " forfeited!";
}

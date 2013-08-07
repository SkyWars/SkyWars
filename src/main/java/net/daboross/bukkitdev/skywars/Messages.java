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
    public static final String KILLED = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has killed " + ColorList.NAME + "%s" + ColorList.BROADCAST + "!";
    public static final String SUICIDE = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " was killed!";
    public static final String FORFEITED_BY = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " was forced for forfeit by " + ColorList.NAME + "%s";
    public static final String FORFEITED = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " forfeited!";
    public static final String SINGLE_WON = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has won the SkyWars!";
    public static final String MULTI_WON = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " have won the SkyWars!";
    public static final String NONE_WON = PREFIX + ColorList.NAME + "No one won the SkyWars!";
    public static final String GAME_STARTING = PREFIX + "Game starting with " + ColorList.NAME + "%s" + ColorList.BROADCAST + "!";

    public static class Join {

        public static final String CONFIRMATION = ColorList.REG + "You have joined the queue.";
        public static final String ALREADY_QUEUED = ColorList.ERR + "You were already in the queue.";
        public static final String IN_GAME = ColorList.REG + "You can't join now, you are already in a game.";
    }

    public static class Lobby {

        public static final String IN_GAME = ColorList.REG + "You can't teleport to the lobby, you are in a game.";
        public static final String CONFIRMATION = ColorList.REG + "Teleporting to lobby";
    }
}

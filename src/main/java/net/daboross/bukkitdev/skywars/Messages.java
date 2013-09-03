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

//    public static final String PREFIX = String.format(ColorList.BROADCAST_NAME_FORMAT, "SkyWars");
//    /**
//     * Message broadcasted when someone is killed in the void, and there has
//     * been someone who hit them since the game started.
//     * <br>
//     * First %s is the name of the last person who attacked, second %s is the
//     * person who died.
//     */
//    public static final String KILLED_VOID = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has pushed " + ColorList.NAME + "%s" + ColorList.BROADCAST + " into the void of doom!";
//    /**
//     * Message broadcasted when someone is killed in the void, and there hasn't
//     * been anyone who hit them since the game started.
//     * <br>
//     * First %s is the name of the person who died.
//     */
//    public static final String SUICIDE_VOID = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " jumped into the void of doom!";
//    /**
//     * Message broadcasted when someone is killed by someone else hitting them.
//     * <br>
//     * First %s is the name of the attacker, second %s is the name of the person
//     * who died.
//     */
//    public static final String KILLED = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has killed " + ColorList.NAME + "%s" + ColorList.BROADCAST + "!";
//    /**
//     * Message broadcasted when someone is killed, and no one has hit them since
//     * the game started.
//     * <br>
//     * First %s is the name of the person who died.
//     */
//    public static final String SUICIDE = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " was killed!";
//    /**
//     * Message broadcasted when someone leaves the game or server, and someone
//     * has attacked them since the game started.
//     * <br>
//     * First %s is the name of the person who left, second %s is the name of the
//     * last person who hit the given person.
//     */
//    public static final String FORFEITED_BY = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " was forced for forfeit by " + ColorList.NAME + "%s";
//    /**
//     * Message broadcasted when someone leaves the game or server, and no one
//     * has attacked them since the game began.
//     * <br>
//     * First %s is the name of the person who left.
//     */
//    public static final String FORFEITED = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " forfeited!";
//    /**
//     * Message broadcasted when one person wins the game.
//     * <br>
//     * First %s is the name of the person who has won.
//     */
//    public static final String SINGLE_WON = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " has won the SkyWars!";
//    /**
//     * Message broadcasted when two or more people win the game.
//     * <br>
//     * First %s is a list of people who won.
//     */
//    public static final String MULTI_WON = PREFIX + ColorList.NAME + "%s" + ColorList.BROADCAST + " have won the SkyWars!";
//    /**
//     * Message broadcasted when a game ends with no people left in it. This
//     * message is normally _never_ used.
//     */
//    public static final String NONE_WON = PREFIX + ColorList.NAME + "No one won the SkyWars!";
//    /**
//     * Message broadcasted when a game starts.
//     * <br>
//     * First %s is a list of people in the game.
//     */
//    public static final String GAME_STARTING = PREFIX + "Game starting with " + ColorList.NAME + "%s" + ColorList.BROADCAST + "!";
    public static class Join {

        /**
         * Message sent to someone when he/she joins the queue.
         */
        public static final String CONFIRMATION = ColorList.REG + "You have joined the queue.";
        /**
         * Message sent when someone tries to join the queue, but they are
         * already in it.
         */
        public static final String ALREADY_QUEUED = ColorList.ERR + "You were already in the queue.";
        /**
         * Message sent when someone tries to join the queue, but they are in a
         * game.
         */
        public static final String IN_GAME = ColorList.REG + "You can't join now, you are already in a game.";
    }

    public static class Lobby {

        /**
         * Message sent when someone tries to teleport to the lobby when they
         * are in a game.
         */
        public static final String IN_GAME = ColorList.REG + "You can't teleport to the lobby, you are in a game.";
        /**
         * Message sent when someone teleports to the lobby.
         */
        public static final String CONFIRMATION = ColorList.REG + "Teleporting to lobby";
    }

    public static class Version {

        /**
         * Message displayed when someone uses the version command.
         * <br>
         * First %s is the plugin's version.
         */
        public static final String CREDITS_AND_VERSION =
                ColorList.REG + "Skyblock Warriors map created by " + ColorList.NAME + "SwipeShot\n"
                + ColorList.REG + "SkyWars plugin v" + ColorList.DATA + "%s" + ColorList.REG + " created by " + ColorList.NAME + "Dabo Ross";
    }

    public static class Leave {

        /**
         * Message displayed when someone is removed from the queue.
         */
        public static final String REMOVED_FROM_QUEUE = ColorList.REG + "You are no longer in the queue.";
        /**
         * Message displayed when someone is removed from the game.
         */
        public static final String REMOVED_FROM_GAME = ColorList.REG + "You have fled the game.";
        /**
         * Message displayed when someone tries to leave when they are not in
         * the queue or game.
         */
        public static final String NOT_IN = ColorList.ERR + "You were not in the queue.";
    }

    public static class Death {

        public static final String REMOVED_BECAUSE_DEATH = ColorList.REG + "You were removed from the queue because you died.";
    }
}

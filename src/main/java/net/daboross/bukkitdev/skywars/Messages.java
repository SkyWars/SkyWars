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

public class Messages {

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
         * First %s is the plugin's name, second %s is plugin's short version,
         * third %s is plugin's implementation version, fourth %s is API
         * version.
         */
        public static final String CREDITS_AND_VERSION
                = ColorList.REG + "%s v" + ColorList.DATA + "%s" + ColorList.REG + " created by " + ColorList.NAME + "Dabo Ross%n"
                + ColorList.REG + " Implementation version %s";
        public static final String CREDITS_AND_VERSION_WHEN_VERSION_IS_SAME = ColorList.REG + "%s v" + ColorList.DATA + "%s" + ColorList.REG + " created by " + ColorList.NAME + "Dabo Ross";
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

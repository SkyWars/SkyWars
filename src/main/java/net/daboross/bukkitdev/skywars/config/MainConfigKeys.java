/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.config;

class MainConfigKeys {

    public static final String VERSION = "config-version";
    public static final String ENABLED_ARENAS = "enabled-arenas";
    public static final String ARENA_ORDER = "arena-order";
    public static final String MESSAGE_PREFIX = "message-prefix";
    public static final String DEBUG = "debug";
    public static final String SAVE_INVENTORY = "save-inventory";
    public static final String ARENA_DISTANCE_APART = "arena-distance-apart";
    public static final String LOCALE = "locale";
//    public static final String PER_ARENA_DEATH_MESSAGES_ENABLED = "enable-per-arena-death-messages";
//    public static final String PER_ARENA_WIN_MESSAGES_ENABLED = "enable-per-arena-win-messages";

    public static class Score {

        public static final String ENABLE = "points.enable-points";
        public static final String DEATH_DIFF = "points.death-point-diff";
        public static final String WIN_DIFF = "points.win-point-diff";
        public static final String KILL_DIFF = "points.kill-point-diff";
        public static final String SAVE_INTERVAL = "points.max-save-interval";
        public static final String USE_SQL = "points.use-sql";
        public static final String SQL_HOST = "points.sql.host";
        public static final String SQL_PORT = "points.sql.port";
        public static final String SQL_DATABASE = "points.sql.database";
        public static final String SQL_USERNAME = "points.sql.username";
        public static final String SQL_PASSWORD = "points.sql.password";
    }

    public static class Economy {

        public static final String ENABLE = "economy.enable-economy";
        public static final String WIN_REWARD = "economy.win-reward";
        public static final String KILL_REWARD = "economy.kill-reward";
        public static final String MESSAGE = "economy.reward-messages";
    }

    public static class CommandWhitelist {

        public static final String WHITELIST_ENABLED = "command-whitelist.whitelist-enabled";
        public static final String IS_BLACKLIST = "command-whitelist.treated-as-blacklist";
        public static final String COMMAND_WHITELIST = "command-whitelist.whitelist";
    }

    public static class Hooks {

        public static final String MULTIVERSE_CORE = "hooks.multiverse-core-hook";
        public static final String MULTIVERSE_INVENTORIES = "hooks.multiverse-inventories-hook";
        public static final String WORLDEDIT = "hooks.worldedit-hook";
    }

    public static class Deprecated {

        public static final String PREFIX_CHAT = "points.should-prefix-chat";
        public static final String CHAT_PREFIX = "points.chat-prefix";
    }
}

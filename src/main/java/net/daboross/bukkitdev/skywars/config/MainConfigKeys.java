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

public class MainConfigKeys {

    public static final String VERSION = "config-version";
    public static final String ENABLED_ARENAS = "enabled-arenas";
    public static final String ARENA_ORDER = "arena-order";
    public static final String MESSAGE_PREFIX = "message-prefix";
    public static final String DEBUG = "debug";
    public static final String REPORT_STATISTICS = "report-statistics";
    public static final String SKIP_UUID_CHECK = "skip-uuid-version-check";
    public static final String SAVE_INVENTORY = "save-inventory";
    public static final String SAVE_EXPERIENCE = "save-experience";
    public static final String SAVE_POSITION_GAMEMODE_HEALTH = "save-position-gamemode-health";
    public static final String ARENA_DISTANCE_APART = "arena-distance-apart";
    public static final String LOCALE = "locale";
    public static final String ARENA_GAMERULES = "skywars-arena-gamerules";
    public static final String RESPAWN_PLAYERS_IMMEDIATELY = "skip-respawn-screen";
    public static final String DISABLE_REPORT = "disable-report";
    public static final String DEVELOPER_OPTIONS = "developer-options";
    public static final String DISABLE_SCORE_RECOVERY = "disable-score-recovery";
    public static final String JOIN_SIGN_LINES = "join-sign-lines";
    public static final String LIMIT_START_MESSAGES_TO_ARENA = "only-broadcast-to-players-in-arena.start";
    public static final String LIMIT_DEATH_MESSAGES_TO_ARENA = "only-broadcast-to-players-in-arena.death";
    public static final String LIMIT_END_MESSAGES_TO_ARENA = "only-broadcast-to-players-in-arena.end";
    public static final String LIMIT_START_TIMER_MESSAGES_TO_ARENA = "only-broadcast-to-players-in-arena.starting-in-start-timer";
    public static final String KIT_GUI_SHOW_UNAVAILABLE_KITS = "kit-gui.show-unavailable-kits";
    public static final String KIT_GUI_REPLACE_KIT_COMMAND = "kit-gui.replace-kit-command";
    public static final String KIT_GUI_AUTO_SHOW_ON_JOIN = "kit-gui.auto-show-on-join";
    public static final String TIME_TILL_START_AFTER_MAX_PLAYERS = "game-timer.time-till-start-after-max-join";
    public static final String TIME_TILL_START_AFTER_MIN_PLAYERS = "game-timer.time-till-start-after-any-join";
    public static final String TIME_BEFORE_GAME_STARTS_TO_COPY_ARENA = "game-timer.time-before-start-to-start-arena-copy-operation";
    public static final String IN_GAME_PLAYER_FREEZE_TIME = "game-timer.time-after-start-to-freeze-players";
    public static final String START_TIMER_MESSAGE_TIMES = "game-timer.times-to-message-before-start";
    public static final String ARENA_COPYING_BLOCK_SIZE = "arena-copying.number-of-blocks-to-copy-at-once";

    private MainConfigKeys() {
    }

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
        public static final String SQL_UPDATE_INDIVIDUALS_RANK_INTERVAL = "points.sql.individual-rank-update-interval";

        private Score() {
        }
    }

    public static class Economy {

        public static final String ENABLE = "economy.enable-economy";
        public static final String WIN_REWARD = "economy.win-reward";
        public static final String KILL_REWARD = "economy.kill-reward";
        public static final String MESSAGE = "economy.reward-messages";

        private Economy() {
        }
    }

    public static class CommandWhitelist {

        public static final String WHITELIST_ENABLED = "command-whitelist.whitelist-enabled";
        public static final String IS_BLACKLIST = "command-whitelist.treated-as-blacklist";
        public static final String COMMAND_WHITELIST = "command-whitelist.whitelist";

        private CommandWhitelist() {
        }
    }

    public static class Hooks {

        public static final String MULTIVERSE_CORE = "hooks.multiverse-core-hook";
        public static final String WORLDEDIT = "hooks.worldedit-hook";

        private Hooks() {
        }
    }

    public static class Deprecated {

        public static final String PREFIX_CHAT = "points.should-prefix-chat";
        public static final String CHAT_PREFIX = "points.chat-prefix";

        private Deprecated() {
        }
    }
}

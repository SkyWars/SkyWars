/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;

public class MainConfigDefaults {

    public static final int VERSION = 2;
    public static final String MESSAGE_PREFIX = "&8[&cSkyWars&8]&a ";
    public static final boolean DEBUG = false;
    public static final boolean REPORT_STATISTICS = true;
    public static final boolean SKIP_UUID_CHECK = false;
    public static final SkyConfiguration.ArenaOrder ARENA_ORDER = SkyConfiguration.ArenaOrder.RANDOM;
    public static final List<String> ENABLED_ARENAS = Arrays.asList("skyblock-warriors", "water-warriors");
    public static final boolean SAVE_INVENTORY = true;
    public static final int ARENA_DISTANCE_APART = 200;
    public static final String LOCALE = Locale.getDefault().getLanguage();
    public static final Map<String, String> ARENA_GAMERULES;
    public static final boolean DEVELOPER_OPTIONS = false;
    public static final boolean DISABLE_REPORT = false;
    public static final boolean DISABLE_SCORE_RECOVERY = false;
    public static final boolean RESPAWN_PLAYERS_IMMEDIATELY = false;
    public static final String[] JOIN_SIGN_LINES = new String[]{
            "&8[&cSkyWars&8]",
            "&8Next Game: &c{name}",
            "&8Players: &2{count}&3/&2{max}",
            "&cClick to join!",
    };
    public static final boolean LIMIT_START_MESSAGES_TO_ARENA = false;
    public static final boolean LIMIT_DEATH_MESSAGES_TO_ARENA = true;
    public static final boolean LIMIT_END_MESSAGES_TO_ARENA = false;
    public static final boolean LIMIT_START_TIMER_MESSAGES_TO_ARENA = false;
    public static final boolean KIT_GUI_SHOW_UNAVAILABLE_KITS = true;
    public static final boolean KIT_GUI_REPLACE_KIT_COMMAND = false;
    public static final boolean KIT_GUI_AUTO_SHOW_ON_JOIN = true;
    public static final long TIME_TILL_START_AFTER_MAX_PLAYERS = 30;
    public static final long TIME_TILL_START_AFTER_MIN_PLAYERS = 200;
    public static final long TIME_BEFORE_GAME_STARTS_TO_COPY_ARENA = 45;
    public static final long IN_GAME_PLAYER_FREEZE_TIME = 5;
    public static final List<Long> START_TIMER_MESSAGE_TIMES = Arrays.asList(600l, 300l, 180l, 60l, 45l, 30l, 15l, 5l, 3l, 2l, 1l);
    public static final int ARENA_COPYING_BLOCK_SIZE = 500;

    static {
        Map<String, String> defaultArenaGamerules = new HashMap<>(1);
        defaultArenaGamerules.put("doDaylightCycle", "false");
        ARENA_GAMERULES = Collections.unmodifiableMap(defaultArenaGamerules);
    }

    private MainConfigDefaults() {
    }

    public static class Score {

        public static final boolean ENABLE = true;
        public static final int DEATH_DIFF = -2;
        public static final int WIN_DIFF = 7;
        public static final int KILL_DIFF = 1;
        public static final long SAVE_INTERVAL = 300;
        public static final long SAVE_INTERVAL_WITH_SQL = 30;
        public static final boolean USE_SQL = false;
        public static final String SQL_HOST = "127.0.0.1";
        public static final int SQL_PORT = 3306;
        public static final String SQL_DATABASE = "minecraft";
        public static final String SQL_USERNAME = "root";
        public static final String SQL_PASSWORD = "aComplexPassword";
        public static final long SQL_UPDATE_INDIVIDUALS_RANK_INTERVAL = 60;

        private Score() {
        }
    }

    public static class Economy {

        public static final boolean ENABLE = false;
        public static final int WIN_REWARD = 10;
        public static final int KILL_REWARD = 10;
        public static final boolean MESSAGE = true;

        private Economy() {
        }
    }

    public static class CommandWhitelist {

        public static final boolean WHITELIST_ENABLED = true;
        public static final boolean IS_BLACKLIST = false;
        public static final List<String> COMMAND_WHITELIST = Arrays.asList("/skywars", "/sw", "/me");

        private CommandWhitelist() {
        }
    }

    public static class Hooks {

        public static final boolean MULTIVERSE_CORE = true;
        public static final boolean WORLDEDIT = true;
        public static final boolean MULTIINV_WORKAROUND_WHEN_FOUND = true;
        public static final boolean FORCE_MULTIINV_WORKAROUND = false;

        private Hooks() {
        }
    }
}

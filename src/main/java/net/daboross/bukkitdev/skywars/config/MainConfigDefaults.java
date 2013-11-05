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
package net.daboross.bukkitdev.skywars.config;

import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;

public class MainConfigDefaults {

    public static final int VERSION = 1;
    public static final String MESSAGE_PREFIX = "&8[&cSkyWars&8]&a ";
    public static final boolean DEBUG = false;
    public static final SkyConfiguration.ArenaOrder ARENA_ORDER = SkyConfiguration.ArenaOrder.RANDOM;
    public static final List<String> ENABLED_ARENAS = Arrays.asList("skyblock-warriors");
    public static final boolean SAVE_INVENTORY = true;
    public static final int ARENA_DISTANCE_APART = 200;
    public static final boolean PER_ARENA_DEATH_MESSAGES_ENABLED = true;
    public static final boolean PER_ARENA_WIN_MESSAGES_ENABLED = false;

    public static class Points {

        public static final boolean ENABLE = true;
        public static final int DEATH_DIFF = -2;
        public static final int WIN_DIFF = 7;
        public static final int KILL_DIFF = 1;
    }

    public static class Economy {

        public static final boolean ENABLE = false;
        public static final int WIN_REWARD = 10;
        public static final int KILL_REWARD = 10;
        public static final boolean MESSAGE = true;
    }

    public static class CommandWhitelist {

        public static final boolean WHITELIST_ENABLED = true;
        public static final boolean IS_BLACKLIST = false;
        public static final List<String> COMMAND_WHITELIST = Arrays.asList("/skywars", "/sw", "/me");
    }
}

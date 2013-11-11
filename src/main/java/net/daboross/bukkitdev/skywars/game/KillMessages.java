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
package net.daboross.bukkitdev.skywars.game;

import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.config.SkyMessageKeys;

public class KillMessages {

    public static String getMessage(String player, String damager, KillReason reason, SkyArena arena) {
        SkyStatic.debug("Getting death message for player %s killed by damager %s with reason %s", player, damager, reason);
        if (damager == null) {
            switch (reason) {
                case VOID:
                    return String.format(arena.getMessages().getMessage(SkyMessageKeys.SUICIDE_VOID), player);
                case LEFT:
                    return String.format(arena.getMessages().getMessage(SkyMessageKeys.FORFEITED), player);
                case OTHER:
                    return String.format(arena.getMessages().getMessage(SkyMessageKeys.KILLED_OTHER), player);
            }
        } else {
            switch (reason) {
                case VOID:
                    return String.format(arena.getMessages().getMessage(SkyMessageKeys.KILLED_VOID), damager, player);
                case LEFT:
                    return String.format(arena.getMessages().getMessage(SkyMessageKeys.FORFEITED_DAMAGED), damager, player);
                case OTHER:
                    return String.format(arena.getMessages().getMessage(SkyMessageKeys.KILLED_DAMAGED), damager, player);
            }
        }
        throw new IllegalArgumentException();
    }

    public enum KillReason {

        VOID, LEFT, OTHER
    }
}

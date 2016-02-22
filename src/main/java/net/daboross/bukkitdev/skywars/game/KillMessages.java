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
package net.daboross.bukkitdev.skywars.game;

import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;

public class KillMessages {

    public static String getMessage(String player, String damager, KillReason reason, SkyArena arena) {
        SkyStatic.debug("Getting death message for player %s killed by damager %s with reason %s", player, damager, reason);
        if (damager == null) {
            switch (reason) {
                case VOID:
                    return SkyTrans.get(TransKey.GAME_DEATH_KILLED_BY_VOID, player);
                case LEFT:
                    return SkyTrans.get(TransKey.GAME_DEATH_FORFEITED, player);
                case OTHER:
                    return SkyTrans.get(TransKey.GAME_DEATH_KILLED_BY_ENVIRONMENT, player);
                default:
                    break;
            }
        } else {
            switch (reason) {
                case VOID:
                    return SkyTrans.get(TransKey.GAME_DEATH_KILLED_BY_PLAYER_AND_VOID, damager, player);
                case LEFT:
                    return SkyTrans.get(TransKey.GAME_DEATH_FORFEITED_WHILE_ATTACKED, damager, player);
                case OTHER:
                    return SkyTrans.get(TransKey.GAME_DEATH_KILLED_BY_PLAYER, damager, player);
                default:
                    break;
            }
        }
        throw new IllegalArgumentException("Unknown reason");
    }

    public enum KillReason {

        VOID, LEFT, OTHER
    }
}

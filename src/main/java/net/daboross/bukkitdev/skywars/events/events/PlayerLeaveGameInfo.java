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
package net.daboross.bukkitdev.skywars.events.events;

import net.daboross.bukkitdev.skywars.api.game.LeaveGameReason;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class PlayerLeaveGameInfo {

    private final int id;
    private final Player player;
    private final LeaveGameReason reason;

    public PlayerLeaveGameInfo(final int id, final Player player, final LeaveGameReason reason) {
        this.reason = reason;
        Validate.notNull(player, "Player cannot be null");
        this.id = id;
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public LeaveGameReason getReason() {
        return reason;
    }
}

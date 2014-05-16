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

import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class PlayerKillPlayerInfo {

    private final int gameId;
    private final UUID killerUuid;
    private final String killerName;
    private final Player killed;

    public PlayerKillPlayerInfo(final int gameId, final UUID killerUuid, final String killerName, final Player killed) {
        Validate.notNull(killerName, "Killer name cannot be null");
        Validate.notNull(killerUuid, "Killer UUID cannot be null");
        Validate.notNull(killed, "Killed cannot be null");
        this.gameId = gameId;
        this.killerUuid = killerUuid;
        this.killerName = killerName;
        this.killed = killed;
    }

    public int getGameId() {
        return gameId;
    }

    public UUID getKillerUuid() {
        return killerUuid;
    }

    public String getKillerName() {
        return killerName;
    }

    public Player getKilled() {
        return killed;
    }

    @Override
    public String toString() {
        return "PlayerKillPlayerInfo{" +
                "gameId=" + gameId +
                ", killerName='" + killerName + '\'' +
                ", killed=" + killed +
                '}';
    }
}

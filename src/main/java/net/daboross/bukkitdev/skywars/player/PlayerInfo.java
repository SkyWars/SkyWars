/*
 * Copyright (C) 2014 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.player;

import lombok.Data;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayerState;
import net.daboross.bukkitdev.skywars.api.ingame.SkySavedInventory;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import org.bukkit.entity.Player;

@Data
public class PlayerInfo implements SkyPlayer {

    private final Player player;
    private final String name;
    private int gameId;
    private SkyPlayerState state;
    private SkyKit selectedKit;
    private SkySavedInventory savedInventory;

    public PlayerInfo(@NonNull final Player player) {
        this(player, player.getName().toLowerCase());
    }

    private PlayerInfo(@NonNull final Player player, @NonNull final String name) {
        this.player = player;
        this.name = name.toLowerCase();
    }
}

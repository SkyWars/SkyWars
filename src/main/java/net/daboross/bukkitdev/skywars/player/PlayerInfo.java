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

import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayerState;
import net.daboross.bukkitdev.skywars.api.ingame.SkySavedInventory;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class PlayerInfo implements SkyPlayer {

    private final Player player;
    private final String name;
    private int gameId;
    private SkyPlayerState state;
    private SkyKit selectedKit;
    private SkySavedInventory savedInventory;

    public PlayerInfo(final Player player) {
        Validate.notNull(player, "Player cannot be null");
        this.player = player;
        this.name = player.getName().toLowerCase();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getGameId() {
        return gameId;
    }

    public void setGameId(final int gameId) {
        this.gameId = gameId;
    }

    @Override
    public SkyPlayerState getState() {
        return state;
    }

    public void setState(final SkyPlayerState state) {
        this.state = state;
    }

    @Override
    public SkyKit getSelectedKit() {
        return selectedKit;
    }

    @Override
    public void setSelectedKit(final SkyKit selectedKit) {
        this.selectedKit = selectedKit;
    }

    @Override
    public SkySavedInventory getSavedInventory() {
        return savedInventory;
    }

    @Override
    public void setSavedInventory(final SkySavedInventory savedInventory) {
        this.savedInventory = savedInventory;
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "player=" + player +
                ", name='" + name + '\'' +
                ", gameId=" + gameId +
                ", state=" + state +
                ", selectedKit=" + selectedKit +
                ", savedInventory=" + savedInventory +
                '}';
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerInfo)) return false;

        PlayerInfo info = (PlayerInfo) o;

        if (gameId != info.gameId) return false;
        if (!name.equals(info.name)) return false;
        if (!player.equals(info.player)) return false;
        if (savedInventory != null ? !savedInventory.equals(info.savedInventory) : info.savedInventory != null)
            return false;
        if (selectedKit != null ? !selectedKit.equals(info.selectedKit) : info.selectedKit != null) return false;
        if (state != info.state) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = player.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + gameId;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (selectedKit != null ? selectedKit.hashCode() : 0);
        result = 31 * result + (savedInventory != null ? savedInventory.hashCode() : 0);
        return result;
    }
}

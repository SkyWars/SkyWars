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

import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayerState;
import net.daboross.bukkitdev.skywars.api.players.SkySavedInventory;
import net.daboross.bukkitdev.skywars.api.storage.SkyInternalPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public abstract class AbstractSkyPlayer implements SkyInternalPlayer {

    protected final Player player;
    protected final String name;
    protected final UUID uuid;
    private int gameId;
    private SkyPlayerState state;
    private SkyKit selectedKit;
    private SkySavedInventory savedInventory;

    public AbstractSkyPlayer(final Player player) {
        Validate.notNull(player, "Player cannot be null");
        this.player = player;
        this.name = player.getName().toLowerCase();
        this.uuid = player.getUniqueId();
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
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public int getGameId() {
        return gameId;
    }

    @Override
    public void setGameId(final int gameId) {
        this.gameId = gameId;
    }

    @Override
    public SkyPlayerState getState() {
        return state;
    }

    @Override
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
        if (!(o instanceof AbstractSkyPlayer)) return false;

        AbstractSkyPlayer info = (AbstractSkyPlayer) o;

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

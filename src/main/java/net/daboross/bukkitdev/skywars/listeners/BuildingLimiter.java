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
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class BuildingLimiter implements Listener {

    private final SkyWars plugin;

    public BuildingLimiter(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent evt) {
        if (!isValid(evt.getPlayer(), evt.getBlock())) {
            evt.setBuild(false);
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent evt) {
        if (!isValid(evt.getPlayer(), evt.getBlock())) {
            evt.setCancelled(true);
        }
    }

    private boolean isValid(Player p, Block block) {
        SkyGame game = plugin.getIDHandler().getGame(plugin.getCurrentGameTracker().getGameID(p.getName()));
        if (game == null) {
            return true;
        }
        SkyBlockLocationRange range = game.getBuildingBoundaries();
        return range.isWithin(block);
    }
}

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
package net.daboross.bukkitdev.skywars.game.reactors;

import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameInfo;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class ResetInventoryHealth {

    private final Plugin plugin;

    public ResetInventoryHealth( Plugin plugin ) {
        this.plugin = plugin;
    }

    public void onGameStart( GameStartInfo info ) {
        for ( Player p : info.getPlayers() ) {
            p.setGameMode( GameMode.SURVIVAL );
            try {
                p.setHealth( p.getMaxHealth() );
            } catch ( NoSuchMethodError unused ) {
                plugin.getLogger().log( Level.INFO, "Couldn't reset health. Probably due to server version being below 1.6.2" );
            }
            p.getInventory().clear();
            p.getInventory().setArmorContents( new ItemStack[ 4 ] );
            p.setFoodLevel( 20 );
        }
    }

    public void onPlayerLeave( PlayerLeaveGameInfo info ) {
        Player p = info.getPlayer();
        p.setGameMode( GameMode.SURVIVAL );
        try {
            p.setHealth( p.getMaxHealth() );
        } catch ( NoSuchMethodError unused ) {
            plugin.getLogger().log( Level.INFO, "Couldn't reset health. Probably due to server version being below 1.6.2" );
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents( new ItemStack[ 4 ] );
        p.setFoodLevel( 20 );
    }
}

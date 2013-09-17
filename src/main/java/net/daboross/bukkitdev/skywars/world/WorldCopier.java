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
package net.daboross.bukkitdev.skywars.world;

import java.util.Arrays;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class WorldCopier {

    private final Plugin plugin;

    public WorldCopier( Plugin plugin ) {
        this.plugin = plugin;
    }

    public void copyArena( @NonNull SkyBlockLocation toMin, @NonNull SkyBlockLocationRange from ) {
        copy( from.min, from.max, toMin );
    }

    public void destroyArena( @NonNull SkyBlockLocation center, @NonNull SkyBlockLocationRange area ) {
        World world = Bukkit.getWorld( center.world );
        if ( world == null ) {
            throw new IllegalArgumentException( "No world applicable." );
        }
        int xLength = area.max.x - area.min.x + 30;
        int zLength = area.max.z - area.min.z + 30;
        SkyBlockLocation min = new SkyBlockLocation( center.x - xLength / 2, area.min.y, center.z - zLength / 2, center.world );
        SkyBlockLocation length = new SkyBlockLocation( xLength, area.max.y - area.min.y, zLength, center.world );
        destroyArena( min, length, world );
    }

    public void destroyArena( @NonNull SkyBlockLocation min, @NonNull SkyBlockLocation length, @NonNull World world ) {
        for ( int x = 0 ; x < length.x ; x++ ) {
            for ( int y = 0 ; y < length.y ; y++ ) {
                for ( int z = 0 ; z < length.z ; z++ ) {
                    world.getBlockAt( min.x + x, min.y + y, min.z + z ).setType( Material.AIR );
                }
            }
        }
    }

    public void copy( @NonNull SkyBlockLocation fromMin, @NonNull SkyBlockLocation fromMax, @NonNull SkyBlockLocation toMin ) {
        if ( !fromMin.world.equals( fromMax.world ) ) {
            throw new IllegalArgumentException( "From min and from max are not in same world" );
        }
        World fromWorld = Bukkit.getWorld( fromMin.world );
        if ( fromWorld == null ) {
            throw new IllegalArgumentException( "From world doesn't exist" );
        }
        World toWorld = Bukkit.getWorld( toMin.world );
        if ( toWorld == null ) {
            throw new IllegalArgumentException( "To world doesn't exist" );
        }
        int xLength = fromMax.x - fromMin.x;
        int yLength = fromMax.y - fromMin.y;
        int zLength = fromMax.z - fromMin.z;
        for ( int x = 0 ; x <= xLength ; x++ ) {
            for ( int y = 0 ; y <= yLength ; y++ ) {
                for ( int z = 0 ; z <= zLength ; z++ ) {
                    Block from = fromWorld.getBlockAt( fromMin.x + x, fromMin.y + y, fromMin.z + z );
                    Block to = toWorld.getBlockAt( toMin.x + x, toMin.y + y, toMin.z + z );
                    to.setType( from.getType() );
                    to.setData( from.getData() );
                    BlockState fromState = from.getState();
                    if ( fromState instanceof Chest ) {
                        Chest toChest = (Chest) to.getState();
                        Chest fromChest = (Chest) fromState;
                        ItemStack[] contents = fromChest.getBlockInventory().getContents();
                        toChest.getBlockInventory().setContents( Arrays.copyOf( contents, contents.length ) );
                        toChest.update( true );
                    }
                }
            }
        }
    }
}

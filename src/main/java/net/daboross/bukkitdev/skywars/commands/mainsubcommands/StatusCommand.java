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
package net.daboross.bukkitdev.skywars.commands.mainsubcommands;

import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class StatusCommand extends SubCommand {

    private final SkyWars plugin;

    public StatusCommand( SkyWars plugin ) {
        super( "status", true, "skywars.status", "Gives game status" );
        this.addCommandFilter( new ArgumentFilter( ArgumentFilter.ArgumentCondition.EQUALS, 0, ColorList.ERR + "Too many arguments!" ) );
        this.plugin = plugin;
    }

    @Override
    public void runCommand( CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs ) {
        SkyIDHandler idh = plugin.getIDHandler();
        sender.sendMessage( String.format( ColorList.TOP_FORMAT, "SkyWars Status" ) );
        sender.sendMessage( ColorList.REG + "In Queue: " + ColorList.DATA + ArrayHelpers.combinedWithSeperator( plugin.getGameQueue().getCopy(), ColorList.REG + ", " + ColorList.DATA ) );
        sender.sendMessage( String.format( ColorList.TOP_FORMAT, "Current Arenas" ) );
        for ( Integer id : idh.getCurrentIDs() ) {
            SkyGame game = idh.getGame( id );
            sender.sendMessage( ColorList.DATA + id + ColorList.REG + ": " + getPlayerString( game ) );
        }
    }

    private String getPlayerString( SkyGame game ) {
        StringBuilder resultBuilder = new StringBuilder();
        List<String> alive = game.getAlivePlayers();
        List<String> dead = game.getDeadPlayers();
        switch ( alive.size() ) {
            case 0:
                break;
            case 1:
                resultBuilder.append( ChatColor.GREEN ).append( alive.get( 0 ) );
                break;
            default:
                resultBuilder.append( ChatColor.GREEN ).append( alive.get( 0 ) );
                for ( int i = 1 ; i < alive.size() ; i++ ) {
                    resultBuilder.append( ColorList.REG ).append( ", " ).append( ChatColor.GREEN ).append( alive.get( i ) );
                }
        }
        switch ( dead.size() ) {
            case 0:
                break;
            case 1:
                if ( resultBuilder.length() == 0 ) {
                    resultBuilder.append( ChatColor.RED ).append( dead.get( 0 ) );
                } else {
                    resultBuilder.append( ColorList.REG ).append( ", " ).append( ChatColor.RED ).append( dead.get( 0 ) );
                }
            default:
                if ( resultBuilder.length() == 0 ) {
                    resultBuilder.append( ChatColor.RED ).append( dead.get( 0 ) );
                    for ( int i = 1 ; i < alive.size() ; i++ ) {
                        resultBuilder.append( ColorList.REG ).append( ", " ).append( ChatColor.RED ).append( dead.get( i ) );
                    }
                } else {
                    for ( int i = 0 ; i < dead.size() ; i++ ) {
                        resultBuilder.append( ColorList.REG ).append( ", " ).append( ChatColor.RED ).append( dead.get( i ) );
                    }
                }
        }
        return resultBuilder.toString();
    }
}

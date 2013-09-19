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

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class LeaveCommand extends SubCommand {

    private final SkyWars plugin;

    public LeaveCommand( SkyWars plugin ) {
        super( "leave", false, "skywars.leave", "Leaves the queue or the game you are in" );
        this.addCommandFilter( new ArgumentFilter( ArgumentFilter.ArgumentCondition.EQUALS, 0, ColorList.ERR + "Too many arguments!" ) );
        this.plugin = plugin;
    }

    @Override
    public void runCommand( CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs ) {
        if ( plugin.getGameQueue().inQueue( sender.getName() ) ) {
            plugin.getGameQueue().removePlayer( sender.getName() );
            sender.sendMessage( Messages.Leave.REMOVED_FROM_QUEUE );
        } else if ( plugin.getCurrentGameTracker().isInGame( sender.getName() ) ) {
            plugin.getGameHandler().removePlayerFromGame( sender.getName(), true, true );
            sender.sendMessage( Messages.Leave.REMOVED_FROM_GAME );
        } else {
            sender.sendMessage( Messages.Leave.NOT_IN );
        }
    }
}

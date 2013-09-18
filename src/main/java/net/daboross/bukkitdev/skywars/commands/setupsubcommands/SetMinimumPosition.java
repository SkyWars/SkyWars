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
package net.daboross.bukkitdev.skywars.commands.setupsubcommands;

import lombok.NonNull;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import net.daboross.bukkitdev.skywars.commands.setupstuff.StartedArenaCondition;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 */
public class SetMinimumPosition extends SubCommand {

    private final SkyWars plugin;
    private final SetupStates states;

    public SetMinimumPosition( @NonNull SkyWars plugin, @NonNull SetupStates states ) {
        super( "setmin", false, null, "Sets the minimum position for the arena to copy from to your current eye location." );
        StartedArenaCondition condition = new StartedArenaCondition( states, true );
        addCommandFilter( condition );
        addCommandPreCondition( condition );
        this.plugin = plugin;
        this.states = states;
    }

    @Override
    public void runCommand( CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs ) {
        Player p = (Player) sender;
        Location eye = p.getEyeLocation();
        SkyBlockLocation min = new SkyBlockLocation( eye );
        sender.sendMessage( ColorList.REG + "Setting the minimum position to x=" + min.x + " y=" + min.y + " z=" + min.z + " world=" + min.world );
        states.getSetupState( p.getName() ).setOriginMin( min );
    }
}

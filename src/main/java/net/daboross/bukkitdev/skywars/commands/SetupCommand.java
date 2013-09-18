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
package net.daboross.bukkitdev.skywars.commands;

import lombok.NonNull;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.SkyStatic;
import org.bukkit.command.PluginCommand;

/**
 *
 */
public class SetupCommand {

    private final SkyWars plugin;
    private final CommandExecutorBase base;

    public SetupCommand( @NonNull SkyWars plugin ) {
        this.plugin = plugin;
        this.base = new CommandExecutorBase( null );
        this.initCommands();
    }

    private void initCommands() {
    }

    public void latchOnto( PluginCommand command ) {
        if ( command != null ) {
            command.setDescription( "Setup command for " + SkyStatic.getPluginName() );
            command.setExecutor( base );
            command.setUsage( "/<command>" );
            command.setPermission( null );
        }
    }
}

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
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.commands.setupstuff.BoundariesSetCondition;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import net.daboross.bukkitdev.skywars.gist.GistReport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 *
 */
public class SaveCurrentArena extends SubCommand {

    private final SkyWars plugin;
    private final SetupStates states;

    public SaveCurrentArena( @NonNull SkyWars plugin, @NonNull SetupStates states ) {
        super( "save", false, null, "Saves the current arena setup to file." );
        BoundariesSetCondition condition = new BoundariesSetCondition( states );
        addCommandFilter( condition );
        addCommandPreCondition( condition );
        this.plugin = plugin;
        this.states = states;
    }

    @Override
    public void runCommand( CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs ) {
        sender.sendMessage( ColorList.REG + "Saving arena" );
        SkyArenaConfig config = states.getSetupState( sender.getName() ).convertToArenaConfig();
        plugin.getConfiguration().saveArena( config );
        sender.sendMessage( ColorList.REG + "Now saving a configuration debug - this is for testing and will be disabled in the future." );
        new GistReportRunnable( plugin, sender.getName(), getData( config ) ).runMe();
    }

    private String getData( SkyArenaConfig config ) {
        return "##" + config.getArenaName() + "\n```\n" + config.toIndentedString( 0 ) + "\n```\n";
    }

    private static class GistReportRunnable implements Runnable {

        private final Plugin plugin;
        private final String playerName;
        private final String text;

        public GistReportRunnable( Plugin plugin, String playerName, String text ) {
            this.plugin = plugin;
            this.playerName = playerName;
            this.text = text;
        }

        public void runMe() {
            plugin.getServer().getScheduler().runTaskAsynchronously( plugin, this );
        }

        @Override
        public void run() {
            String url = GistReport.gistText( plugin.getLogger(), text );
            plugin.getServer().getScheduler().runTask( plugin, new SendResult( playerName, ColorList.REG + "Debug data url: " + url ) );
        }

        private static class SendResult implements Runnable {

            private final String playerName;
            private final String result;

            public SendResult( String playerName, String result ) {
                this.playerName = playerName;
                this.result = result;
            }

            @Override
            public void run() {
                CommandSender sender = Bukkit.getPlayer( playerName );
                if ( sender != null ) {
                    sender.sendMessage( result );
                }
            }
        }
    }
}

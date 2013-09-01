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
package net.daboross.bukkitdev.skywars;

import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.commands.CancelAllCommand;
import net.daboross.bukkitdev.skywars.commands.CancelCommand;
import net.daboross.bukkitdev.skywars.commands.JoinCommand;
import net.daboross.bukkitdev.skywars.commands.LeaveCommand;
import net.daboross.bukkitdev.skywars.commands.LobbyCommand;
import net.daboross.bukkitdev.skywars.commands.SetLobbyCommand;
import net.daboross.bukkitdev.skywars.commands.SetPortalCommand;
import net.daboross.bukkitdev.skywars.commands.StatusCommand;
import net.daboross.bukkitdev.skywars.commands.VersionCommand;
import org.bukkit.command.CommandExecutor;

/**
 *
 * @author daboross
 */
public class CommandBase {

    private final SkyWars plugin;
    private final CommandExecutorBase base;

    public CommandBase(SkyWars plugin) {
        this.plugin = plugin;
        this.base = new CommandExecutorBase(null);
        this.initCommands();
    }

    private void initCommands() {
        base.addSubCommand(new JoinCommand(plugin));
        base.addSubCommand(new LeaveCommand(plugin));
        base.addSubCommand(new SetLobbyCommand(plugin));
        base.addSubCommand(new SetPortalCommand(plugin));
        base.addSubCommand(new CancelCommand(plugin));
        base.addSubCommand(new StatusCommand(plugin));
        base.addSubCommand(new VersionCommand(plugin));
        base.addSubCommand(new LobbyCommand(plugin));
        base.addSubCommand(new CancelAllCommand(plugin));
    }

    public CommandExecutor getExecutor() {
        return base;
    }
}

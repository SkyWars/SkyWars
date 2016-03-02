/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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

import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import net.daboross.bukkitdev.skywars.commands.setupsubcommands.NewKit;
import net.daboross.bukkitdev.skywars.commands.setupsubcommands.SaveCurrentArena;
import net.daboross.bukkitdev.skywars.commands.setupsubcommands.SetPos1;
import net.daboross.bukkitdev.skywars.commands.setupsubcommands.SetPos2;
import net.daboross.bukkitdev.skywars.commands.setupsubcommands.SetSpawnLocation;
import net.daboross.bukkitdev.skywars.commands.setupsubcommands.StartNewArena;
import org.bukkit.command.PluginCommand;

public class SetupCommand {

    private final SkyWars plugin;
    private final CommandExecutorBase base;
    private final SetupStates states;

    public SetupCommand(SkyWars plugin) {
        this.plugin = plugin;
        this.base = new CommandExecutorBase("skywars.setup");
        this.states = new SetupStates();
        this.initCommands();
    }

    private void initCommands() {
        base.addSubCommand(new NewKit(plugin));
        base.addSubCommand(new StartNewArena(plugin, states));
        base.addSubCommand(new SetPos1(states));
        base.addSubCommand(new SetPos2(states));
        base.addSubCommand(new SetSpawnLocation(states));
        base.addSubCommand(new SaveCurrentArena(plugin, states));
    }

    public void latchOnto(PluginCommand command) {
        if (command != null) {
            command.setDescription(SkyTrans.get(TransKey.SETUP_CMD_DESCRIPTION));
            command.setExecutor(base);
            command.setUsage("/<command>");
            command.setPermission(null);
        }
    }
}

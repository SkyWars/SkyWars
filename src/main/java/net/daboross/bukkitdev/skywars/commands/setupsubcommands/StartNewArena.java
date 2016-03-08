/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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

import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupData;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import net.daboross.bukkitdev.skywars.commands.setupstuff.WESetupData;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartNewArena extends SubCommand {

    private final SkyWars plugin;
    private final SetupStates states;

    public StartNewArena(SkyWars plugin, SetupStates states) {
        super("start", false, null, SkyTrans.get(TransKey.SWS_START_DESCRIPTION));
        Validate.notNull(states, "SetupStates cannot be null");
        addArgumentNames(SkyTrans.get(TransKey.SWS_START_NAME_ARGUMENT));
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 1, SkyTrans.get(TransKey.SWS_START_ONE_ARGUMENT)));
        this.plugin = plugin;
        this.states = states;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        SetupData state;
        if (plugin.getConfiguration().isWorldeditHookEnabled() && plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            state = new WESetupData(plugin);
        } else {
            state = new SetupData(plugin);
        }
        state.setArenaName(subCommandArgs[0]);
        states.setSetupState(((Player) sender).getUniqueId(), state);
        sender.sendMessage(SkyTrans.get(TransKey.SWS_START_CONFIRMATION, subCommandArgs[0]));
    }
}

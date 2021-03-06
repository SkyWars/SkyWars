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
package net.daboross.bukkitdev.skywars.commands.setupstuff;

import net.daboross.bukkitdev.commandexecutorbase.CommandFilter;
import net.daboross.bukkitdev.commandexecutorbase.CommandPreCondition;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartedArenaCondition implements CommandPreCondition, CommandFilter {

    private final SetupStates states;
    private final boolean started;

    public StartedArenaCondition(final SetupStates states, final boolean started) {
        this.states = states;
        this.started = started;
    }

    @Override
    public boolean canContinue(CommandSender sender, SubCommand subCommand) {
        return (states.getSetupState(((Player) sender).getUniqueId()) == null) != started;
    }

    @Override
    public boolean canContinue(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        return (states.getSetupState(((Player) sender).getUniqueId()) == null) != started;
    }

    @Override
    public String[] getDeniedMessage(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (states.getSetupState(((Player) sender).getUniqueId()) == null) {
            return new String[]{SkyTrans.get(TransKey.SWS_NO_ARENA_STARTED)};
        } else {
            return new String[]{SkyTrans.get(TransKey.SWS_ARENA_ALREADY_STARTED)};
        }
    }
}

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
package net.daboross.bukkitdev.skywars.commands.setupstuff;

import lombok.AllArgsConstructor;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandFilter;
import net.daboross.bukkitdev.commandexecutorbase.CommandPreCondition;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 */
@AllArgsConstructor
public class NoSpawnSetCondition implements CommandPreCondition, CommandFilter {

    private final SetupStates states;

    @Override
    public boolean canContinue(CommandSender sender, SubCommand subCommand) {
        SetupData state = states.getSetupState(sender.getName());
        if (state == null || state.getSpawns().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        SetupData state = states.getSetupState(sender.getName());
        if (state == null || state.getSpawns().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public String[] getDeniedMessage(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        return new String[]{ColorList.ERR + "You can't edit the boundaries after having spawn locations set."};
    }
}

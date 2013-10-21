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
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.commands.setupstuff.NoSpawnSetCondition;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupData;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import net.daboross.bukkitdev.skywars.commands.setupstuff.StartedArenaCondition;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPos1 extends SubCommand {

    private final SetupStates states;

    public SetPos1(@NonNull SetupStates states) {
        super("setpos1", false, null, "Sets the first position for the arena to copy from to your current eye location.");
        StartedArenaCondition condition = new StartedArenaCondition(states, true);
        addCommandFilter(condition);
        addCommandPreCondition(condition);
        NoSpawnSetCondition condition2 = new NoSpawnSetCondition(states);
        addCommandFilter(condition2);
        addCommandPreCondition(condition2);
        this.states = states;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player p = (Player) sender;
        Location eye = p.getEyeLocation();
        SkyBlockLocation pos1 = new SkyBlockLocation(eye);
        SetupData state = states.getSetupState(p.getName());
        if (state.getOriginPos2() != null && !state.getOriginPos2().world.equalsIgnoreCase(pos1.world)) {
            sender.sendMessage("Unsetting the second position due to you being in a different world.");
            state.setOriginPos2(null);
        }
        sender.sendMessage(ColorList.REG + "Setting the first position to " + pos1);
        state.setOriginPos1(pos1);
    }
}

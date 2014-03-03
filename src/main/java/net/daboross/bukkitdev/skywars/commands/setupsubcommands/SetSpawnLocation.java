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
package net.daboross.bukkitdev.skywars.commands.setupsubcommands;

import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.setupstuff.BoundariesSetCondition;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupData;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnLocation extends SubCommand {

    private final SetupStates states;

    public SetSpawnLocation(SetupStates states) {
        super("addspawn", false, null, SkyTrans.get(TransKey.SWS_ADDSPAWN_DESCRIPTION));
        Validate.notNull(states, "SetupStates cannot be null");
        BoundariesSetCondition condition = new BoundariesSetCondition(states);
        addCommandFilter(condition);
        addCommandPreCondition(condition);
        this.states = states;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player p = (Player) sender;
        SetupData state = states.getSetupState(p.getName());
        SkyPlayerLocation pos = new SkyPlayerLocation(p.getLocation()).subtract(state.getOriginMin());
        sender.sendMessage(SkyTrans.get(TransKey.SWS_ADDSPAWN_CONFIRMATION, pos.toString()));
        state.getSpawns().add(pos);
    }
}

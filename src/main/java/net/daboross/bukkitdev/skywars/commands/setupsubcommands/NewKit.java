/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NewKit extends SubCommand {

    private final SkyWars plugin;

    public NewKit(final SkyWars plugin) {
        super("createkit", false, null, SkyTrans.get(TransKey.SWS_CREATEKIT_DESCRIPTION));
        addArgumentNames(SkyTrans.get(TransKey.SWS_CREATEKIT_NAME_ARGUMENT_NAME),
                SkyTrans.get(TransKey.SWS_CREATEKIT_COST_ARGUMENT_NAME),
                SkyTrans.get(TransKey.SWS_CREATEKIT_PERMISSION_ARGUMENT_NAME));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command baseCommand, final String baseCommandLabel, final String subCommandLabel, final String[] subCommandArgs) {
        // TODO: do this?
    }
}

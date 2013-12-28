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
package net.daboross.bukkitdev.skywars.commands.mainsubcommands;

import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CancelCommand extends SubCommand {

    private final SkyWars plugin;

    public CancelCommand(SkyWars plugin) {
        super("cancel", true, "skywars.cancel", SkyTrans.get(TransKey.CMD_CANCEL_DESCRIPTION));
        addArgumentNames("ID");
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        int id;
        try {
            id = Integer.parseInt(subCommandArgs[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_CANCEL_NOT_AN_INT, subCommandArgs[0]));
            return;
        }
        SkyIDHandler idh = plugin.getIDHandler();
        if (idh.getGame(id) == null) {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_CANCEL_NO_GAMES_WITH_ID, id));
            return;
        }
        sender.sendMessage(SkyTrans.get(TransKey.CMD_CANCEL_CONFIRMATION, id));
        plugin.getGameHandler().endGame(id, true);
    }
}

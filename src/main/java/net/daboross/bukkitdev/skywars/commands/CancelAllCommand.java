/*
 * Copyright (C) 2013 daboross
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

import java.util.ArrayList;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.game.GameIDHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class CancelAllCommand extends SubCommand {

    private final SkyWarsPlugin plugin;

    public CancelAllCommand(SkyWarsPlugin plugin) {
        super("cancelall", true, "skywars.cancelall", "Cancels all current games.");
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, ColorList.ERR + "Too many arguments!"));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        GameIDHandler idh = plugin.getIDHandler();
        for (int id : new ArrayList<Integer>(idh.getCurrentIDs())) {
            sender.sendMessage(ColorList.REG + "Canceling game " + ColorList.DATA + id);
            plugin.getGameHandler().endGame(id, true);
        }
    }
}

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
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class LeaveCommand extends SubCommand {

    private final String REMOVED_FROM_QUEUE = ColorList.REG + "You are no longer in the queue.";
    private final String REMOVED_FROM_GAME = ColorList.REG + "You have fled the game.";
    private final String NOT_IN = ColorList.ERR + "You were not in the queue.";
    private final SkyWarsPlugin plugin;

    public LeaveCommand(SkyWarsPlugin plugin) {
        super("leave", false, "skywars.leave", "Leaves the queue or the game you are in");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length != 0) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (plugin.getGameQueue().inQueue(sender.getName())) {
            plugin.getGameQueue().removePlayer(sender.getName());
            sender.sendMessage(REMOVED_FROM_QUEUE);
        } else if (plugin.getCurrentGameTracker().isInGame(sender.getName())) {
            plugin.getGameHandler().removePlayerFromGame(sender.getName(), true, true);
            sender.sendMessage(REMOVED_FROM_GAME);
        } else {
            sender.sendMessage(NOT_IN);
        }
    }
}

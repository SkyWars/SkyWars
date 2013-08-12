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
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class JoinCommand extends SubCommand {

    private final SkyWarsPlugin plugin;

    public JoinCommand(SkyWarsPlugin plugin) {
        super("join", false, "skywars.join", "Joins the queue for the next game");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player player = (Player) sender;
        if (subCommandArgs.length != 0) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String name = player.getName().toLowerCase();
        if (plugin.getCurrentGameTracker().isInGame(name)) {
            sender.sendMessage(Messages.Join.IN_GAME);
        } else if (plugin.getGameQueue().inQueue(name)) {
            sender.sendMessage(Messages.Join.ALREADY_QUEUED);
        } else {
            sender.sendMessage(Messages.Join.CONFIRMATION);
            plugin.getGameQueue().queuePlayer(name);
        }
    }
}

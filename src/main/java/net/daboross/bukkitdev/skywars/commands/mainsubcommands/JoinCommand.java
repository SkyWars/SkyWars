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
package net.daboross.bukkitdev.skywars.commands.mainsubcommands;

import java.util.UUID;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand {

    private final SkyWars plugin;

    public JoinCommand(SkyWars plugin) {
        super("join", false, "skywars.join", SkyTrans.get(TransKey.CMD_JOIN_DESCRIPTION));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        UUID uuid = ((Player) sender).getUniqueId();
        if (plugin.getCurrentGameTracker().isInGame(uuid)) {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_IN_GAME));
        } else if (plugin.getGameQueue().inQueue(uuid)) {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_QUEUED));
        } else {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION));
            plugin.getGameQueue().queuePlayer((Player) sender);
        }
    }
}

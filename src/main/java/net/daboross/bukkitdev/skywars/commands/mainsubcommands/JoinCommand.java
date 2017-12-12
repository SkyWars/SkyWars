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
import net.daboross.bukkitdev.skywars.commands.filters.QueueNameValidFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand {

    private final SkyWars plugin;

    public JoinCommand(SkyWars plugin) {
        super("join", false, "skywars.join", SkyTrans.get(TransKey.CMD_JOIN_DESCRIPTION));
        if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
            this.addArgumentNames(SkyTrans.get(TransKey.CMD_ARG_QUEUE_NAME));
            this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS)));
            this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
            this.addCommandFilter(new QueueNameValidFilter(plugin, 0));
        } else {
            this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        }
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        UUID uuid = ((Player) sender).getUniqueId();
        if (plugin.getCurrentGameTracker().isInGame(uuid)) {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_IN_GAME));
        } else if (plugin.getGameQueue().inQueue(uuid)) {
            if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_IN_SPECIFIC_QUEUE, plugin.getGameQueue().getPlayerQueue(uuid)));
            } else {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_QUEUED));
            }
            // Kit GUI is automatically shown when joining, but it should also be shown if already queued.
            plugin.getKitGui().autoOpenGuiIfApplicable((Player) sender);
        } else if (plugin.getGameQueue().inSecondaryQueue(uuid)) {
            if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_IN_SPECIFIC_SECONDARY_QUEUE, plugin.getGameQueue().getPlayerQueue(uuid)));
            } else {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_IN_SECONDARY_QUEUE));
            }
            sender.sendMessage(SkyTrans.get(TransKey.SECONDARY_QUEUE_EXPLANATION));
        } else {
            String queueName;
            if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
                queueName = subCommandArgs[0];
                if (plugin.getGameQueue().isQueueFull(queueName)) {
                    sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_JOINED_SPECIFIC_SECONDARY_QUEUE, queueName));
                    sender.sendMessage(SkyTrans.get(TransKey.SECONDARY_QUEUE_EXPLANATION));
                } else {
                    sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION_SPECIFIC_QUEUE, queueName));
                }
            } else {
                queueName = null;
                if (plugin.getGameQueue().isQueueFull(null)) {
                    sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_JOINED_SECONDARY_QUEUE));
                    sender.sendMessage(SkyTrans.get(TransKey.SECONDARY_QUEUE_EXPLANATION));
                } else {
                    sender.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION));
                }
            }
            plugin.getGameQueue().queuePlayer((Player) sender, queueName);
        }
    }
}

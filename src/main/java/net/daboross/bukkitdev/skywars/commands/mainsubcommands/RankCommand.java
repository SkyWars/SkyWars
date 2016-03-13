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
package net.daboross.bukkitdev.skywars.commands.mainsubcommands;

import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.players.OfflineSkyPlayer;
import net.daboross.bukkitdev.skywars.api.storage.Callback;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends SubCommand {

    private final SkyWars plugin;

    public RankCommand(final SkyWars plugin) {
        super("rank", true, "skywars.rank", SkyTrans.get(TransKey.CMD_RANK_DESCRIPTION));
        this.plugin = plugin;
        addArgumentNames(SkyTrans.get(TransKey.CMD_RANK_ARGUMENT));
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS)));
    }

    @Override
    public void runCommand(final CommandSender sender, final Command baseCommand, final String baseCommandLabel, final String subCommandLabel, final String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            if (sender instanceof Player) {
                displayResult(sender, plugin.getPlayers().getPlayer((Player) sender));
            } else {
                sender.sendMessage(SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS));
            }
        } else {
            if (!sender.hasPermission("skywars.rank.other")) {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_RANK_NO_OTHER_PERMISSION));
                return;
            }
            plugin.getScore().getOfflinePlayer(subCommandArgs[0], new Callback<OfflineSkyPlayer>() {
                @Override
                public void call(final OfflineSkyPlayer value) {
                    if (value == null) {
                        sender.sendMessage(SkyTrans.get(TransKey.CMD_RANK_NOT_FOUND, subCommandArgs[0]));
                    } else {
                        displayResult(sender, value);
                    }
                }
            });
        }
    }

    private void displayResult(CommandSender sender, OfflineSkyPlayer playerToDisplay) {
        sender.sendMessage(SkyTrans.get(TransKey.CMD_RANK_HEADER, playerToDisplay.getName()));
        sender.sendMessage(SkyTrans.get(TransKey.CMD_RANK_RANK, playerToDisplay.getRank() + 1));
        sender.sendMessage(SkyTrans.get(TransKey.CMD_RANK_SCORE, playerToDisplay.getScore()));
    }
}

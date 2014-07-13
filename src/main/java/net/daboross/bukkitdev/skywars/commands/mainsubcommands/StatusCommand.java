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

import java.util.List;
import java.util.UUID;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.game.SkyIDHandler;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayers;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StatusCommand extends SubCommand {

    private final SkyWars plugin;

    public StatusCommand(SkyWars plugin) {
        super("status", true, "skywars.status", SkyTrans.get(TransKey.CMD_STATUS_DESCRIPTION));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        SkyIDHandler idh = plugin.getIDHandler();
        sender.sendMessage(SkyTrans.get(TransKey.CMD_STATUS_HEADER));
        sender.sendMessage(SkyTrans.get(TransKey.CMD_STATUS_IN_QUEUE,
                ArrayHelpers.combinedWithSeperator(plugin.getGameQueue().getCopy(), SkyTrans.get(TransKey.CMD_STATUS_QUEUE_COMMA))));
        sender.sendMessage(SkyTrans.get(TransKey.CMD_STATUS_ARENA_HEADER));
        for (Integer id : idh.getCurrentIDs()) {
            SkyGame game = idh.getGame(id);
            sender.sendMessage(getPlayerString(game));
        }
    }

    private String getPlayerString(SkyGame game) {
        StringBuilder b = new StringBuilder();
        b.append(ColorList.DATA).append(game.getId()).append(ColorList.REG).append(":");
        List<UUID> alive = game.getAlivePlayers();
        SkyPlayers skyPlayers = plugin.getPlayers(); // so we don't call this method once per player
        switch (alive.size()) {
            case 0:
                b.append(ColorList.DATA).append(" -- ");
                break;
            case 1:
                b.append(ChatColor.GREEN).append(skyPlayers.getPlayer(alive.get(0)).getName());
                break;
            default:
                if (game.areTeamsEnabled()) {
                    for (int team = 0; team < game.getNumTeams(); team++) {
                        List<UUID> players = game.getAlivePlayersInTeam(team);
                        if (!players.isEmpty()) {
                            b.append("\n  ").append(ColorList.REG).append("Team ").append(ColorList.DATA).append(team).append(ColorList.REG).append(": ").append(ColorList.DATA).append(skyPlayers.getPlayer(players.get(0)).getName());
                            for (int i = 1; i < players.size(); i++) {
                                // I would use Bukkit.getPlayer(), but this uses a hashmap lookup, which is more efficient.
                                b.append(ColorList.REG).append(", ").append(ColorList.DATA).append(skyPlayers.getPlayer(players.get(i)).getName());
                            }
                        }
                    }
                } else {
                    b.append(" ").append(ChatColor.GREEN).append(skyPlayers.getPlayer(alive.get(0)).getName());
                    for (int i = 1; i < alive.size(); i++) {
                        b.append(ColorList.REG).append(", ").append(ChatColor.GREEN).append(skyPlayers.getPlayer(alive.get(i)).getName());
                    }
                }
        }
        return b.toString();
    }
}

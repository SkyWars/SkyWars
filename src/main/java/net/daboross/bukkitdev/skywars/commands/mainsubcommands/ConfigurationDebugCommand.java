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

import lombok.AllArgsConstructor;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.gist.GistReport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 *
 */
public class ConfigurationDebugCommand extends SubCommand {

    private final SkyWars plugin;

    public ConfigurationDebugCommand(SkyWars plugin) {
        super("cfgdebug", true, "skywars.cfgdebug", "Displays debug information for the current configuration. Best used in console. If '-p' is specified as a parameter than the output is pasted and you are given a link.");
        super.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 3, ColorList.ERR + "Too many parameters"));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        boolean paste = false;
        if (subCommandArgs.length > 0) {
            if (subCommandArgs[0].equalsIgnoreCase("-p")) {
                paste = true;
            } else {
                sender.sendMessage("Invalid argument '" + subCommandArgs[0] + "'.");
                sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
        }
        sender.sendMessage("Gathering data");
        String data = GistReport.generateReportText(plugin.getConfiguration());
        if (paste) {
            new GistReportRunnable(plugin, sender.getName(), data).runMe();
        } else {
            sender.sendMessage(data);
        }

    }

    @AllArgsConstructor
    private static class GistReportRunnable implements Runnable {

        private final Plugin plugin;
        private final String playerName;
        private final String report;

        public void runMe() {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this);
        }

        @Override
        public void run() {
            String url = GistReport.reportReport(report);
            plugin.getServer().getScheduler().runTask(plugin, new SendResult(playerName, "Report url: " + url));
        }

        private static class SendResult implements Runnable {

            private final String playerName;
            private final String result;

            public SendResult(String playerName, String result) {
                this.playerName = playerName;
                this.result = result;
            }

            @Override
            public void run() {
                CommandSender sender = playerName.equalsIgnoreCase("CONSOLE") ? Bukkit.getConsoleSender() : Bukkit.getPlayerExact(playerName);
                if (sender != null) {
                    sender.sendMessage(result);
                }
            }
        }
    }
}

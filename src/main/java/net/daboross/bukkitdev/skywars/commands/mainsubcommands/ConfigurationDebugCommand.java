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
import net.daboross.bukkitdev.skywars.gist.GistReport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ConfigurationDebugCommand extends SubCommand {

    private final SkyWars plugin;

    public ConfigurationDebugCommand(SkyWars plugin) {
        super("report", true, "skywars.report", SkyTrans.get(TransKey.CMD_REPORT_DESCRIPTION));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (plugin.getConfiguration().isDisableReport()) {
            sender.sendMessage(SkyTrans.get(TransKey.REPORT_DISABLED));
        } else {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_REPORT_START));
            String data = GistReport.generateReportText(plugin);
            if (sender instanceof Player) {
                new GistReportRunnable(plugin, ((Player) sender).getUniqueId(), data).runMe();
            } else {
                new GistReportRunnable(plugin, null, data).runMe();
            }
        }
    }

    private static class GistReportRunnable implements Runnable {

        private final Plugin plugin;
        private final UUID playerUuid;
        private final String report;

        private GistReportRunnable(final Plugin plugin, final UUID playerUuid, final String report) {
            this.plugin = plugin;
            this.playerUuid = playerUuid;
            this.report = report;
        }

        public void runMe() {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this);
        }

        @Override
        public void run() {
            String url = GistReport.reportReport(report);
            plugin.getServer().getScheduler().runTask(plugin, new SendResult(playerUuid, SkyTrans.get(TransKey.CMD_REPORT_OUTPUT, url)));
        }

        private static class SendResult implements Runnable {

            private final UUID playerUuid;
            private final String result;

            public SendResult(UUID playerUuid, String result) {
                this.playerUuid = playerUuid;
                this.result = result;
            }

            @Override
            public void run() {
                // null playerUuid signifies that it isn't a player - rather it's the console.
                CommandSender sender = playerUuid == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(playerUuid);
                if (sender != null) {
                    sender.sendMessage(result);
                }
            }
        }
    }
}

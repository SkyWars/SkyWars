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

import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand extends SubCommand {

    private final SkyWars plugin;

    public KitCommand(SkyWars plugin) {
        super("kit", false, "skywars.kit", SkyTrans.get(TransKey.CMD_STATUS_DESCRIPTION));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player p = (Player) sender;
        if (subCommandArgs.length == 0) {
            List<SkyKit> availableKits = plugin.getKits().getAvailableKits(p);

            sender.sendMessage(formatKitList(availableKits));
        }
    }

    private String formatKitList(List<SkyKit> kits) {
        if (kits.isEmpty()) {
            // return SkyTrans.get(); TODO: Create NO KITS AVAILABLE translation.
        }

        StringBuilder builder = new StringBuilder();
        // TODO: Implement
        return builder.toString();
    }
}

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
package net.daboross.bukkitdev.skywars.commands.setupsubcommands;

import java.io.IOException;
import java.util.logging.Level;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKits;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.kits.SkyKitBukkitDecode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewKit extends SubCommand {

    private final SkyWars plugin;

    public NewKit(final SkyWars plugin) {
        super("createkit", false, null, SkyTrans.get(TransKey.SWS_CREATEKIT_DESCRIPTION));
        addArgumentNames(SkyTrans.get(TransKey.SWS_CREATEKIT_NAME_ARGUMENT_NAME),
                SkyTrans.get(TransKey.SWS_CREATEKIT_COST_ARGUMENT_NAME),
                SkyTrans.get(TransKey.SWS_CREATEKIT_PERMISSION_ARGUMENT_NAME));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 4, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command baseCommand, final String baseCommandLabel, final String subCommandLabel, final String[] subCommandArgs) {
        String kitName = subCommandArgs[0];
        String permission = null;
        int cost = 0;
        if (subCommandArgs.length >= 3) {
            try {
                cost = Integer.valueOf(subCommandArgs[1]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(SkyTrans.get(TransKey.NOT_AN_INTEGER, subCommandArgs[1]));
                return;
            }
            permission = subCommandArgs[2];
        } else if (subCommandArgs.length == 2) {
            try {
                cost = Integer.valueOf(subCommandArgs[1]);
            } catch (NumberFormatException ignored) {
                permission = subCommandArgs[1];
            }
        }

        // deal with negative costs the most simple way possible
        cost = Math.abs(cost);

        SkyKits kits = plugin.getKits();
        if (kits.getKit(kitName) != null) {
            sender.sendMessage(SkyTrans.get(TransKey.SWS_CREATEKIT_KIT_EXISTS, kitName));
            return;
        }
        SkyKit kit = SkyKitBukkitDecode.inventoryToKit(((Player) sender).getInventory(), kitName, permission, cost);
        kits.addKit(kit);

        try {
            kits.save();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Saving kits.yml file to disk failed!", e);
            sender.sendMessage(SkyTrans.get(TransKey.SWS_CREATEKIT_SAVE_FAILED));
            return;
        }

        StringBuilder message; // use message builder for optionally appending cost message to end.
        if (permission == null) {
            message = new StringBuilder(SkyTrans.get(TransKey.SWS_CREATEKIT_SAVED, kitName.toLowerCase()));
        } else {
            message = new StringBuilder(SkyTrans.get(TransKey.SWS_CREATEKIT_SAVED_PERMISSION, kitName.toLowerCase(), permission));
        }
        if (cost > 0) {
            message.append(SkyTrans.get(TransKey.SWS_CREATEKIT_COST_TO_USE, cost));
        }
        sender.sendMessage(message.toString());
    }
}

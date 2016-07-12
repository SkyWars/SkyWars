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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UpdateArena extends SubCommand {

    private final SkyWarsPlugin plugin;

    public UpdateArena(final SkyWarsPlugin plugin) {
        super("update-arena", true, null, SkyTrans.get(TransKey.SWS_UPDATEARENA_DESCRIPTION));
        addArgumentNames(SkyTrans.get(TransKey.SWS_UPDATEARENA_ARENA_ARGUMENT));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command baseCommand, final String baseCommandLabel, final String subCommandLabel, final String[] subCommandArgs) {
        SkyArenaConfig realArena = null;
        for (SkyArenaConfig arena : plugin.getConfiguration().getEnabledArenas()) {
            if (arena.getArenaName().equals(subCommandArgs[0])) {
                realArena = arena;
                break;
            }
        }
        if (realArena == null) {
            int found = 0;
            for (SkyArenaConfig arena : plugin.getConfiguration().getEnabledArenas()) {
                if (arena.getArenaName().equalsIgnoreCase(subCommandArgs[0])) {
                    realArena = arena;
                    found++;
                }
            }
            if (found != 1) {
                // If found is > 1, then fall back and ask for correct capitalization
                // If found < 1, then no arena was found (and no arena was found in the first loop)
                sender.sendMessage(SkyTrans.get(TransKey.SWS_UPDATEARENA_UNKNOWN_ARENA, subCommandArgs[0]));
                return;
            }
        }
        try {
            plugin.getWorldHandler().loadNewArena(realArena, true);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update arena blocks cache for " + realArena.getArenaName() + "!", e);
            sender.sendMessage(SkyTrans.get(TransKey.SWS_UPDATEARENA_FAILED, realArena.getArenaName()));
            return;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Origin location")) {
                sender.sendMessage(SkyTrans.get(TransKey.SWS_UPDATEARENA_BUILTIN, realArena.getArenaName()));
                return;
            } else {
                plugin.getLogger().log(Level.SEVERE, "Failed to update arena blocks cache for " + realArena.getArenaName() + "!", e);
                sender.sendMessage(SkyTrans.get(TransKey.SWS_UPDATEARENA_FAILED, realArena.getArenaName()));
            }
            return;
        }
        sender.sendMessage(SkyTrans.get(TransKey.SWS_UPDATEARENA_COMPLETED, realArena.getArenaName()));
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final Command baseCommand, final String baseCommandLabel, final SubCommand subCommand, final String subCommandLabel, final String[] subCommandArgs) {
        if (subCommandArgs.length > 1) {
            return Collections.emptyList();
        }
        ArrayList<String> resultList = new ArrayList<>();
        String argument = subCommandArgs[0].toLowerCase();
        for (SkyArenaConfig arenaConfig : plugin.getConfiguration().getEnabledArenas()) {
            String name = arenaConfig.getArenaName().toLowerCase();
            if (name.startsWith(argument)) {
                resultList.add(name);
            }
        }
        return resultList;
    }
}

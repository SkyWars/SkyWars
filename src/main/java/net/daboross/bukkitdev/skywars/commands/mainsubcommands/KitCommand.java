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
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayerState;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand extends SubCommand {

    private final SkyWars plugin;

    public KitCommand(SkyWars plugin) {
        super("kit", false, "skywars.kit", SkyTrans.get(TransKey.CMD_KIT_DESCRIPTION));
        this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player p = (Player) sender;
        SkyPlayer skyPlayer = plugin.getInGame().getPlayerForce(p);
        if (skyPlayer.getState() == SkyPlayerState.IN_RUNNING_GAME) {
            sender.sendMessage(SkyTrans.get(TransKey.GENERIC_IN_GAME));
            return;
        }
        if (subCommandArgs.length == 0) {
            sendKitList(p, skyPlayer);
        } else if (subCommandArgs[0].equals("remove")) {
            SkyKit kit = skyPlayer.getSelectedKit();
            if (kit != null) {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_KIT_REMOVED_KIT));
                skyPlayer.setSelectedKit(null);
            } else {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_KIT_NO_KIT_REMOVED));
            }
        } else {
            SkyKit kit = plugin.getKits().getKit(subCommandArgs[0]);
            if (kit == null) {
                sender.sendMessage(SkyTrans.get(TransKey.CMD_KIT_UNKNOWN_KIT, subCommandArgs[0]));
                return;
            }
            if (!p.hasPermission(kit.getPermission())) {
                p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_NO_ACCESS, kit.getName()));
                return;
            }
            int cost = kit.getCost();
            if (cost == 0 || plugin.getEconomyHook().canAfford(p.getName(), cost)) {
                skyPlayer.setSelectedKit(kit);
                if (cost == 0) {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CHOSE_KIT, kit.getName()));
                } else {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CHOSE_KIT_WITH_COST, kit.getName(), kit.getCost()));
                }
            } else {
                double diff = cost - plugin.getEconomyHook().getAmount(p.getName());
                sender.sendMessage(SkyTrans.get(TransKey.CMD_KIT_NOT_ENOUGH_MONEY, plugin.getEconomyHook().getCurrencySymbolWord(diff), kit.getName(), diff));
            }
        }
    }

    private void sendKitList(Player p, SkyPlayer skyPlayer) {
        List<SkyKit> availableKits = plugin.getKits().getAvailableKits(p);
        List<SkyKit> unAvailableKits = plugin.getKits().getUnavailableKits(p);
        if (availableKits.isEmpty()) {
            p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_NO_KITS_AVAILABLE));
        } else {
            SkyKit currentKit = skyPlayer.getSelectedKit();
            if (currentKit != null) {
                if (currentKit.getCost() == 0) {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CURRENT_KIT, currentKit.getName()));
                } else {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CURRENT_KIT_WITH_COST, currentKit.getName(), currentKit.getCost()));
                }
            }
            p.sendMessage(SkyTrans.get(TransKey.KITS_CHOOSE_A_KIT));
            p.sendMessage(getAvailableKitList(availableKits));
        }
        if (!unAvailableKits.isEmpty()) {
            p.sendMessage(getUnavailableKitList(unAvailableKits));
        }
    }

    private String getAvailableKitList(List<SkyKit> availableKits) {
        String comma = SkyTrans.get(TransKey.KITS_KIT_LIST_COMMA);
        StringBuilder result = new StringBuilder();
        for (SkyKit kit : availableKits) {
            if (kit.getCost() == 0) {
                result.append(kit.getName());
            } else {
                result.append(SkyTrans.get(TransKey.KITS_KIT_LIST_COST_ITEM, kit.getName(), kit.getCost()));
            }
            result.append(comma);
        }
        return SkyTrans.get(TransKey.KITS_KIT_LIST, result);
    }

    private String getUnavailableKitList(List<SkyKit> unavailableKits) {
        String comma = SkyTrans.get(TransKey.KITS_KIT_LIST_COMMA);
        StringBuilder result = new StringBuilder();
        for (SkyKit kit : unavailableKits) {
            if (kit.getCost() == 0) {
                result.append(kit.getName());
            } else {
                result.append(SkyTrans.get(TransKey.KITS_KIT_LIST_COST_ITEM, kit.getName(), kit.getCost()));
            }
            result.append(comma);
        }
        return SkyTrans.get(TransKey.CMD_KIT_UNAVAILABLE_KITS, result);
    }
}

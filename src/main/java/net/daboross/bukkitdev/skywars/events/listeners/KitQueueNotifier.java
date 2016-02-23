/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.events.listeners;

import java.util.List;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKits;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.kits.KitUtils;
import org.bukkit.entity.Player;

public class KitQueueNotifier {

    private final SkyWars plugin;

    public KitQueueNotifier(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onQueueJoin(PlayerJoinQueueInfo info) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(info.getPlayer());
        SkyKit kit = skyPlayer.getSelectedKit();
        if (kit == null) {
            SkyKits kits = plugin.getKits();
            List<SkyKit> availableKits = kits.getAvailableKits(info.getPlayer());
            if (!availableKits.isEmpty()) {
                info.getPlayer().sendMessage(SkyTrans.get(TransKey.KITS_CHOOSE_A_KIT));
                info.getPlayer().sendMessage(generateKitList(availableKits));
            }
        } else {
            Player player = info.getPlayer();
            String permission = kit.getPermission();
            if (permission != null && !player.hasPermission(permission)) {
                player.sendMessage(SkyTrans.get(TransKey.KITS_NO_PERMISSION, kit.getName()));
                skyPlayer.setSelectedKit(null);
                return;
            }
            int cost = kit.getCost();
            if (cost == 0) {
                player.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CURRENT_KIT, kit.getName()));
            } else if (plugin.getEconomyHook().canAfford(player, cost)) {
                player.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CURRENT_KIT_WITH_COST, kit.getName(), kit.getCost()));
            } else {
                skyPlayer.setSelectedKit(null);
                player.sendMessage(SkyTrans.get(TransKey.KITS_NOT_ENOUGH_MONEY, kit.getName()));
            }
        }
    }

    private String generateKitList(List<SkyKit> kits) {
        return SkyTrans.get(TransKey.KITS_KIT_LIST, KitUtils.formatKitList(kits));
    }
}

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
package net.daboross.bukkitdev.skywars.events.listeners;

import java.util.List;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKits;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;

public class KitQueueNotifier {

    private final SkyWars plugin;

    public KitQueueNotifier(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onQueueJoin(PlayerJoinQueueInfo info) {
        if (plugin.getInGame().getPlayer(info.getPlayer()).getSelectedKit() == null) {
            SkyKits kits = plugin.getKits();
            List<SkyKit> availableKits = kits.getAvailableKits(info.getPlayer());
            if (!availableKits.isEmpty()) {
                info.getPlayer().sendMessage(SkyTrans.get(TransKey.KITS_CHOOSE_A_KIT));
                info.getPlayer().sendMessage(generateKitList(availableKits));
            }
        }
    }

    private String generateKitList(List<SkyKit> kits) {
        StringBuilder kitString = new StringBuilder();
        String comma = SkyTrans.get(TransKey.KITS_KIT_LIST_COMMA);
        for (SkyKit kit : kits) {
            if (kit.getCost() == 0) {
                kitString.append(kit.getName());
            } else {
                kitString.append(SkyTrans.get(TransKey.KITS_KIT_LIST_COST_ITEM, kit.getName(), kit.getCost()));
            }
            kitString.append(comma);
        }
        return SkyTrans.get(TransKey.KITS_KIT_LIST, kitString);
    }
}

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
package net.daboross.bukkitdev.skywars.events.listeners;

import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import org.bukkit.entity.Player;

public class KitApplyListener {

    private final SkyWars plugin;

    public KitApplyListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onGameStart(GameStartInfo info) {
        for (Player p : info.getPlayers()) {
            SkyPlayer skyPlayer = plugin.getInGame().getPlayerForce(p);
            SkyKit kit = skyPlayer.getSelectedKit();
            if (kit != null) {
                int cost = kit.getCost();
                if (cost == 0) {
                    p.sendMessage(SkyTrans.get(TransKey.KITS_APPLIED_KIT, kit.getName()));
                } else if (plugin.getEconomyHook().canAfford(p.getName(), cost)) {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_KIT_CURRENT_KIT_WITH_COST, kit.getName(), kit.getCost()));
                    plugin.getEconomyHook().charge(p.getName(), cost);
                } else {
                    p.sendMessage(SkyTrans.get(TransKey.KITS_NOT_ENOUGH_MONEY));
                }
            }
        }
    }
}

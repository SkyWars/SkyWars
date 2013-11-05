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
package net.daboross.bukkitdev.skywars.economy;

import java.util.List;
import lombok.AllArgsConstructor;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.economy.SkyEconomyAbstraction;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.PlayerKillPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class SkyEconomyGameRewards {

    private final SkyWars plugin;

    public void onPlayerKillPlayer(PlayerKillPlayerInfo info) {
        int reward = plugin.getConfiguration().getEconomyKillReward();
        String killer = info.getKillerName();
        if (plugin.getConfiguration().getEconomyRewardMessages()) {
            Player p = Bukkit.getPlayerExact(killer);
            if (p != null) {
                p.sendMessage(SkyTrans.get(TransKey.ECO_REWARD_KILL, reward, info.getKilled().getName()));
            }
        }
        plugin.getEconomyHook().addReward(killer, reward);
    }

    public void onGameEnd(GameEndInfo info) {
        int reward = plugin.getConfiguration().getEconomyKillReward();
        boolean enableMessages = plugin.getConfiguration().getEconomyRewardMessages();
        SkyEconomyAbstraction eco = plugin.getEconomyHook();
        List<Player> alive = info.getAlivePlayers();
        if (!alive.isEmpty() && alive.size() <= info.getGame().getArena().getTeamSize()) {
            for (Player p : alive) {
                if (enableMessages) {
                    p.sendMessage(SkyTrans.get(TransKey.ECO_REWARD_WIN, reward));
                }
                eco.addReward(p.getName(), reward);
            }
        }
    }
}

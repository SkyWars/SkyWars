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
import net.daboross.bukkitdev.skywars.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.PlayerKillPlayerInfo;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class SkyEconomyGameRewards {

    private final SkyWars plugin;

    public void onPlayerKillPlayer(PlayerKillPlayerInfo info) {
        plugin.getEconomyHook().addReward(info.getKillerName(), plugin.getConfiguration().getEconomyKillReward());
    }

    public void onGameEnd(GameEndInfo info) {
        int reward = plugin.getConfiguration().getEconomyKillReward();
        SkyEconomyAbstraction eco = plugin.getEconomyHook();
        List<Player> alive = info.getAlivePlayers();
        if (!alive.isEmpty() && alive.size() <= info.getGame().getArena().getTeamSize()) {
            for (Player p : alive) {
                eco.addReward(p.getName(), reward);
            }
        }
    }
}

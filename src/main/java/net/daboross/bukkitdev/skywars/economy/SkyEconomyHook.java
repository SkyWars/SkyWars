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

import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.economy.SkyEconomyAbstraction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SkyEconomyHook implements SkyEconomyAbstraction {

    private final Economy economy;

    public SkyEconomyHook(SkyWars plugin) throws EconomyFailedException {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            throw new EconomyFailedException("Vault plugin not found");
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new EconomyFailedException("Economy plugin provider not found");
        }
        this.economy = rsp.getProvider();
        if (this.economy == null) {
            throw new EconomyFailedException("Economy plugin not found");
        }
    }

    public void addReward(String player, double reward) {
        economy.depositPlayer(player, reward);
        SkyStatic.debug("Gave " + player + " an economy reward of " + reward);
    }
}

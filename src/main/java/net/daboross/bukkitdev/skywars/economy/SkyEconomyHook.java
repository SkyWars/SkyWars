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
package net.daboross.bukkitdev.skywars.economy;

import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.economy.SkyEconomyAbstraction;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SkyEconomyHook implements SkyEconomyAbstraction {

    private final Economy economy;
    private final SkyWars plugin;

    public SkyEconomyHook(SkyWars plugin) throws EconomyFailedException {
        this.plugin = plugin;
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

    @Override
    public void addReward(String player, double reward) {
        if (reward > 0) {
            economy.depositPlayer(player, reward);
        } else if (reward < 0) {
            economy.withdrawPlayer(player, -reward);
        }
        SkyStatic.debug("Gave %s an economy reward of %s", player, reward);
    }

    @Override
    public String getCurrencySymbolWord(double amount) {
        String name = amount == 1 || amount == -1 ? economy.currencyNameSingular() : economy.currencyNamePlural();
        switch (name.length()) {
            case 0:
                return "dollars";
            default:
                return name;
        }
    }

    @Override
    public String getCurrencySymbol(double amount) {
        String name = amount == 1 || amount == -1 ? economy.currencyNameSingular() : economy.currencyNamePlural();
        switch (name.length()) {
            case 0:
                return "$";
            case 1:
                return name;
            default:
                return " " + name;
        }
    }

    @Override
    public boolean canAfford(String player, double amount) {
        return economy.has(player, amount);
    }

    @Override
    public boolean charge(String player, double amount) {
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (response.type == EconomyResponse.ResponseType.NOT_IMPLEMENTED) {
            plugin.getLogger().log(Level.WARNING, "Vault-Implementing economy plugin {0} doesn''t support withdrawPlayer. This will cause players to not be able to buy anything.", economy.getName());
        }
        return response.type == EconomyResponse.ResponseType.SUCCESS;
    }

    @Override
    public double getAmount(String player) {
        return 0;
    }
}

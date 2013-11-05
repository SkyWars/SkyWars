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
package net.daboross.bukkitdev.skywars.kits;

import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class SkyKitDecoder {

    public static SkyKit decodeKit(ConfigurationSection section) throws SkyConfigurationException {
        throw new SkyConfigurationException();
    }

    public static SkyKitItem decodeItem(ConfigurationSection section) throws SkyConfigurationException {
        if (!section.isString("type")) {
            throw new SkyConfigurationException("The item does not define a type.");
        }
        String typeString = section.getString("type");
        int amount = section.isInt("amount") ? section.getInt("amount") : 1;
        Material type;
        try {
            type = Material.getMaterial(typeString.toUpperCase());
        } catch (Exception e) {
            throw new SkyConfigurationException("The type string '" + typeString + "' is not valid. Check http://tiny.cc/BukkitMaterial for a list of valid material names.");
        }
        return new SkyKitItemConfig(type, amount);
    }
}

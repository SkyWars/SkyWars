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
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import org.bukkit.Material;
import org.json.JSONException;
import org.json.JSONObject;

public class SkyKitDecoder {

    public static SkyKitItem itemFromJSON(String str) throws SkyConfigurationException {
        JSONObject json;
        try {
            json = new JSONObject(str);
        } catch (JSONException ex) {
            throw new SkyConfigurationException("The string '" + str + "' is invalid JSON.");
        }
        String typeString;
        int amount;
        try {
            typeString = json.getString("type");
        } catch (JSONException ex) {
            throw new SkyConfigurationException("The item string '" + str + "' does not define a type.");
        }
        try {
            amount = json.getInt("amount");
        } catch (JSONException ex) {
            amount = 1;
        }
        Material type;
        try {
            type = Material.getMaterial(typeString.toUpperCase());
        } catch (Exception e) {
            throw new SkyConfigurationException("The type string '" + str + "' is not valid. Check http://tiny.cc/BukkitMaterial for a list of valid material names.");
        }
        return new SkyKitItemConfig(type, amount);
    }
}

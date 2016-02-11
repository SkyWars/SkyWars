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
package net.daboross.bukkitdev.skywars.player;

import net.daboross.bukkitdev.skywars.api.players.SkySavedInventory;
import net.daboross.bukkitdev.skywars.util.CrossVersion;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SavedInventory implements SkySavedInventory {

    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final int experience;
    private final Location location;
    private final double health;
    private final double healthScale;
    private final float fallDistance;
    private final int foodLevel;
    private final float exhaustion;
    private final float saturation;
    private final boolean allowFlight;
    private final boolean isFlying;
    private final GameMode gameMode;

    public SavedInventory(Player p) {
        PlayerInventory inv = p.getInventory();
        ItemStack[] contents = inv.getContents();
        items = new ItemStack[contents.length];
        ItemStack[] armorContents = inv.getArmorContents();
        armor = new ItemStack[armorContents.length];
        for (int i = 0; i < contents.length; i++) {
            items[i] = contents[i] == null ? null : contents[i].clone();
        }
        for (int i = 0; i < armorContents.length; i++) {
            armor[i] = armorContents[i] == null ? null : armorContents[i].clone();
        }
        location = p.getLocation();
        experience = p.getTotalExperience();
        health = p.getHealth();
        healthScale = p.getHealthScale();
        fallDistance = p.getFallDistance();
        foodLevel = p.getFoodLevel();
        exhaustion = p.getExhaustion();
        saturation = p.getSaturation();
        allowFlight = p.getAllowFlight();
        isFlying = p.isFlying();
        gameMode = p.getGameMode();
    }

    @Override
    public void apply(final Player p, boolean saveExperience, boolean savePgh) {
        if (savePgh) {
            p.teleport(location); // player hasn't been teleported yet if savePgh is enabled.
            CrossVersion.setHealth(p, health);
            p.setHealthScale(healthScale);
            p.setFallDistance(fallDistance);
            p.setFoodLevel(foodLevel);
            p.setExhaustion(exhaustion);
            p.setSaturation(saturation);
            p.setAllowFlight(allowFlight);
            p.setFlying(isFlying);
            p.setGameMode(gameMode);
        }
        PlayerInventory inv = p.getInventory();
        inv.setContents(items);
        inv.setArmorContents(armor);
        if (saveExperience) {
            p.setTotalExperience(experience);
        }
    }
}

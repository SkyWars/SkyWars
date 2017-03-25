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

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.players.SkySavedInventory;
import net.daboross.bukkitdev.skywars.util.CrossVersion;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class SavedInventory implements SkySavedInventory {

    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final int experience;
    private final SavedPghData pghData;

    public SavedInventory(Player p, boolean savePgh) {
        SkyStatic.debug("Saving %s's inventory. [SavedInventory.constructor]", p.getUniqueId());
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
        experience = p.getTotalExperience();
        // don't store all of this if we don't need to.
        if (savePgh) {
            pghData = new SavedPghData(p);
        } else {
            pghData = null;
        }
    }

    @Override
    public void apply(final Player p, boolean restoreExp, boolean restorePgh) {
        SkyStatic.debug("Applying %s's saved inventory. [SavedInventory.apply]", p.getUniqueId());
        // use these two methods to avoid code duplication.
        //
        // player hasn't been teleported yet if savePgh is enabled,
        // so we need to apply it first
        teleportOnlyAndIfPgh(p, restoreExp, restorePgh);
        applyNoTeleportation(p, restoreExp, restorePgh);
    }

    @Override
    public void applyNoTeleportation(final Player p, final boolean restoreExp, final boolean restorePgh) {
        SkyStatic.debug("Applying %s's saved inventory. [SavedInventory.applyNoTeleportation]", p.getUniqueId());
        PlayerInventory inv = p.getInventory();
        inv.setContents(items);
        inv.setArmorContents(armor);
        pghData.apply(p); // I guess setting health can be a problem if armor hasn't been applied yet
        if (restoreExp) {
            p.setTotalExperience(experience);
        }
    }

    @Override
    public void teleportOnlyAndIfPgh(final Player p, final boolean restoreExp, final boolean restorePgh) {
        if (restorePgh) {
            SkyStatic.debug("Teleporting %s's to saved location if location was saved. [SavedInventory.teleportOnlyAndIfPgh]", p.getUniqueId());
            pghData.teleport(p);
        }
    }

    private class SavedPghData {

        private final Location location;
        private final double health;
        private final double maxHealth;
        private final double healthScale;
        private final float fallDistance;
        private final int foodLevel;
        private final float exhaustion;
        private final float saturation;
        private final boolean allowFlight;
        private final boolean isFlying;
        private final GameMode gameMode;
        private final List<PotionEffect> effects;

        private SavedPghData(Player p) {
            location = p.getLocation();
            health = p.getHealth();
            maxHealth = p.getMaxHealth();
            healthScale = p.getHealthScale();
            fallDistance = p.getFallDistance();
            foodLevel = p.getFoodLevel();
            exhaustion = p.getExhaustion();
            saturation = p.getSaturation();
            allowFlight = p.getAllowFlight();
            isFlying = p.isFlying();
            gameMode = p.getGameMode();
            effects = new ArrayList<>(p.getActivePotionEffects());
        }

        /**
         * Player hasn't been teleported yet if savePgh is enabled,
         * this should be done before any other restoration.
         *
         * @param p Player to teleport
         */
        private void teleport(final Player p) {
            SkyStatic.debug("Teleporting %s to %s. [SavedInventory.teleport]", p.getUniqueId(), location);
            p.teleport(location);
        }

        /**
         * Applies all saved data besides teleportation location.
         * <p>
         * This should be used *after* applying armor contents, in order to ensure that setHealth works correctly.
         *
         * @param p Player to apply saved data to
         */
        private void apply(final Player p) {
            CrossVersion.setHealth(p, health);
            p.setHealthScale(healthScale);
            p.setMaxHealth(maxHealth);
            p.setFallDistance(fallDistance);
            p.setFoodLevel(foodLevel);
            p.setExhaustion(exhaustion);
            p.setSaturation(saturation);
            p.setAllowFlight(allowFlight);
            p.setFlying(isFlying);
            p.setGameMode(gameMode);
            for (PotionEffect effect : effects) {
                p.addPotionEffect(effect);
            }
        }
    }
}

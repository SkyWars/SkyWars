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
package net.daboross.bukkitdev.skywars.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

public class CrossVersion {

    private CrossVersion() {
    }

    /**
     * Supports Bukkit earlier than... 1.8?
     */
    public static Collection<? extends Player> getOnlinePlayers(Server s) {
        try {
            return s.getOnlinePlayers();
        } catch (NoSuchMethodError ignored) {
            Class<? extends Server> theClass = s.getClass();
            try {
                for (Method method : theClass.getMethods()) {
                    if ("getOnlinePlayers".equals(method.getName()) && method.getParameterTypes().length == 0
                            && method.getReturnType().isArray()) {
                        return Arrays.asList((Player[]) method.invoke(s));
                    }
                }
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SkyStatic.getLogger().log(Level.WARNING, "Couldn't use fallback .getOnlinePlayers method of Server! Acting as if there are no online players!!", ex);
            }
            SkyStatic.getLogger().log(Level.WARNING, "Couldn't find old fallback .getOnlinePlayers method of Server! Acting as if there are no online players!!");
        }
        return Collections.emptyList();
    }

    /**
     * Supports Bukkit earlier than 1.6. TODO: removable?
     */
    public static void setHealth(Damageable d, double health) {
        Validate.notNull(d, "Damageable cannot be null");
        try {
            d.setHealth(health);
        } catch (NoSuchMethodError ignored) {
            Class<? extends Damageable> dClass = d.getClass();
            try {
                Method healthMethod = dClass.getMethod("setHealth", Integer.TYPE);
                healthMethod.invoke(d, (int) health);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SkyStatic.getLogger().log(Level.WARNING, "Couldn't find / use .setHealth method of LivingEntity!", ex);
            }
        }
    }

    /**
     * Supports Bukkit earlier than 1.6. TODO: removable?
     */
    public static double getMaxHealth(Damageable d) {
        Validate.notNull(d, "Damageable cannot be null");
        try {
            return d.getMaxHealth();
        } catch (NoSuchMethodError ignored) {
            Class<? extends Damageable> dClass = d.getClass();
            try {
                Method healthMethod = dClass.getMethod("getMaxHealth");
                Object obj = healthMethod.invoke(d);
                if (obj instanceof Number) {
                    return ((Number) obj).doubleValue();
                } else {
                    SkyStatic.getLogger().log(Level.WARNING, "LivingEntity.getHealth returned {0}, which is not a Number!", obj);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SkyStatic.getLogger().log(Level.WARNING, "Couldn't find / use .getMaxHealth method of LivingEntity!", ex);
            }
            return 10;
        }
    }

    /**
     * Supports Bukkit earlier than Spigot Bukkit-1.8.4
     */
    public static Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        World world = location.getWorld();
        try {
            return world.getNearbyEntities(location, x, y, z);
        } catch (NoSuchMethodError ignored) {
            Entity entity = world.spawnEntity(location, EntityType.EXPERIENCE_ORB);
            Collection<Entity> result = entity.getNearbyEntities(x, y, z);
            entity.remove();
            return result;
        }
    }

    /**
     * Supports Bukkit earlier than Spigot Bukkit-1.8.7
     */
    @SuppressWarnings("deprecation")
    public static void addPlayerToTeam(Team team, Player player) {
        try {
            team.addEntry(player.getName());
        } catch (NoSuchMethodError ignored) {
            team.addPlayer(player);
        }
    }

    /**
     * Supports Bukkit earlier than Spigot Bukkit-1.8.7
     */
    @SuppressWarnings("deprecation")
    public static void removePlayerFromTeam(Team team, Player player) {
        try {
            team.removeEntry(player.getName());
        } catch (NoSuchMethodError ignored) {
            team.removePlayer(player);
        }
    }

    /**
     * Supports Bukkit earlier than Minecraft 1.9
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(PlayerInventory inventory) {
        try {
            return inventory.getItemInMainHand();
        } catch (NoSuchMethodError ignored) {
            return inventory.getItemInHand();
        }
    }
}

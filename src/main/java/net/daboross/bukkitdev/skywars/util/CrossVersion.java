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
package net.daboross.bukkitdev.skywars.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import org.bukkit.entity.LivingEntity;

public class CrossVersion {

    public static void setHealth(@NonNull LivingEntity p, double health) {
        try {
            p.setHealth(health);
        } catch (NoSuchMethodError ignored) {
            Class<? extends LivingEntity> eClass = p.getClass();
            try {
                Method healthMethod = eClass.getMethod("setHealth", Integer.TYPE);
                healthMethod.invoke(p, Integer.valueOf((int) health));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SkyStatic.getLogger().log(Level.WARNING, "Couldn't find / use .setHealth method of LivingEntity!", ex);
            }
        }
    }

    public static void setMaxHealth(@NonNull LivingEntity p) {
        setHealth(p, getMaxHealth(p));
    }

    public static double getHealth(@NonNull LivingEntity p) {
        try {
            return p.getHealth();
        } catch (NoSuchMethodError ignored) {
            Class<? extends LivingEntity> eClass = p.getClass();
            try {
                Method healthMethod = eClass.getMethod("getHealth");
                Object obj = healthMethod.invoke(p);
                if (obj instanceof Number) {
                    return ((Number) obj).doubleValue();
                } else {
                    SkyStatic.getLogger().log(Level.WARNING, "LivingEntity.getHealth returned {0}, which is not a Number!", obj);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SkyStatic.getLogger().log(Level.WARNING, "Couldn't find / use .getHealth method of LivingEntity!", ex);
            }
            return 0;
        }
    }

    public static double getMaxHealth(@NonNull LivingEntity p) {
        try {
            return p.getMaxHealth();
        } catch (NoSuchMethodError ignored) {
            Class<? extends LivingEntity> eClass = p.getClass();
            try {
                Method healthMethod = eClass.getMethod("getMaxHealth");
                Object obj = healthMethod.invoke(p);
                if (obj instanceof Number) {
                    return ((Number) obj).doubleValue();
                } else {
                    SkyStatic.getLogger().log(Level.WARNING, "LivingEntity.getHealth returned {0}, which is not a Number!", obj);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SkyStatic.getLogger().log(Level.WARNING, "Couldn't find / use .getMaxHealth method of LivingEntity!", ex);
            }
            return 0;
        }
    }
}

/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import org.bukkit.entity.Player;

public class ForceRespawn {

    /**
     * Respawns a player who is currently on the respawn screen - or in the world. This uses reflection, and does
     * trigger PlayerRespawnEvent!
     * <p>
     * Thank you to @dannydog on #spigot IRC for finding the method to do this! It's been since rewritten to use
     * reflection, but using the same method!
     *
     * @param player Player to respawn.
     * @return True if successful, false if an error occurred.
     */
    public static boolean forceRespawn(Player player) {
        // CraftPlayer handle = (CraftPlayer) player.getHandle();
        // handle.server.getPlayerList().moveToWorld(handle, 0, false);
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Class<?> handleClass = handle.getClass();

            Object server = handleClass.getField("server").get(handle);
            Object playerList = server.getClass().getMethod("getPlayerList").invoke(server);

            // int is dimension (as in world dimension: 0 = overworld?), boolean is keepInventory as far as I can tell.
            Method moveToWorld = playerList.getClass().getMethod("moveToWorld", handleClass, int.class, boolean.class);
            moveToWorld.invoke(playerList, handle, 0, false);
            return true;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            SkyStatic.getLogger().log(Level.SEVERE, "Couldn't force player to respawn (after death screen). This should always work when using CraftBukkit or Spigot servers.", e);
            return false;
        }
    }
}

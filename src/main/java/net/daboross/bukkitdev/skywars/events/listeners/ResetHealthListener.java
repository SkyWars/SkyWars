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
package net.daboross.bukkitdev.skywars.events.listeners;

import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import net.daboross.bukkitdev.skywars.util.CrossVersion;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class ResetHealthListener {

    public void onGameStart(GameStartInfo info) {
        for (Player p : info.getPlayers()) {
            resetHealth(p);
        }
    }

    public void onPlayerRespawn(PlayerRespawnAfterGameEndInfo info) {
        resetHealth(info.getPlayer());
    }

    private void resetHealth(Player p) {
        p.setGameMode(GameMode.SURVIVAL);
        CrossVersion.setHealth(p, CrossVersion.getMaxHealth(p));
        p.setFallDistance(0);
        p.setFoodLevel(20);
        p.setExhaustion(0);
        p.setSaturation(0);
        p.setHealthScaled(false);
        p.setAllowFlight(false);
        p.setFlying(false);
        for (PotionEffect effect : p.getActivePotionEffects()) {
            SkyStatic.log(Level.INFO, "Removing:" + effect);
            p.removePotionEffect(effect.getType());
        }
    }
}

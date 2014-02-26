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
package net.daboross.bukkitdev.skywars.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyAttackerStorage;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.ingame.SkyPlayerState;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.PlayerDeathInArenaInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerKillPlayerInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.game.KillMessages;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class AttackerStorageListener implements Listener, SkyAttackerStorage {

    private final Map<String, String> lastHit = new HashMap<>();
    private final Set<String> causedVoid = new HashSet<>();
    private final SkyWarsPlugin plugin;

    public AttackerStorageListener(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        String name = evt.getPlayer().getName().toLowerCase();
        lastHit.remove(name);
        causedVoid.remove(name);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player p = (Player) evt.getEntity();
            String name = p.getName().toLowerCase();
            Entity damager = evt.getDamager();
            if (damager instanceof HumanEntity) {
                lastHit.put(name, ((HumanEntity) damager).getName());
            } else if (damager instanceof Projectile) {
                LivingEntity shooter = ((Projectile) damager).getShooter();
                if (shooter == null) {
                    lastHit.put(name, "Unknown Bowman");
                } else {
                    if (shooter instanceof HumanEntity) {
                        lastHit.put(name, ((HumanEntity) shooter).getName());
                    } else {
                        String customName = shooter.getCustomName();
                        lastHit.put(name, customName == null ? shooter.getType().toString() : customName);
                    }
                }
            } else if (damager instanceof LivingEntity) {
                String customName = ((LivingEntity) damager).getCustomName();
                lastHit.put(name, customName == null ? damager.getType().toString() : customName);
            } else {
                lastHit.put(name, evt.getDamager().getType().toString());
            }
            if (plugin.getCurrentGameTracker().isInGame(name)) {
                evt.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            String name = ((Player) evt.getEntity()).getName().toLowerCase();
            if (evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                causedVoid.add(name);
            } else {
                causedVoid.remove(name);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        String name = evt.getEntity().getName();
        SkyGame game = plugin.getIDHandler().getGame(plugin.getCurrentGameTracker().getGameID(name));
        if (game != null) {
            String killer = lastHit.get(name.toLowerCase());
            plugin.getDistributor().distribute(new PlayerDeathInArenaInfo(game.getId(), evt.getEntity()));
            if (killer != null) {
                plugin.getDistributor().distribute(new PlayerKillPlayerInfo(game.getId(), killer, evt.getEntity()));
            }
            plugin.getGameHandler().removePlayerFromGame(evt.getEntity(), false, false);
            evt.setDeathMessage(KillMessages.getMessage(name, killer, causedVoid.contains(name.toLowerCase()) ? KillMessages.KillReason.VOID : KillMessages.KillReason.OTHER, game.getArena()));
        } else if (plugin.getGameQueue().inQueue(name)) {
            plugin.getGameQueue().removePlayer(evt.getEntity());
            evt.getEntity().sendMessage(SkyTrans.get(TransKey.QUEUE_DEATH));
        }
    }

    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        lastHit.remove(info.getPlayer().getName().toLowerCase());
    }

    @Override
    public String getKiller(String name) {
        return lastHit.get(name.toLowerCase());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent evt) {
        SkyPlayer skyPlayer = plugin.getInGame().getPlayer(evt.getPlayer());
        if (skyPlayer != null && skyPlayer.getState() == SkyPlayerState.WAITING_FOR_RESPAWN) {
            evt.setRespawnLocation(plugin.getLocationStore().getLobbyPosition().toLocation());
            final Player p = evt.getPlayer();
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getGameHandler().respawnPlayer(p);
                }
            });
        }
    }
}

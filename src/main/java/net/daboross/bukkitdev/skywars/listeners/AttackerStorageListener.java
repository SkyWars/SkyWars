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
import java.util.UUID;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.game.SkyAttackerStorage;
import net.daboross.bukkitdev.skywars.api.game.SkyGame;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayerState;
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
import org.bukkit.projectiles.ProjectileSource;

public class AttackerStorageListener implements Listener, SkyAttackerStorage {

    private final Map<UUID, UUID> lastHitUuid = new HashMap<>();
    private final Map<UUID, String> lastHitName = new HashMap<>();
    private final Set<UUID> causedVoid = new HashSet<>();
    private final SkyWarsPlugin plugin;

    public AttackerStorageListener(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        UUID uuid = evt.getPlayer().getUniqueId();
        lastHitUuid.remove(uuid);
        lastHitName.remove(uuid);
        causedVoid.remove(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player p = (Player) evt.getEntity();

            UUID uuid = p.getUniqueId();
            Entity damager = evt.getDamager();
            if (damager instanceof HumanEntity) {
                if (damager instanceof Player) {
                    lastHitUuid.put(uuid, damager.getUniqueId());
                } else {
                    lastHitUuid.remove(uuid);
                }
                lastHitName.put(uuid, ((HumanEntity) damager).getName());
            } else if (damager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) damager).getShooter();
                if (shooter == null || !(shooter instanceof LivingEntity)) { // we want to make sure the shooter is a LivingEntity
                    lastHitUuid.remove(uuid);
                    lastHitName.put(uuid, "Unknown Bowman");
                } else {
                    if (shooter instanceof HumanEntity) {
                        if (shooter instanceof Player) {
                            lastHitUuid.put(uuid, ((Player) shooter).getUniqueId());
                        } else {
                            lastHitUuid.remove(uuid);
                        }
                        lastHitName.put(uuid, ((HumanEntity) shooter).getName());
                    } else {
                        String customName = ((LivingEntity) shooter).getCustomName();
                        lastHitUuid.remove(uuid);
                        lastHitName.put(uuid, customName == null ? ((LivingEntity) shooter).getType().toString() : customName);
                    }
                }
            } else if (damager instanceof LivingEntity) {
                String customName = ((LivingEntity) damager).getCustomName();
                lastHitUuid.remove(uuid);
                lastHitName.put(uuid, customName == null ? damager.getType().toString() : customName);
            } else {
                lastHitUuid.remove(uuid);
                lastHitName.put(uuid, evt.getDamager().getType().toString());
            }
            if (plugin.getCurrentGameTracker().isInGame(uuid)) {
                evt.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            UUID uuid = evt.getEntity().getUniqueId();
            if (evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                causedVoid.add(uuid);
            } else {
                causedVoid.remove(uuid);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        String name = evt.getEntity().getName();
        UUID uuid = evt.getEntity().getUniqueId();
        SkyGame game = plugin.getIDHandler().getGame(plugin.getCurrentGameTracker().getGameId(uuid));
        if (game != null) {
            String killerName = lastHitName.get(uuid);
            UUID killerUuid = lastHitUuid.get(uuid);
            plugin.getDistributor().distribute(new PlayerDeathInArenaInfo(game.getId(), evt.getEntity()));
            if (killerUuid != null && killerUuid != uuid) {
                plugin.getDistributor().distribute(new PlayerKillPlayerInfo(game.getId(), killerUuid, killerName, evt.getEntity()));
            }
            plugin.getGameHandler().removePlayerFromGame(evt.getEntity(), false, false);
            evt.setDeathMessage(KillMessages.getMessage(name, killerName, causedVoid.contains(uuid) ? KillMessages.KillReason.VOID : KillMessages.KillReason.OTHER, game.getArena()));
        } else if (plugin.getGameQueue().inQueue(uuid)) {
            plugin.getGameQueue().removePlayer(evt.getEntity());
            evt.getEntity().sendMessage(SkyTrans.get(TransKey.QUEUE_DEATH));
        }
    }

    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        UUID uuid = info.getPlayer().getUniqueId();
        lastHitName.remove(uuid);
        lastHitUuid.remove(uuid);
    }

    @Override
    public String getKillerName(UUID uuid) {
        return lastHitName.get(uuid);
    }

    @Override
    public UUID getKillerUuid(UUID uuid) {
        return lastHitUuid.get(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent evt) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(evt.getPlayer());
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

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import java.util.HashSet;
import java.util.Set;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.game.KillBroadcaster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author daboross
 */
public class DeathListener implements Listener {

    private final SkyWarsPlugin plugin;
    private Set<String> playersAwaitingRespawn = new HashSet<String>();

    public DeathListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        Integer id = plugin.getCurrentGames().getGameID(evt.getEntity().getName());
        if (id != null) {
            String name = evt.getEntity().getName().toLowerCase();
            evt.setDeathMessage(KillBroadcaster.getMessage(evt.getEntity()));
            plugin.getGameHandler().removePlayerFromGame(name, false);
            playersAwaitingRespawn.add(name);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent evt) {
        String name = evt.getPlayer().getName().toLowerCase();
        if (playersAwaitingRespawn.remove(name)) {
            evt.setRespawnLocation(plugin.getLocationStore().getLobbyPosition().toLocation());
        }
    }
}

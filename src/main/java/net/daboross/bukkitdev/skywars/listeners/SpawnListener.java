/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author daboross
 */
public class SpawnListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent evt) {
        String world = evt.getLocation().getWorld().getName();
        if (world.equalsIgnoreCase("SkyWarsArenaWorld") || world.equalsIgnoreCase("SkyworldWarriors")) {
            evt.setCancelled(true);
        }
    }
}

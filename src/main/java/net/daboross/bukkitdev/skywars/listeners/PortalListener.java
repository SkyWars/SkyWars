/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author daboross
 */
public class PortalListener implements Listener {

    private final SkyWarsPlugin plugin;

    public PortalListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Location location = evt.getTo();
        for (SkyLocation loc : plugin.getLocationStore().getPortals()) {
            if (loc.isNear(location)) {
                Player p = evt.getPlayer();
                String name = p.getName().toLowerCase();
                if (plugin.getCurrentGames().getGameID(name) == null && !plugin.getGameQueue().inQueue(name)) {
                    p.sendMessage(Messages.Join.CONFIRMATION);
                    plugin.getGameQueue().queuePlayer(name);
                }
            }
        }
    }
}

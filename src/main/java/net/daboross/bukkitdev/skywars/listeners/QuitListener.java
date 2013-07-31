/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author daboross
 */
public class QuitListener implements Listener {

    private final SkyWarsPlugin plugin;

    public QuitListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        String name = evt.getPlayer().getName();
        plugin.getGameHandler().removePlayerFromGame(name, true, true);
        plugin.getGameQueue().removePlayer(name);
    }
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author daboross
 */
public class CommandListener implements Listener {

    private final SkyWarsPlugin plugin;

    public CommandListener(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent evt) {
        if (!evt.getMessage().substring(1, 3).equalsIgnoreCase("sw")) {
            if (plugin.getCurrentGames().getGameID(evt.getPlayer().getName()) != null) {
                evt.setCancelled(true);
            }
        }
    }
}

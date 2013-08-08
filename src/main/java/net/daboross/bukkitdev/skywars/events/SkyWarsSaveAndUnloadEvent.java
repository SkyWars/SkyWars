/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.events;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class SkyWarsSaveAndUnloadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final SkyWarsPlugin plugin;

    public SkyWarsSaveAndUnloadEvent(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public SkyWarsPlugin getPlugin() {
        return plugin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

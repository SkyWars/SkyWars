/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author daboross
 */
public class PlayerLeaveGameEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();
    private final int id;

    public PlayerLeaveGameEvent(int id, Player who) {
        super(who);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

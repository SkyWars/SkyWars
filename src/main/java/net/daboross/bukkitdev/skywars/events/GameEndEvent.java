/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class GameEndEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String[] players;
    private final int id;

    public GameEndEvent(String[] players, int id) {
        if (players == null || players.length != 4) {
            throw new IllegalArgumentException();
        }
        this.players = players;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String[] getPlayers() {
        return players;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

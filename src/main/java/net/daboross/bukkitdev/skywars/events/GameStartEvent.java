/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author daboross
 */
public class GameStartEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String[] names;
    private final Player[] players = new Player[4];
    private int id;

    public GameStartEvent(String[] names) {
        if (names == null || names.length != 4) {
            throw new IllegalArgumentException();
        }
        this.names = names;
        for (int i = 0; i < 4; i++) {
            Player p = Bukkit.getPlayer(names[i]);
            if (p == null) {
                throw new IllegalArgumentException();
            }
            players[i] = p;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Player[] getPlayers() {
        return players;
    }

    public String[] getNames() {
        return names;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

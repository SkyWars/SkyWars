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
public class GameEndEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String[] playerNames;
    private final Player[] players = new Player[4];
    private final int id;
    private final boolean broadcast;

    public GameEndEvent(String[] names, int id, boolean broadcast) {
        if (names == null || names.length != 4) {
            throw new IllegalArgumentException();
        }
        this.playerNames = names;
        this.id = id;
        this.broadcast = broadcast;
        for (int i = 0; i < 4; i++) {
            if (names[i] != null) {
                Player p = Bukkit.getPlayer(names[i]);
                if (p == null) {
                    throw new IllegalArgumentException();
                }
                players[i] = p;
            }
        }
    }

    public int getId() {
        return id;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public Player[] getPlayers() {
        return players;
    }

    public boolean shouldBroadcast() {
        return broadcast;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

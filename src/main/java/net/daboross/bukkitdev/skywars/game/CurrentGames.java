/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import java.util.HashMap;
import java.util.Map;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;

/**
 *
 * @author daboross
 */
public class CurrentGames {

    private final Map<String, Integer> currentlyInGame = new HashMap<String, Integer>();

    public void setGameID(String player, int gameID) {
        currentlyInGame.put(player.toLowerCase(), Integer.valueOf(gameID));
    }

    public Integer getGameID(String player) {
        return currentlyInGame.get(player.toLowerCase());
    }

    public void removePlayer(String player) {
        currentlyInGame.remove(player.toLowerCase());
    }
}

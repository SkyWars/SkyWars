/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author daboross
 */
public class GameIdHandler {

    private final Map<Integer, String[]> currentGames = new HashMap<Integer, String[]>();

    public String[] getPlayers(int id) {
        return currentGames.get(id);
    }

    public int addNewGame(String[] players) {
        if (players == null || players.length != 4) {
            throw new IllegalArgumentException();
        }
        int id = 0;
        while (currentGames.containsKey(id)) {
            id++;
        }
        currentGames.put(id, players);
        return id;
    }

    public void gameFinished(int id) {
        currentGames.remove(id);
    }
}

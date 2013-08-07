/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import java.util.HashMap;
import java.util.Map;
import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class CurrentGames implements Listener {

    private final Map<String, Integer> currentlyInGame = new HashMap<String, Integer>();

    private void setGameID(String player, int gameID) {
        currentlyInGame.put(player.toLowerCase(), Integer.valueOf(gameID));
    }

    public Integer getGameID(String player) {
        return currentlyInGame.get(player.toLowerCase());
    }

    @EventHandler
    public void onPlayerLeaveGame(PlayerLeaveGameEvent evt) {
        currentlyInGame.remove(evt.getPlayer().getName().toLowerCase());
    }

    @EventHandler
    public void onGameStart(GameStartEvent evt) {
        int id = evt.getId();
        for (String name : evt.getNames()) {
            setGameID(name, id);
        }
    }
}

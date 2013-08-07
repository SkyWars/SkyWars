/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.daboross.bukkitdev.skywars.events.GameEndEvent;
import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class GameIdHandler implements Listener {

    private final Map<Integer, String[]> currentGames = new HashMap<Integer, String[]>();
    private final List<Integer> currentIds = new ArrayList<Integer>();

    public String[] getPlayers(int id) {
        return currentGames.get(id);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameStart(GameStartEvent evt) {
        int id = 0;
        while (currentGames.containsKey(id)) {
            id++;
        }
        currentGames.put(id, evt.getNames());
        currentIds.add(id);
        evt.setId(id);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameEnd(GameEndEvent evt) {
        currentGames.remove(evt.getId());
        currentIds.remove(evt.getId());
    }

    public List<Integer> getCurrentIds() {
        return Collections.unmodifiableList(currentIds);
    }
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;

/**
 *
 * @author daboross
 */
public class GameQueue {

    private final SkyWarsPlugin plugin;
    private final List<String> currentlyQueued;

    public GameQueue(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.currentlyQueued = new ArrayList<String>(4);
    }

    public boolean inQueue(String player) {
        return currentlyQueued.contains(player.toLowerCase());
    }

    public void queuePlayer(String player) {
        player = player.toLowerCase();
        if (!currentlyQueued.contains(player)) {
            currentlyQueued.add(player);
        }
        if (currentlyQueued.size() == 4) {
            plugin.getGameHandler().startNewGame();
            currentlyQueued.clear();
        }
    }

    public void removePlayer(String player) {
        currentlyQueued.remove(player.toLowerCase());
    }

    public String[] getQueueCopy() {
        return currentlyQueued.toArray(new String[currentlyQueued.size()]);
    }
}

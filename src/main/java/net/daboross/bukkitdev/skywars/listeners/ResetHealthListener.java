/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.events.PlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author daboross
 */
public class ResetHealthListener implements Listener {

    @EventHandler
    public void onGameStart(GameStartEvent evt) {
        for (Player p : evt.getPlayers()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(p.getMaxHealth());
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveGameEvent evt) {
        Player p = evt.getPlayer();
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(p.getMaxHealth());
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.setFoodLevel(20);
    }
}

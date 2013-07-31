/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author daboross
 */
public class GameHandler {

    private final SkyWarsPlugin plugin;

    public GameHandler(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startNewGame() {
        String[] queued = plugin.getGameQueue().getQueueCopy();
        if (queued.length != 4) {
            throw new IllegalStateException("Queue size is not 4");
        }
        CurrentGames cg = plugin.getCurrentGames();
        int gameID = plugin.getIdHandler().addNewGame(queued);
        Location[] spawnLocations = plugin.getWorldHandler().createArena(gameID);
        StringBuilder players = new StringBuilder(ColorList.NAME);
        for (int i = 0; i < 4; i++) {
            String name = queued[i];
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                throw new IllegalArgumentException("One or more of the players is not online");
            }
            cg.setGameID(name, gameID);
            player.teleport(spawnLocations[i]);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(player.getMaxHealth());
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setFoodLevel(20);
            if (i == 4) {
                players.append(ColorList.BROADCAST).append(" and ").append(ColorList.NAME).append(player.getName());
            } else if (i > 0) {
                players.append(ColorList.BROADCAST).append(", ").append(ColorList.NAME).append(player.getName());
            } else {
                players.append(ColorList.NAME).append(player.getName());
            }
        }
        Bukkit.broadcastMessage(String.format(Messages.GAME_STARTING, players.toString()));
    }

    public void endGame(int id, boolean broadcast) {
        CurrentGames cg = plugin.getCurrentGames();
        GameIdHandler idh = plugin.getIdHandler();
        String[] players = idh.getPlayers(id);
        idh.gameFinished(id);
        Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
        String winner = null;
        for (String playerName : players) {
            if (playerName != null) {
                Player player = Bukkit.getPlayerExact(playerName);
                if (player == null) {
                    throw new IllegalStateException("Player in game that isn't online");
                }
                cg.removePlayer(playerName);
                player.teleport(lobby);
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(player.getMaxHealth());
                player.getInventory().clear();
                player.getInventory().setArmorContents(new ItemStack[4]);
                player.setFoodLevel(20);
                if (winner == null) {
                    winner = playerName;
                } else {
                    winner += ", " + playerName;
                }
            }
        }
        if (broadcast) {
            final String message;
            if (winner == null) {
                message = Messages.NONE_WON;
            } else if (winner.contains(", ")) {
                message = String.format(Messages.MULTI_WON, winner);
            } else {
                message = String.format(Messages.SINGLE_WON, winner);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage(message);
                }
            }.runTask(plugin);
        }
    }

    public void removePlayerFromGame(String playerName, boolean teleport, boolean broadcast) {
        playerName = playerName.toLowerCase();
        CurrentGames cg = plugin.getCurrentGames();
        Integer id = cg.getGameID(playerName);
        if (id == null) {
            return;
        }
        GameIdHandler idh = plugin.getIdHandler();
        cg.removePlayer(playerName);
        String[] players = idh.getPlayers(id);
        int playersLeft = 0;
        for (int i = 0; i < 4; i++) {
            if (players[i] != null) {
                if (players[i].equalsIgnoreCase(playerName)) {
                    players[i] = null;
                } else {
                    playersLeft++;
                }
            }
        }
        if (teleport || broadcast) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (teleport) {
                Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
                player.teleport(lobby);
            }
            if (broadcast) {
                EntityDamageEvent ede = player.getLastDamageCause();
                Entity damager = null;
                if (ede instanceof EntityDamageByEntityEvent) {
                    damager = ((EntityDamageByEntityEvent) ede).getDamager();
                }
                if (damager == null) {
                    Bukkit.broadcastMessage(String.format(Messages.FORFEITED, player.getName()));
                } else {
                    String damagerName = (damager instanceof LivingEntity) ? ((LivingEntity) damager).getCustomName() : damager.getType().getName();
                    Bukkit.broadcastMessage(String.format(Messages.FORFEITED_BY, damagerName, playerName));
                }
            }
        }
        if (playersLeft < 2) {
            endGame(id, true);
        }
    }
}

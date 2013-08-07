/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.scoreboards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.events.GameEndEvent;
import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author daboross
 */
public class KillScoreboardManager implements Listener {

    private final ScoreboardManager manager;
    private final SkyWarsPlugin plugin;
    private final File saveFile;
    private final YamlConfiguration save;
    private final Map<Integer, Scoreboard> gameScoreboards = new HashMap<Integer, Scoreboard>();

    public KillScoreboardManager(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getServer().getScoreboardManager();
        File dataFolder = plugin.getDataFolder();
        saveFile = new File(dataFolder, "kills.yml");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error creating kills.yml", ex);
            }
        }
        save = YamlConfiguration.loadConfiguration(saveFile);
    }

    private Scoreboard createAndAddScoreboard(String[] playersToTrack) {
        Scoreboard scoreboard = this.manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Kills this game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (String player : playersToTrack) {
            int kills = save.getInt(player.toLowerCase());
            Score score = objective.getScore(Bukkit.getOfflinePlayer(player));
            score.setScore(kills);
        }
        return scoreboard;
    }

    @EventHandler
    public void onStart(GameStartEvent evt) {
        Scoreboard gameBoard = createAndAddScoreboard(evt.getNames());
        for (Player p : evt.getPlayers()) {
            p.setScoreboard(gameBoard);
        }
        gameScoreboards.put(evt.getId(), gameBoard);
    }

    @EventHandler
    public void onEnd(GameEndEvent evt) {
        Scoreboard gameBoard = gameScoreboards.remove(evt.getId());
        gameBoard.getObjective("Kills this game").unregister();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        Player killer = evt.getEntity().getKiller();
        if (killer != null) {
            upKills(killer.getName().toLowerCase());
        }
    }

    private void upKills(String lowerCaseName) {
        save.set(lowerCaseName, save.getInt(lowerCaseName) + 1);
    }
}

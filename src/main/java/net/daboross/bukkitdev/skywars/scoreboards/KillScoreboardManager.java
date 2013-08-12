/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.scoreboards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.events.PrepairGameEndEvent;
import net.daboross.bukkitdev.skywars.events.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.events.UnloadListener;
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
public class KillScoreboardManager implements Listener, UnloadListener {

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

    public void save() {
        try {
            save.save(saveFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error saving kills.yml", ex);
        }
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
    public void onStart(PrepairGameStartEvent evt) {
        Scoreboard gameBoard = createAndAddScoreboard(evt.getNames());
        for (Player p : evt.getPlayers()) {
            p.setScoreboard(gameBoard);
        }
        gameScoreboards.put(evt.getId(), gameBoard);
    }

    @EventHandler
    public void onEnd(PrepairGameEndEvent evt) {
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

    @Override
    public void saveAndUnload(SkyWarsPlugin plugin) {
        save();
    }

    private void upKills(String lowerCaseName) {
        save.set(lowerCaseName, save.getInt(lowerCaseName) + 1);
    }
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.scoreboards;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.events.GameEndEvent;
import net.daboross.bukkitdev.skywars.events.GameStartEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author daboross
 */
public class KillScoreboardManager implements Listener {

    private final ScoreboardManager manager;
    private final Scoreboard globalKill;
    private final SkyWarsPlugin plugin;
    private final File saveFile;
    private final YamlConfiguration save;

    public KillScoreboardManager(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getServer().getScoreboardManager();
        globalKill = this.manager.getNewScoreboard();
        Objective objective = globalKill.registerNewObjective("TotalKill", "playerKillCount");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        saveFile = new File(plugin.getDataFolder(), "kills.yml");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error creating kills.yml", ex);
            }
        }
        save = YamlConfiguration.loadConfiguration(saveFile);
    }

    @EventHandler
    public void onStart(GameStartEvent evt) {
    }

    @EventHandler
    public void onEnd(GameEndEvent evt) {
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

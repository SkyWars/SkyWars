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
package net.daboross.bukkitdev.skywars;

import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.commands.MainCommand;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import lombok.Getter;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.location.SkyLocationStore;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.commands.SetupCommand;
import net.daboross.bukkitdev.skywars.config.SkyWarsConfiguration;
import net.daboross.bukkitdev.skywars.events.GameEventDistributor;
import net.daboross.bukkitdev.skywars.game.CurrentGames;
import net.daboross.bukkitdev.skywars.game.GameHandler;
import net.daboross.bukkitdev.skywars.game.GameIDHandler;
import net.daboross.bukkitdev.skywars.game.GameQueue;
import net.daboross.bukkitdev.skywars.listeners.CommandListener;
import net.daboross.bukkitdev.skywars.listeners.DeathStorage;
import net.daboross.bukkitdev.skywars.game.reactors.GameBroadcaster;
import net.daboross.bukkitdev.skywars.game.reactors.InventorySave;
import net.daboross.bukkitdev.skywars.listeners.PortalListener;
import net.daboross.bukkitdev.skywars.listeners.QuitListener;
import net.daboross.bukkitdev.skywars.game.reactors.ResetHealth;
import net.daboross.bukkitdev.skywars.listeners.BuildingLimiter;
import net.daboross.bukkitdev.skywars.listeners.MobSpawnDisable;
import net.daboross.bukkitdev.skywars.listeners.PointStorageChatListener;
import net.daboross.bukkitdev.skywars.listeners.SpawnListener;
import net.daboross.bukkitdev.skywars.points.PointStorage;
import net.daboross.bukkitdev.skywars.scoreboards.TeamScoreboardListener;
import net.daboross.bukkitdev.skywars.storage.LocationStore;
import net.daboross.bukkitdev.skywars.world.SkyWorldHandler;
import net.daboross.bukkitdev.skywars.world.Statics;
import net.daboross.bukkitdev.skywars.world.WorldUnzipper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.MetricsLite;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class SkyWarsPlugin extends JavaPlugin implements SkyWars {

    @Getter
    private SkyConfiguration configuration;
    @Getter
    private SkyLocationStore locationStore;
    @Getter
    private GameQueue gameQueue;
    @Getter
    private CurrentGames currentGameTracker;
    @Getter
    private SkyGameHandler gameHandler;
    @Getter
    private GameIDHandler iDHandler;
    @Getter
    private SkyWorldHandler worldHandler;
    @Getter
    private DeathStorage attackerStorage;
    @Getter
    private GameBroadcaster broadcaster;
    @Getter
    private ResetHealth resetHealth;
    @Getter
    private GameEventDistributor distributor;
    @Getter
    private InventorySave inventorySave;
    @Getter
    private PointStorage points;
    @Getter
    private PointStorageChatListener chatListener;
    @Getter
    private TeamScoreboardListener teamListener;
    private boolean enabledCorrectly = false;

    @Override
    public void onLoad() {
        SkyStatic.setPluginName(this.getDescription().getName());
        SkyStatic.setVersion(this.getDescription().getVersion());
        SkyStatic.setLogger(this.getLogger());
    }

    @Override
    public void onEnable() {
        try {
            startPlugin();
            metrics();
        } catch (Throwable ex) {
            getLogger().log(Level.SEVERE, "Startup failed", ex);
            enabledCorrectly = false;
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void startPlugin() throws StartupFailedException {
        try {
            configuration = new SkyWarsConfiguration(this);
        } catch (IOException | InvalidConfigurationException | SkyConfigurationException ex) {
            throw new StartupFailedException("Failed to load configuration", ex);
        }
        for (SkyArena arena : configuration.getEnabledArenas()) {
            if (arena.getBoundaries().getOrigin().world.equalsIgnoreCase(Statics.BASE_WORLD_NAME)) {
                new WorldUnzipper().doWorldUnzip(getLogger());
                break;
            }
        }
        currentGameTracker = new CurrentGames();
        iDHandler = new GameIDHandler();
        broadcaster = new GameBroadcaster();
        worldHandler = new SkyWorldHandler(this);
        inventorySave = new InventorySave(this);
        resetHealth = new ResetHealth(this);
        locationStore = new LocationStore(this);
        gameQueue = new GameQueue(this);
        gameHandler = new GameHandler(this);
        attackerStorage = new DeathStorage(this);
        distributor = new GameEventDistributor(this);
        teamListener = new TeamScoreboardListener();
        if (configuration.isEnablePoints()) {
            points = new PointStorage(this);
            chatListener = new PointStorageChatListener(this);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                worldHandler.create();
                worldHandler.findAndLoadRequiredWorlds();
            }
        }.runTask(this);
        new PermissionHandler("skywars").setupPermissions();
        setupCommand();
        PluginManager pm = getServer().getPluginManager();
        registerListeners(pm, new SpawnListener(), attackerStorage,
                new QuitListener(this), new PortalListener(this),
                new CommandListener(this), new BuildingLimiter(this),
                new MobSpawnDisable(), chatListener);
        enabledCorrectly = true;
    }

    private void registerListeners(PluginManager pm, Listener... listeners) {
        for (Listener l : listeners) {
            if (l != null) {
                pm.registerEvents(l, this);
            }
        }
    }

    @Override
    public void onDisable() {
        if (enabledCorrectly) {
            locationStore.save();
            iDHandler.saveAndUnload(this);
            if (points != null) {
                try {
                    points.save();
                } catch (IOException ex) {
                    getLogger().log(Level.WARNING, "Failed to save points", ex);
                }
            }
            SkyStatic.setLogger(getServer().getLogger());
            getLogger().log(Level.INFO, "SkyWars disabled successfully");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!enabledCorrectly) {
            sender.sendMessage(ColorList.ERR + "Not fully enabled.");
        } else {
            sender.sendMessage(ColorList.ERR + "SkyWars has no clue what " + cmd.getName() + " is.");
        }
        return true;
    }

    private void setupCommand() {
        MainCommand main = new MainCommand(this);
        SetupCommand setup = new SetupCommand(this);
        for (String commandName : getDescription().getCommands().keySet()) {
            if (commandName.toLowerCase(Locale.ENGLISH).endsWith("setup")) {
                setup.latchOnto(getCommand(commandName));
            } else {
                main.latchOnto(getCommand(commandName));
            }
        }
    }

    private void metrics() {
        try {
            MetricsLite metrics;
            try {
                metrics = new MetricsLite(this);
            } catch (IOException ex) {
                return;
            }
            metrics.start();
        } catch (Throwable t) {
            // We just won't do metrics now
        }
    }
}

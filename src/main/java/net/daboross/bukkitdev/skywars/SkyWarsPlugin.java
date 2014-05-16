/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.kits.SkyKits;
import net.daboross.bukkitdev.skywars.api.location.SkyLocationStore;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.SkyTranslations;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.MainCommand;
import net.daboross.bukkitdev.skywars.commands.SetupCommand;
import net.daboross.bukkitdev.skywars.config.SkyWarsConfiguration;
import net.daboross.bukkitdev.skywars.config.TranslationsConfiguration;
import net.daboross.bukkitdev.skywars.economy.EconomyFailedException;
import net.daboross.bukkitdev.skywars.economy.SkyEconomyGameRewards;
import net.daboross.bukkitdev.skywars.economy.SkyEconomyHook;
import net.daboross.bukkitdev.skywars.events.GameEventDistributor;
import net.daboross.bukkitdev.skywars.events.listeners.GameBroadcaster;
import net.daboross.bukkitdev.skywars.events.listeners.InventorySaveListener;
import net.daboross.bukkitdev.skywars.events.listeners.KitApplyListener;
import net.daboross.bukkitdev.skywars.events.listeners.KitQueueNotifier;
import net.daboross.bukkitdev.skywars.events.listeners.ResetHealthListener;
import net.daboross.bukkitdev.skywars.game.CurrentGames;
import net.daboross.bukkitdev.skywars.game.GameHandler;
import net.daboross.bukkitdev.skywars.game.GameIDHandler;
import net.daboross.bukkitdev.skywars.game.GameQueue;
import net.daboross.bukkitdev.skywars.kits.SkyKitConfiguration;
import net.daboross.bukkitdev.skywars.listeners.AttackerStorageListener;
import net.daboross.bukkitdev.skywars.listeners.BuildingLimiter;
import net.daboross.bukkitdev.skywars.listeners.CommandWhitelistListener;
import net.daboross.bukkitdev.skywars.listeners.MobSpawnDisable;
import net.daboross.bukkitdev.skywars.listeners.PortalListener;
import net.daboross.bukkitdev.skywars.listeners.QuitListener;
import net.daboross.bukkitdev.skywars.listeners.ScoreCacheOnJoinListener;
import net.daboross.bukkitdev.skywars.listeners.ScoreReplaceChatListener;
import net.daboross.bukkitdev.skywars.listeners.SpawnListener;
import net.daboross.bukkitdev.skywars.player.OnlineSkyPlayers;
import net.daboross.bukkitdev.skywars.score.ScoreStorage;
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

public class SkyWarsPlugin extends JavaPlugin implements SkyWars {

    private SkyTranslations translations;
    private SkyConfiguration configuration;
    private SkyLocationStore locationStore;
    private SkyGameHandler gameHandler;
    private SkyWorldHandler worldHandler;
    private SkyEconomyHook economyHook;
    private SkyEconomyGameRewards ecoRewards;
    private SkyKits kits;
    private GameQueue gameQueue;
    private CurrentGames currentGameTracker;
    private GameIDHandler idHandler;
    private GameBroadcaster broadcaster;
    private GameEventDistributor distributor;
    private ScoreStorage score;
    private KitQueueNotifier kitQueueNotifier;

    private OnlineSkyPlayers inGame;
    private TeamScoreboardListener teamListener;
    private AttackerStorageListener attackerStorage;
    // Info listeners
    private ResetHealthListener resetHealth;
    private KitApplyListener kitApplyListener;
    private InventorySaveListener inventorySaveListener;
    // Bukkit listeners
    private ScoreReplaceChatListener chatListener;
    private ScoreCacheOnJoinListener joinListener;
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
        SkyStatic.initializeSupportsUuids();
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
        try {
            translations = new TranslationsConfiguration(this);
        } catch (SkyConfigurationException ex) {
            throw new StartupFailedException("Failed to load translations", ex);
        }
        SkyTrans.setInstance(translations);
        currentGameTracker = new CurrentGames();
        idHandler = new GameIDHandler();
        broadcaster = new GameBroadcaster();
        worldHandler = new SkyWorldHandler(this);
        inventorySaveListener = new InventorySaveListener(this);
        resetHealth = new ResetHealthListener();
        locationStore = new LocationStore(this);
        gameQueue = new GameQueue(this);
        gameHandler = new GameHandler(this);
        attackerStorage = new AttackerStorageListener(this);
        distributor = new GameEventDistributor(this);
        teamListener = new TeamScoreboardListener();
        inGame = new OnlineSkyPlayers(this);
        if (configuration.isEnableScore()) {
            score = new ScoreStorage(this);
            chatListener = new ScoreReplaceChatListener(this);
            joinListener = new ScoreCacheOnJoinListener(this);
        }
        if (configuration.isEconomyEnabled()) {
            SkyStatic.debug("Enabling economy support");
            try {
                economyHook = new SkyEconomyHook(this);
                ecoRewards = new SkyEconomyGameRewards(this);
            } catch (EconomyFailedException ex) {
                getLogger().log(Level.WARNING, "{0}. Couldn't enable economy hook.", ex.getMessage());
            }
        }
        kits = new SkyKitConfiguration(this);
        kitQueueNotifier = new KitQueueNotifier(this);
        kitApplyListener = new KitApplyListener(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                worldHandler.create();
                worldHandler.findAndLoadRequiredWorlds();
            }
        }.runTask(this);
        new PermissionHandler().setupPermissions();
        setupCommand();
        PluginManager pm = getServer().getPluginManager();
        registerListeners(pm, new SpawnListener(), attackerStorage,
                new QuitListener(this), new PortalListener(this),
                new CommandWhitelistListener(this), new BuildingLimiter(this),
                new MobSpawnDisable(), chatListener, joinListener);
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
            idHandler.saveAndUnload(this);
            if (score != null) {
                try {
                    score.save();
                } catch (IOException ex) {
                    getLogger().log(Level.WARNING, "Failed to save score", ex);
                }
            }
            getLogger().log(Level.INFO, "Unloading arena world - without saving");
            worldHandler.destroyArenaWorld();
            SkyStatic.setLogger(null);
            getLogger().log(Level.INFO, "SkyWars disabled successfully");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!enabledCorrectly) {
            sender.sendMessage(SkyTrans.get(TransKey.NOT_FULLY_ENABLED));
        } else {
            sender.sendMessage(SkyTrans.get(TransKey.NO_CLUE_COMMAND, cmd.getName()));
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
        } catch (Throwable ignored) {
            // We just won't do metrics now
        }
    }

    @Override
    public boolean reloadTranslations() {
        SkyTranslations tempTrans;
        try {
            tempTrans = new TranslationsConfiguration(this);
        } catch (SkyConfigurationException | RuntimeException ex) {
            getLogger().log(Level.WARNING, "Failed to reload translations. Just using older version for now.", ex);
            return false;
        }
        translations = tempTrans;
        SkyTrans.setInstance(tempTrans);
        return true;
    }

    @Override
    public SkyTranslations getTranslations() {
        return translations;
    }

    @Override
    public SkyConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public SkyLocationStore getLocationStore() {
        return locationStore;
    }

    @Override
    public GameQueue getGameQueue() {
        return gameQueue;
    }

    @Override
    public CurrentGames getCurrentGameTracker() {
        return currentGameTracker;
    }

    @Override
    public SkyGameHandler getGameHandler() {
        return gameHandler;
    }

    @Override
    public GameIDHandler getIDHandler() {
        return idHandler;
    }

    @Override
    public AttackerStorageListener getAttackerStorage() {
        return attackerStorage;
    }

    @Override
    public ScoreStorage getScore() {
        return score;
    }

    @Override
    public SkyEconomyHook getEconomyHook() {
        return economyHook;
    }

    @Override
    public SkyKits getKits() {
        return kits;
    }

    @Override
    public OnlineSkyPlayers getPlayers() {
        return inGame;
    }

    public SkyWorldHandler getWorldHandler() {
        return worldHandler;
    }

    public GameBroadcaster getBroadcaster() {
        return broadcaster;
    }

    public ResetHealthListener getResetHealth() {
        return resetHealth;
    }

    public GameEventDistributor getDistributor() {
        return distributor;
    }

    public InventorySaveListener getInventorySaveListener() {
        return inventorySaveListener;
    }

    public SkyEconomyGameRewards getEcoRewards() {
        return ecoRewards;
    }

    public TeamScoreboardListener getTeamListener() {
        return teamListener;
    }

    public KitQueueNotifier getKitQueueNotifier() {
        return kitQueueNotifier;
    }

    public KitApplyListener getKitApplyListener() {
        return kitApplyListener;
    }
}

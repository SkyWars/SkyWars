/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.game.SkyGameHandler;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitGui;
import net.daboross.bukkitdev.skywars.api.kits.SkyKits;
import net.daboross.bukkitdev.skywars.api.location.SkyLocationStore;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.SkyTranslations;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.MainCommand;
import net.daboross.bukkitdev.skywars.commands.SetupCommand;
import net.daboross.bukkitdev.skywars.config.RandomChestConfiguration;
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
import net.daboross.bukkitdev.skywars.events.listeners.SignListener;
import net.daboross.bukkitdev.skywars.game.CurrentGames;
import net.daboross.bukkitdev.skywars.game.GameHandler;
import net.daboross.bukkitdev.skywars.game.GameIDHandler;
import net.daboross.bukkitdev.skywars.game.GameQueue;
import net.daboross.bukkitdev.skywars.kits.KitGuiManager;
import net.daboross.bukkitdev.skywars.kits.SkyKitConfiguration;
import net.daboross.bukkitdev.skywars.libraries.pluginstatistics.PluginStatistics;
import net.daboross.bukkitdev.skywars.listeners.AttackerStorageListener;
import net.daboross.bukkitdev.skywars.listeners.BuildingLimiter;
import net.daboross.bukkitdev.skywars.listeners.CommandWhitelistListener;
import net.daboross.bukkitdev.skywars.listeners.KitGuiListener;
import net.daboross.bukkitdev.skywars.listeners.MobSpawnDisable;
import net.daboross.bukkitdev.skywars.listeners.PlayerJoinInArenaWorldListener;
import net.daboross.bukkitdev.skywars.listeners.PlayerStateListener;
import net.daboross.bukkitdev.skywars.listeners.PortalListener;
import net.daboross.bukkitdev.skywars.listeners.ScoreReplaceChatListener;
import net.daboross.bukkitdev.skywars.player.OnlineSkyPlayers;
import net.daboross.bukkitdev.skywars.score.ScoreStorage;
import net.daboross.bukkitdev.skywars.scoreboards.TeamScoreboardListener;
import net.daboross.bukkitdev.skywars.storage.LocationStore;
import net.daboross.bukkitdev.skywars.world.SkyWorldHandler;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.MetricsLite;

public class SkyWarsPlugin extends JavaPlugin implements SkyWars {

    private SkyTranslations translations;
    private SkyConfiguration configuration;
    private RandomChestConfiguration chestConfiguration;
    private SkyLocationStore locationStore;
    private SkyGameHandler gameHandler;
    private SkyWorldHandler worldHandler;
    private SkyEconomyHook economyHook;
    private SkyEconomyGameRewards ecoRewards;
    private SkyKits kits;
    private SkyKitGui kitGui;
    private GameQueue gameQueue;
    private CurrentGames currentGameTracker;
    private GameIDHandler idHandler;
    private GameBroadcaster broadcaster;
    private GameEventDistributor distributor;
    private ScoreStorage score;
    private KitQueueNotifier kitQueueNotifier;
    private SignListener signListener;

    private OnlineSkyPlayers inGame;
    private TeamScoreboardListener teamListener;
    private AttackerStorageListener attackerStorage;
    // Info listeners
    private ResetHealthListener resetHealth;
    private KitApplyListener kitApplyListener;
    private InventorySaveListener inventorySaveListener;
    // Bukkit listeners
    private ScoreReplaceChatListener chatListener;
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
            pluginStatistics();
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
        if (!configuration.isSkipUuidCheck() && !supportsUuids()) {
            getLogger().log(Level.SEVERE, "Warning! You are running a CraftBukkit version that is not supported by SkyWars v" + SkyStatic.getVersion());
            getLogger().log(Level.SEVERE, "Please update to at least CraftBukkit version 1.7.8, or the equivilant for your server software.");
            getLogger().log(Level.SEVERE, "If you wish to ignore this, and run SkyWars anyways, set 'skip-uuid-version-check' to true in plugins/SkyWars/main-config.yml");
            getLogger().log(Level.SEVERE, "Download SkyWars v1.4.4 if you want to run on an older version of Minecraft.");
            throw new StartupFailedException("See above");
        }
        try {
            translations = new TranslationsConfiguration(this);
        } catch (SkyConfigurationException ex) {
            throw new StartupFailedException("Failed to load translations", ex);
        }
        SkyTrans.setInstance(translations);
        currentGameTracker = new CurrentGames();
        idHandler = new GameIDHandler();
        broadcaster = new GameBroadcaster(this);
        worldHandler = new SkyWorldHandler(this);
        inventorySaveListener = new InventorySaveListener(this);
        resetHealth = new ResetHealthListener(this);
        try {
            locationStore = new LocationStore(this);
        } catch (SkyConfigurationException ex) {
            throw new StartupFailedException("Failed to load locations", ex);
        }
        gameQueue = new GameQueue(this);
        gameHandler = new GameHandler(this);
        attackerStorage = new AttackerStorageListener(this);
        distributor = new GameEventDistributor(this);
        teamListener = new TeamScoreboardListener();
        inGame = new OnlineSkyPlayers(this);
        signListener = new SignListener(this);
        if (configuration.isEnableScore()) {
            score = new ScoreStorage(this);
            chatListener = new ScoreReplaceChatListener(this);
        }
        // For supporting /reload or plugin manager reloading.
        for (Player online : getServer().getOnlinePlayers()) {
            inGame.loadPlayer(online);
        }
        if (configuration.isEconomyEnabled()) {
            SkyStatic.debug("Enabling economy support");
            try {
                economyHook = new SkyEconomyHook(this);
                ecoRewards = new SkyEconomyGameRewards(this);
            } catch (EconomyFailedException ex) {
                getLogger().log(Level.WARNING, "{0}. Could not enable economy hook.", ex.getMessage());
            }
        }
        try {
            chestConfiguration = new RandomChestConfiguration(this);
        } catch (IOException | InvalidConfigurationException | SkyConfigurationException ex) {
            throw new StartupFailedException("Failed to load chest configuration", ex);
        }
        try {
            kits = new SkyKitConfiguration(this);
        } catch (IOException | InvalidConfigurationException ex) {
            throw new StartupFailedException("Failed to load kit configuration", ex);
        }
        kitGui = new KitGuiManager(this);
        kitQueueNotifier = new KitQueueNotifier(this);
        kitApplyListener = new KitApplyListener(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                worldHandler.create();
                worldHandler.loadArenas();
            }
        }.runTask(this);
        new PermissionHandler().setupPermissions();
        setupCommand();
        PluginManager pm = getServer().getPluginManager();
        registerListeners(pm, attackerStorage, new PlayerStateListener(this),
                new PortalListener(this), new PlayerJoinInArenaWorldListener(this),
                new CommandWhitelistListener(this), new BuildingLimiter(this),
                new MobSpawnDisable(), new KitGuiListener(this), chatListener,
                signListener);
        enabledCorrectly = true;
    }

    private boolean supportsUuids() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.versionstring (or for pre-refactor, just org.bukkit.craftbukkit
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        if (version.equals("craftbukkit")) {
            return false;
        }
        String[] split = version.split("_");
        if (split.length != 3) {
            return backupUuidCheck();
        }
        int first, second, third;
        try {
            first = Integer.parseInt(split[0].substring(1)); // substring for v1 -> 1
            second = Integer.parseInt(split[1]);
            third = Integer.parseInt(split[2].substring(1)); // substring for R1 -> 1
        } catch (NumberFormatException ignored) {
            return backupUuidCheck();
        }
        // if we're on minecraft v2.X, the other version parts don't matter
        return first > 1 || second > 7 || (second == 7 && third >= 3);
    }

    private boolean backupUuidCheck() {
        try {
            Bukkit.class.getMethod("getPlayer", UUID.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
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
            // For better transparency when using /reload.
            for (UUID uuid : gameQueue.getCopy()) {
                Player player = getServer().getPlayer(uuid);
                gameQueue.removePlayer(player);
                player.sendMessage(SkyTrans.get(TransKey.CMD_LEAVE_REMOVED_FROM_QUEUE));
            }
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
        } catch (Throwable ex) {
            if (configuration.isDebug()) {
                getLogger().log(Level.WARNING, "Failed to start plugin metrics!", ex);
            }
        }
    }

    private void pluginStatistics() {
        try {
            if (!configuration.isReportPluginStatistics()) {
                return;
            }
            PluginStatistics statistics = new PluginStatistics(this, configuration.isDebug());
            statistics.start();
        } catch (Throwable ex) {
            if (configuration.isDebug()) {
                getLogger().log(Level.WARNING, "Failed to start plugin-statistics!", ex);
            }
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
    public InputStream getResourceAsStream(final String filename) throws IOException {
        // copied from JavaPlugin.java, and modified to actually give useful errors.
        Validate.notNull(filename, "Filename cannot be null");

        URL url = getClassLoader().getResource(filename);
        if (url == null) {
            throw new FileNotFoundException("No resource '" + filename + "' found.");
        }

        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        return connection.getInputStream();
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
    public RandomChestConfiguration getChestRandomizer() {
        return chestConfiguration;
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
    public SkyKitGui getKitGui() {
        return kitGui;
    }

    @Override
    public Path getArenaPath() {
        return getDataPath().resolve("arenas");
    }

    @Override
    public Path getDataPath() {
        return getDataFolder().toPath();
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

    public TeamScoreboardListener getTeamScoreBoardListener() {
        return teamListener;
    }

    public KitQueueNotifier getKitQueueNotifier() {
        return kitQueueNotifier;
    }

    public KitApplyListener getKitApplyListener() {
        return kitApplyListener;
    }

    public SignListener getSignListener() {
        return signListener;
    }
}

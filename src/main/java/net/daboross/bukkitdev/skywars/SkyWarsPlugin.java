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
import net.daboross.bukkitdev.skywars.listeners.PortalListener;
import net.daboross.bukkitdev.skywars.listeners.QuitListener;
import net.daboross.bukkitdev.skywars.game.reactors.ResetHealth;
import net.daboross.bukkitdev.skywars.listeners.BuildingLimiter;
import net.daboross.bukkitdev.skywars.listeners.MobSpawnDisable;
import net.daboross.bukkitdev.skywars.listeners.SpawnListener;
import net.daboross.bukkitdev.skywars.storage.LocationStore;
import net.daboross.bukkitdev.skywars.world.SkyWorldHandler;
import net.daboross.bukkitdev.skywars.world.Statics;
import net.daboross.bukkitdev.skywars.world.WorldUnzipper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    private boolean enabledCorrectly = false, enablingDone = false;

    @Override
    public void onLoad() {
        SkyStatic.setPluginName( this.getDescription().getName() );
        SkyStatic.setVersion( this.getDescription().getVersion() );
        SkyStatic.setLogger( this.getLogger() );
    }

    @Override
    public void onEnable() {
        setupMetrics();
        try {
            startPlugin();
            enablingDone = true;
        } catch ( Throwable ex ) {
            getLogger().log( Level.SEVERE, "Startup failed", ex );
            enabledCorrectly = false;
            enablingDone = true;
            getServer().getPluginManager().disablePlugin( this );
        }
    }

    private void startPlugin() {
        configuration = new SkyWarsConfiguration( this );
        for ( SkyArena arena : configuration.getEnabledArenas() ) {
            if ( arena.getBoundaries().getOrigin().world.equalsIgnoreCase( Statics.BASE_WORLD_NAME ) ) {
                new WorldUnzipper().doWorldUnzip( getLogger() );
                break;
            }
        }
        currentGameTracker = new CurrentGames();
        iDHandler = new GameIDHandler();
        worldHandler = new SkyWorldHandler( this );
        broadcaster = new GameBroadcaster();
        resetHealth = new ResetHealth( this );
        locationStore = new LocationStore( this );
        gameQueue = new GameQueue( this );
        gameHandler = new GameHandler( this );
        attackerStorage = new DeathStorage( this );
        distributor = new GameEventDistributor( this );
        new BukkitRunnable() {
            @Override
            public void run() {
                worldHandler.create();
                worldHandler.findAndLoadRequiredWorlds();
            }
        }.runTask( this );
        new PermissionHandler( "skywars" ).setupPermissions();
        setupCommand();
        PluginManager pm = getServer().getPluginManager();
        registerListeners( pm, new SpawnListener(), attackerStorage,
                new QuitListener( this ), new PortalListener( this ),
                new CommandListener( this ), new BuildingLimiter( this ),
                new MobSpawnDisable() );
        enabledCorrectly = true;
    }

    private void registerListeners( PluginManager pm, Listener... listeners ) {
        for ( Listener l : listeners ) {
            pm.registerEvents( l, this );
        }
    }

    @Override
    public void onDisable() {
        if ( enabledCorrectly ) {
            locationStore.save();
            iDHandler.saveAndUnload( this );
            getLogger().log( Level.INFO, "SkyWars disabled successfully" );
        } else {
            getLogger().log( Level.INFO, "SkyWars not disabling due to not being enabled successfully." );
        }
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
        if ( !enabledCorrectly ) {
            if ( !enablingDone ) {
                sender.sendMessage( ColorList.ERR + "Some absoultely evil error happened enabling SkyWars. Enabling isn't done." );
            } else {
                sender.sendMessage( ColorList.ERR + "SkyWars not enabled correctly. Check console for errors." );
            }
        } else {
            sender.sendMessage( ColorList.ERR + "SkyWars has no clue what " + cmd.getName() + " is." );
        }
        return true;
    }

    private void setupMetrics() {
        MetricsLite metrics;
        try {
            metrics = new MetricsLite( this );
        } catch ( IOException ex ) {
            getLogger().log( Level.WARNING, "Unable to create metrics: {0}", ex.toString() );
            return;
        }
        metrics.start();
    }

    private void setupCommand() {
        MainCommand main = new MainCommand( this );
        SetupCommand setup = new SetupCommand( this );
        for ( String commandName : getDescription().getCommands().keySet() ) {
            if ( commandName.toLowerCase( Locale.ENGLISH ).endsWith( "setup" ) ) {
                setup.latchOnto( getCommand( commandName ) );
            } else {
                main.latchOnto( getCommand( commandName ) );
            }
        }
    }
}

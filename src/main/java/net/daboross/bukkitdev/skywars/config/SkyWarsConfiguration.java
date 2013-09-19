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
package net.daboross.bukkitdev.skywars.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.StartupFailedException;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class SkyWarsConfiguration implements SkyConfiguration {

    private final SkyWars plugin;
    @Getter
    private File mainConfigFile;
    @Getter
    private File arenaFolder;
    @Getter
    private SkyArenaConfig parentArena;
    private List<SkyArenaConfig> enabledArenas;
    @Getter
    private ArenaOrder arenaOrder;
    @Getter
    private String messagePrefix;
    @Getter
    private boolean inventorySaveEnabled;
    private SkyFileConfig mainConfig;

    public SkyWarsConfiguration( SkyWars plugin ) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        load();
    }

    private void load() throws IOException, InvalidConfigurationException {
        if ( mainConfigFile == null ) {
            mainConfigFile = new File( plugin.getDataFolder(), Names.MAIN );
        }
        mainConfig = new SkyFileConfig( mainConfigFile, plugin.getLogger() );
        mainConfig.load();

        if ( arenaFolder == null ) {
            arenaFolder = new File( plugin.getDataFolder(), Names.ARENAS );
        }
        if ( !arenaFolder.exists() ) {
            boolean mkdirs = arenaFolder.mkdirs();
            if ( !mkdirs ) {
                throw new StartupFailedException( "Making directory " + arenaFolder.getAbsolutePath() + " failed" );
            }
        } else if ( !arenaFolder.isDirectory() ) {
            throw new StartupFailedException( "File " + arenaFolder.getAbsolutePath() + " exists but is not a directory" );
        }

        int version = mainConfig.getSetInt( Keys.VERSION, Defaults.VERSION );
        if ( version > 1 ) {
            throw new StartupFailedException( "Version '" + version + "' as listed under " + Keys.VERSION + " in file " + mainConfigFile.getAbsolutePath() + " is unknown." );
        }
        mainConfig.getConfig().set( Keys.VERSION, Defaults.VERSION );

        SkyStatic.setDebug( mainConfig.getSetBoolean( Keys.DEBUG, Defaults.DEBUG ) );

        arenaOrder = ArenaOrder.getOrder( mainConfig.getSetString( Keys.ARENA_ORDER, Defaults.ARENA_ORDER.toString() ) );
        if ( arenaOrder == null ) {
            throw new StartupFailedException( "Invalid ArenaOrder '" + arenaOrder + "' found under " + Keys.ARENA_ORDER + " in file " + mainConfigFile.getAbsolutePath() + ". Valid values: " + Arrays.toString( ArenaOrder.values() ) );
        }

        messagePrefix = mainConfig.getSetString( Keys.MESSAGE_PREFIX, Defaults.MESSAGE_PREFIX );

        loadParent();

        List<String> enabledArenaNames = mainConfig.getSetStringList( Keys.ENABLED_ARENAS, Defaults.ENABLED_ARENAS );
        enabledArenas = new ArrayList<>( enabledArenaNames.size() );
        if ( enabledArenaNames.isEmpty() ) {
            throw new StartupFailedException( "No arenas enabled" );
        }
        for ( String arenaName : enabledArenaNames ) {
            loadArena( arenaName );
        }

        mainConfig.save( String.format( Headers.CONFIG ) );
    }

    @Override
    public void reload() throws IOException, InvalidConfigurationException {
        load();
    }

    private void loadArena( String name ) {
        if ( enabledArenas == null ) {
            throw new IllegalStateException( "Enabled arenas null" );
        }
        File file = new File( arenaFolder, name + ".yml" );
        if ( !file.exists() ) {
            String fileName = Names.ARENAS + File.separatorChar + name + ".yml";
            try {
                plugin.saveResource( fileName, false );
            } catch ( IllegalArgumentException ex ) {
                throw new StartupFailedException( name + " is in " + Keys.ENABLED_ARENAS + " but file " + file.getAbsolutePath() + " could not be found and file " + fileName + " could not be found in plugin jar." );
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load( file );
        } catch ( FileNotFoundException ex ) {
            throw new StartupFailedException( name + " is in " + Keys.ENABLED_ARENAS + " but file " + file.getAbsolutePath() + " could not be found", ex );
        } catch ( IOException ex ) {
            throw new StartupFailedException( "IOException load file " + file.getAbsolutePath(), ex );
        } catch ( InvalidConfigurationException ex ) {
            throw new StartupFailedException( "Failed to load configuration file " + file.getAbsolutePath(), ex );
        }
        SkyArenaConfig arenaConfig = SkyArenaConfig.deserialize( config );
        arenaConfig.setArenaName( name );
        arenaConfig.setFile( file );
        arenaConfig.getMessages().setPrefix( messagePrefix );
        arenaConfig.setParent( parentArena );
        enabledArenas.add( arenaConfig );

        saveArena( file, arenaConfig, String.format( Headers.ARENA, name ) );
    }

    private void loadParent() {
        File file = new File( plugin.getDataFolder(), "arena-parent.yml" );
        if ( !file.exists() ) {
            String fileName = "arena-parent.yml";
            try {
                plugin.saveResource( fileName, false );
            } catch ( IllegalArgumentException ex ) {
                throw new StartupFailedException( "arena-parent.yml could not be found in plugin jar.", ex );
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load( file );
        } catch ( FileNotFoundException ex ) {
            throw new StartupFailedException( "Can't find the parent arena yaml", ex );
        } catch ( IOException ex ) {
            throw new StartupFailedException( "IOException loading arena-parent " + file.getAbsolutePath(), ex );
        } catch ( InvalidConfigurationException ex ) {
            throw new StartupFailedException( "Failed to load arena-parent.yml " + file.getAbsolutePath(), ex );
        }
        SkyArenaConfig arenaConfig = SkyArenaConfig.deserialize( config );
        arenaConfig.setArenaName( "parent-arena" );
        arenaConfig.setFile( file );
        arenaConfig.getMessages().setPrefix( messagePrefix );
        parentArena = arenaConfig;
        saveArena( file, arenaConfig, String.format( Headers.PARENT ) );
    }

    @Override
    public List<SkyArenaConfig> getEnabledArenas() {
        return Collections.unmodifiableList( enabledArenas );
    }

    public void saveArena( File file, SkyArenaConfig arenaConfig, String header ) {
        YamlConfiguration newConfig = new YamlConfiguration();
        newConfig.options().header( header ).indent( 2 );
        arenaConfig.serialize( newConfig );
        try {
            newConfig.save( file );
        } catch ( IOException ex ) {
            plugin.getLogger().log( Level.SEVERE, "Failed to save arena config to file " + file.getAbsolutePath(), ex );
        }
    }

    @Override
    public void saveArena( SkyArenaConfig arena ) {
        arena.getMessages().setPrefix( messagePrefix );
        arena.setParent( parentArena );
        saveArena( arena.getFile(), arena, String.format( Headers.ARENA, arena.getArenaName() ) );
    }

    private static class Keys {

        private static final String VERSION = "config-version";
        private static final String ENABLED_ARENAS = "enabled-arenas";
        private static final String ARENA_ORDER = "arena-order";
        private static final String MESSAGE_PREFIX = "message-prefix";
        private static final String DEBUG = "debug";
    }

    private static class Names {

        private static final String MAIN = "main-config.yml";
        private static final String ARENAS = "arenas";
    }

    private static class Defaults {

        private static final int VERSION = 1;
        private static final String MESSAGE_PREFIX = "&8[&cSkyWars&8]#B ";
        private static final boolean DEBUG = false;
        private static final ArenaOrder ARENA_ORDER = ArenaOrder.RANDOM;
        private static final List<String> ENABLED_ARENAS = Arrays.asList( "skyblock-warriors" );
    }

    private static class Headers {

        private static final String CONFIG =
                "####### config.yml #######%n"
                + "config-version should NOT be touched.%n"
                + "There _must_ be a <name>.yml file in arenas/ for every enabled arena.%n"
                + "arena-order can be RANDOM or ORDERED.%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "Extensive documentation COMING SOON. Will be available at%n"
                + " https://github.com/daboross/SkyWars/wiki when created.%n"
                + "#########";
        private static final String ARENA =
                "####### %s.yml ###%n"
                + "This is the Skyblock Warriors arena config.%n"
                + "As you can see 'messages:' isn't listed.%n"
                + "You can add arena-specific messages by adding a 'messages:'%n"
                + "section mirroring the one in arena-parent.yml.%n"
                + "Otherwise all messages are inherited from arena-parent.yml%n"
                + "%n"
                + "All values that are not in this configuration will be inherited from%n"
                + " arena-parent.yml%n"
                + "%n"
                + "All arena configs should have the config-version as 0. This will allow them to%n"
                + "be updated whenever any of the values change.%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "Extensive documentation COMING SOON. Will be available at%n"
                + " https://github.com/daboross/SkyWars/wiki when created.%n"
                + "#######";
        private static final String PARENT =
                "####### arena-parent.yml ###%n"
                + "Any settings that an individual arena config leaves out will be inherited%n"
                + " from this arena config.%n"
                + "All of the options must be defined here in order for configuration to work%n"
                + " correctly when the messages are left out of a child config.%n"
                + "Messages section can use both & color codes and # color codes.%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "Extensive documentation COMING SOON. Will be available at%n"
                + " https://github.com/daboross/SkyWars/wiki when created.%n"
                + "#######";
    }
}

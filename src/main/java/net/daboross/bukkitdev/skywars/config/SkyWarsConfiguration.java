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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class SkyWarsConfiguration implements SkyConfiguration {

    private List<SkyArenaConfig> enabledArenas;
    private SkyFileConfig mainConfig;
    private final SkyWars plugin;
    @Getter
    private File mainConfigFile;
    @Getter
    private File arenaFolder;
    @Getter
    private SkyArenaConfig parentArena;
    @Getter
    private ArenaOrder arenaOrder;
    @Getter
    private String messagePrefix;
    @Getter
    private boolean inventorySaveEnabled;
    @Getter
    private boolean enablePoints;
    @Getter
    private int deathPointDiff;
    @Getter
    private int winPointDiff;
    @Getter
    private int killPointDiff;
    @Getter
    private int arenaDistanceApart;
    @Getter
    private boolean commandWhitelistEnabled;
    @Getter
    private boolean commandWhitelistABlacklist;
    @Getter
    private Pattern commandWhitelistCommandRegex;
    private final SkyArenaConfigLoader arenaLoader;

    public SkyWarsConfiguration(SkyWars plugin) throws IOException, InvalidConfigurationException, SkyConfigurationException {
        this.plugin = plugin;
        this.arenaLoader = new SkyArenaConfigLoader();
        load();
    }

    private void load() throws IOException, InvalidConfigurationException, SkyConfigurationException {
        if (mainConfigFile == null) {
            mainConfigFile = new File(plugin.getDataFolder(), Names.MAIN);
        }
        mainConfig = new SkyFileConfig(mainConfigFile, plugin.getLogger());
        mainConfig.load();

        if (arenaFolder == null) {
            arenaFolder = new File(plugin.getDataFolder(), Names.ARENAS);
        }
        if (!arenaFolder.exists()) {
            boolean mkdirs = arenaFolder.mkdirs();
            if (!mkdirs) {
                throw new SkyConfigurationException("Making directory " + arenaFolder.getAbsolutePath() + " failed");
            }
        } else if (!arenaFolder.isDirectory()) {
            throw new SkyConfigurationException("File " + arenaFolder.getAbsolutePath() + " exists but is not a directory");
        }

        // Keys.VERSION
        int version = mainConfig.getSetInt(Keys.VERSION, Defaults.VERSION);
        if (version > 1) {
            throw new SkyConfigurationException("Version '" + version + "' as listed under " + Keys.VERSION + " in file " + mainConfigFile.getAbsolutePath() + " is unknown.");
        }
        mainConfig.getConfig().set(Keys.VERSION, Defaults.VERSION);

        // Keys.DEBUG
        SkyStatic.setDebug(mainConfig.getSetBoolean(Keys.DEBUG, Defaults.DEBUG));

        arenaOrder = ArenaOrder.getOrder(mainConfig.getSetString(Keys.ARENA_ORDER, Defaults.ARENA_ORDER.toString()));
        if (arenaOrder == null) {
            throw new SkyConfigurationException("Invalid ArenaOrder '" + arenaOrder + "' found under " + Keys.ARENA_ORDER + " in file " + mainConfigFile.getAbsolutePath() + ". Valid values: " + Arrays.toString(ArenaOrder.values()));
        }

        // Keys.MESSAGE_PREFIX
        messagePrefix = mainConfig.getSetString(Keys.MESSAGE_PREFIX, Defaults.MESSAGE_PREFIX);

        // Keys.SAVE_INVENTORY
        inventorySaveEnabled = mainConfig.getSetBoolean(Keys.SAVE_INVENTORY, Defaults.SAVE_INVENTORY);

        // Keys.ENABLED_ARENAS
        List<String> enabledArenaNames = mainConfig.getSetStringList(Keys.ENABLED_ARENAS, Defaults.ENABLED_ARENAS);
        enabledArenas = new ArrayList<>(enabledArenaNames.size());
        if (enabledArenaNames.isEmpty()) {
            throw new SkyConfigurationException("No arenas enabled");
        }

        // Keys.Points
        enablePoints = mainConfig.getSetBoolean(Keys.Points.ENABLE, Defaults.Points.ENABLE);
        winPointDiff = mainConfig.getSetInt(Keys.Points.WIN_DIFF, Defaults.Points.WIN_DIFF);
        deathPointDiff = mainConfig.getSetInt(Keys.Points.DEATH_DIFF, Defaults.Points.DEATH_DIFF);
        killPointDiff = mainConfig.getSetInt(Keys.Points.KILL_DIFF, Defaults.Points.KILL_DIFF);

        // Keys.ARENA_DISTANCE_APART
        arenaDistanceApart = mainConfig.getSetInt(Keys.ARENA_DISTANCE_APART, Defaults.ARENA_DISTANCE_APART);

        //Command Whitelist
        commandWhitelistEnabled = mainConfig.getSetBoolean(Keys.CommandWhitelist.WHITELIST_ENABLED, Defaults.CommandWhitelist.WHITELIST_ENABLED);
        commandWhitelistABlacklist = mainConfig.getSetBoolean(Keys.CommandWhitelist.IS_BLACKLIST, Defaults.CommandWhitelist.IS_BLACKLIST);
        commandWhitelistCommandRegex = createCommandRegex(mainConfig.getSetStringList(Keys.CommandWhitelist.COMMAND_WHITELIST, Defaults.CommandWhitelist.COMMAND_WHITELIST));
        // Remove deprecated values
        mainConfig.removeValues(Keys.Deprecated.CHAT_PREFIX, Keys.Deprecated.PREFIX_CHAT);
        // Save
        mainConfig.save(String.format(Headers.CONFIG));

        // Arenas
        loadParent();
        for (String arenaName : enabledArenaNames) {
            loadArena(arenaName);
        }
    }

    private Pattern createCommandRegex(List<String> commands) {
        if (commands.isEmpty()) {
            return null;
        } else {
            StringBuilder b = new StringBuilder("(?i)^(" + Matcher.quoteReplacement(commands.get(0)));
            for (int i = 1; i < commands.size(); i++) {
                b.append("|").append(Matcher.quoteReplacement(commands.get(0)));
            }
            b.append(")( .*|$)");
            return Pattern.compile(b.toString());
        }
    }

    @Override
    public void reload() throws IOException, InvalidConfigurationException, SkyConfigurationException {
        load();
    }

    private void loadArena(String name) throws SkyConfigurationException {
        if (enabledArenas == null) {
            throw new IllegalStateException("Enabled arenas null");
        }
        File file = new File(arenaFolder, name + ".yml");
        if (!file.exists()) {
            String fileName = Names.ARENAS + File.separatorChar + name + ".yml";
            try {
                plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException ex) {
                throw new SkyConfigurationException(name + " is in " + Keys.ENABLED_ARENAS + " but file " + file.getAbsolutePath() + " could not be found.");
            }
        }
        SkyArenaConfig arenaConfig = arenaLoader.loadArena(file, name, messagePrefix);
        arenaConfig.setParent(parentArena);
        enabledArenas.add(arenaConfig);

        saveArena(file, arenaConfig, String.format(Headers.ARENA, name));
    }

    private void loadParent() throws SkyConfigurationException {
        File file = new File(plugin.getDataFolder(), "arena-parent.yml");
        if (!file.exists()) {
            String fileName = "arena-parent.yml";
            try {
                plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException ex) {
                throw new SkyConfigurationException("arena-parent.yml could not be found in plugin jar.", ex);
            }
        }
        SkyArenaConfig arenaConfig = arenaLoader.loadArena(file, "parent-arena", messagePrefix);
        parentArena = arenaConfig;
        parentArena.confirmAllValuesExist();
        saveArena(file, arenaConfig, String.format(Headers.PARENT));
    }

    @Override
    public List<SkyArenaConfig> getEnabledArenas() {
        return Collections.unmodifiableList(enabledArenas);
    }

    public void saveArena(File file, SkyArenaConfig arenaConfig, String header) {
        YamlConfiguration newConfig = new YamlConfiguration();
        newConfig.options().header(header).indent(2);
        arenaConfig.serialize(newConfig);
        try {
            newConfig.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save arena config to file " + file.getAbsolutePath(), ex);
        }
    }

    @Override
    public void saveArena(SkyArenaConfig arena) {
        arena.getMessages().setPrefix(messagePrefix);
        arena.setParent(parentArena);
        saveArena(arena.getFile(), arena, String.format(Headers.ARENA, arena.getArenaName()));
    }

    @Override
    public YamlConfiguration getRawConfig() {
        return mainConfig.getConfig();
    }

    private static class Keys {

        private static final String VERSION = "config-version";
        private static final String ENABLED_ARENAS = "enabled-arenas";
        private static final String ARENA_ORDER = "arena-order";
        private static final String MESSAGE_PREFIX = "message-prefix";
        private static final String DEBUG = "debug";
        private static final String SAVE_INVENTORY = "save-inventory";
        private static final String ARENA_DISTANCE_APART = "arena-distance-apart";

        private static class Points {

            private static final String ENABLE = "points.enable-points";
            private static final String DEATH_DIFF = "points.death-point-diff";
            private static final String WIN_DIFF = "points.win-point-diff";
            private static final String KILL_DIFF = "points.kill-point-diff";
        }

        private static class CommandWhitelist {

            private static final String WHITELIST_ENABLED = "command-whitelist.whitelist-enabled";
            private static final String IS_BLACKLIST = "command-whitelist.treated-as-blacklist";
            private static final String COMMAND_WHITELIST = "command-whitelist.whitelist";
        }

        private static class Deprecated {

            private static final String PREFIX_CHAT = "points.should-prefix-chat";
            private static final String CHAT_PREFIX = "points.chat-prefix";
        }
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
        private static final List<String> ENABLED_ARENAS = Arrays.asList("skyblock-warriors");
        private static final boolean SAVE_INVENTORY = true;
        private static final int ARENA_DISTANCE_APART = 200;

        private static class Points {

            private static final boolean ENABLE = true;
            private static final int DEATH_DIFF = -2;
            private static final int WIN_DIFF = 7;
            private static final int KILL_DIFF = 1;
        }

        private static class CommandWhitelist {

            private static final boolean WHITELIST_ENABLED = true;
            private static final boolean IS_BLACKLIST = false;
            private static final List<String> COMMAND_WHITELIST = Arrays.asList("/skywars", "/sw", "/me");
        }

    }

    private static class Headers {

        private static final String CONFIG
                = "####### config.yml #######%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "For documentation, please visit %n"
                + "https://github.com/daboross/SkyWars/wiki/Configuration%n"
                + "#########";
        private static final String ARENA
                = "####### %s.yml ###%n"
                + "This is the Skyblock Warriors arena config.%n"
                + "%n"
                + "All values that are not in this configuration will be inherited from%n"
                + " arena-parent.yml%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "For documentation, please visit %n"
                + "https://github.com/daboross/SkyWars/wiki/Configuration%n"
                + "#######";
        private static final String PARENT
                = "####### arena-parent.yml ###%n"
                + "Any settings that an individual arena config leaves out will be inherited%n"
                + " from this arena config.%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "For documentation, please visit %n"
                + "https://github.com/daboross/SkyWars/wiki/Configuration%n"
                + "#######";
    }
}

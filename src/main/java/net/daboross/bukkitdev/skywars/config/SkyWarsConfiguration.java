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
import net.daboross.bukkitdev.skywars.api.config.ConfigColorCode;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class SkyWarsConfiguration implements SkyConfiguration {

    private final SkyArenaConfigLoader arenaLoader = new SkyArenaConfigLoader();
    private List<SkyArenaConfig> enabledArenas;
    private final SkyWars plugin;
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
    private long pointsSaveInterval;
    @Getter
    private int arenaDistanceApart;
    @Getter
    private boolean commandWhitelistEnabled;
    @Getter
    private boolean commandWhitelistABlacklist;
    @Getter
    private Pattern commandWhitelistCommandRegex;
    @Getter
    private boolean economyEnabled;
    @Getter
    private int economyWinReward;
    @Getter
    private int economyKillReward;
    private boolean economyRewardMessages;
    private boolean perArenaDeathMessagesEnabled;
    private boolean perArenaWinMessagesEnabled;

    public SkyWarsConfiguration(SkyWars plugin) throws IOException, InvalidConfigurationException, SkyConfigurationException {
        this.plugin = plugin;
        load();
    }

    private void load() throws IOException, InvalidConfigurationException, SkyConfigurationException {
        // This is expected to be only done once, so it is OK to not store some values
        File mainConfigFile = new File(plugin.getDataFolder(), Names.MAIN);
        SkyFileConfig mainConfig = new SkyFileConfig(mainConfigFile, plugin.getLogger());
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

        int version = mainConfig.getSetInt(MainConfigKeys.VERSION, MainConfigDefaults.VERSION);
        if (version > 2) {
            throw new SkyConfigurationException("Version '" + version + "' as listed under " + MainConfigKeys.VERSION + " in file " + mainConfigFile.getAbsolutePath() + " is unknown.");
        }
        mainConfig.getConfig().set(MainConfigKeys.VERSION, MainConfigDefaults.VERSION);

        SkyStatic.setDebug(mainConfig.getSetBoolean(MainConfigKeys.DEBUG, MainConfigDefaults.DEBUG));

        arenaOrder = ArenaOrder.getOrder(mainConfig.getSetString(MainConfigKeys.ARENA_ORDER, MainConfigDefaults.ARENA_ORDER.toString()));
        if (arenaOrder == null) {
            throw new SkyConfigurationException("Invalid ArenaOrder '" + arenaOrder + "' found under " + MainConfigKeys.ARENA_ORDER + " in file " + mainConfigFile.getAbsolutePath() + ". Valid values: " + Arrays.toString(ArenaOrder.values()));
        }

        messagePrefix = ConfigColorCode.translateCodes(mainConfig.getSetString(MainConfigKeys.MESSAGE_PREFIX, MainConfigDefaults.MESSAGE_PREFIX));
        mainConfig.setStringIfNot(MainConfigKeys.MESSAGE_PREFIX, messagePrefix);
        inventorySaveEnabled = mainConfig.getSetBoolean(MainConfigKeys.SAVE_INVENTORY, MainConfigDefaults.SAVE_INVENTORY);

        List<String> enabledArenaNames = mainConfig.getSetStringList(MainConfigKeys.ENABLED_ARENAS, MainConfigDefaults.ENABLED_ARENAS);
        enabledArenas = new ArrayList<>(enabledArenaNames.size());
        if (enabledArenaNames.isEmpty()) {
            throw new SkyConfigurationException("No arenas enabled");
        }

        // Points
        enablePoints = mainConfig.getSetBoolean(MainConfigKeys.Points.ENABLE, MainConfigDefaults.Points.ENABLE);
        winPointDiff = mainConfig.getSetInt(MainConfigKeys.Points.WIN_DIFF, MainConfigDefaults.Points.WIN_DIFF);
        deathPointDiff = mainConfig.getSetInt(MainConfigKeys.Points.DEATH_DIFF, MainConfigDefaults.Points.DEATH_DIFF);
        killPointDiff = mainConfig.getSetInt(MainConfigKeys.Points.KILL_DIFF, MainConfigDefaults.Points.KILL_DIFF);
        pointsSaveInterval = mainConfig.getSetLong(MainConfigKeys.Points.SAVE_INTERVAL, MainConfigDefaults.Points.SAVE_INTERVAL);

        // Economy
        economyEnabled = mainConfig.getSetBoolean(MainConfigKeys.Economy.ENABLE, MainConfigDefaults.Economy.ENABLE);
        economyKillReward = mainConfig.getSetInt(MainConfigKeys.Economy.KILL_REWARD, MainConfigDefaults.Economy.KILL_REWARD);
        economyWinReward = mainConfig.getSetInt(MainConfigKeys.Economy.WIN_REWARD, MainConfigDefaults.Economy.WIN_REWARD);
        economyRewardMessages = mainConfig.getSetBoolean(MainConfigKeys.Economy.MESSAGE, MainConfigDefaults.Economy.MESSAGE);

        arenaDistanceApart = mainConfig.getSetInt(MainConfigKeys.ARENA_DISTANCE_APART, MainConfigDefaults.ARENA_DISTANCE_APART);

        commandWhitelistEnabled = mainConfig.getSetBoolean(MainConfigKeys.CommandWhitelist.WHITELIST_ENABLED, MainConfigDefaults.CommandWhitelist.WHITELIST_ENABLED);
        commandWhitelistABlacklist = mainConfig.getSetBoolean(MainConfigKeys.CommandWhitelist.IS_BLACKLIST, MainConfigDefaults.CommandWhitelist.IS_BLACKLIST);
        commandWhitelistCommandRegex = createCommandRegex(mainConfig.getSetStringList(MainConfigKeys.CommandWhitelist.COMMAND_WHITELIST, MainConfigDefaults.CommandWhitelist.COMMAND_WHITELIST));

        // per-arena messages
        perArenaDeathMessagesEnabled = mainConfig.getSetBoolean(MainConfigKeys.PER_ARENA_DEATH_MESSAGES_ENABLED, MainConfigDefaults.PER_ARENA_DEATH_MESSAGES_ENABLED);
        perArenaWinMessagesEnabled = mainConfig.getSetBoolean(MainConfigKeys.PER_ARENA_WIN_MESSAGES_ENABLED, MainConfigDefaults.PER_ARENA_WIN_MESSAGES_ENABLED);
        // Remove deprecated values
        mainConfig.removeValues(MainConfigKeys.Deprecated.CHAT_PREFIX, MainConfigKeys.Deprecated.PREFIX_CHAT);

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
                b.append("|").append(Matcher.quoteReplacement(commands.get(i)));
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
                throw new SkyConfigurationException(name + " is in " + MainConfigKeys.ENABLED_ARENAS + " but file " + file.getAbsolutePath() + " could not be found.");
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
    public boolean arePerArenaDeathMessagesEnabled() {
        return perArenaDeathMessagesEnabled;
    }

    @Override
    public boolean arePerArenaWinMessagesEnabled() {
        return perArenaWinMessagesEnabled;
    }

    @Override
    public boolean areEconomyRewardMessagesEnabled() {
        return economyRewardMessages;
    }

    private static class Names {

        private static final String MAIN = "main-config.yml";
        private static final String ARENAS = "arenas";
    }

    private static class Headers {

        private static final String CONFIG = "####### config.yml #######%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "For documentation, please visit %n"
                + "https://github.com/daboross/SkyWars/wiki/Configuration-main-config%n"
                + "#########";
        private static final String ARENA = "####### %s.yml ###%n"
                + "This is the Skyblock Warriors arena config.%n"
                + "%n"
                + "All values that are not in this configuration will be inherited from%n"
                + " arena-parent.yml%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "For documentation, please visit %n"
                + "https://github.com/daboross/SkyWars/wiki/Configuration-arenas%n"
                + "#######";
        private static final String PARENT = "####### arena-parent.yml ###%n"
                + "Any settings that an individual arena config leaves out will be inherited%n"
                + " from this arena config.%n"
                + "%n"
                + "All comment changes will be removed.%n"
                + "%n"
                + "For documentation, please visit %n"
                + "https://github.com/daboross/SkyWars/wiki/Configuration-parent%n"
                + "#######";
    }
}

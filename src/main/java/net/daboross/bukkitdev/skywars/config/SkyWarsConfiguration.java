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
package net.daboross.bukkitdev.skywars.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private Path arenaFolder;
    private SkyArenaConfig parentArena;
    private ArenaOrder arenaOrder;
    private boolean skipUuidCheck;
    private String messagePrefix;
    private boolean inventorySaveEnabled;
    private boolean enableScore;
    private int deathScoreDiff;
    private int winScoreDiff;
    private int killScoreDiff;
    private boolean scoreUseSql;
    private String scoreSqlHost;
    private int scoreSqlPort;
    private String scoreSqlDatabase;
    private String scoreSqlUsername;
    private String scoreSqlPassword;
    private long scoreSaveInterval;
    private int arenaDistanceApart;
    private boolean commandWhitelistEnabled;
    private boolean commandWhitelistABlacklist;
    private Pattern commandWhitelistCommandRegex;
    private boolean economyEnabled;
    private int economyWinReward;
    private int economyKillReward;
    private String locale;
    private boolean disableReport;
    private boolean economyRewardMessages;
    //    private boolean perArenaDeathMessagesEnabled;
//    private boolean perArenaWinMessagesEnabled;
    private boolean multiverseCoreHookEnabled;
    private boolean multiverseInventoriesHookEnabled;
    private boolean worldeditHookEnabled;

    public SkyWarsConfiguration(SkyWars plugin) throws IOException, InvalidConfigurationException, SkyConfigurationException {
        this.plugin = plugin;
        load();
    }

    private void load() throws IOException, InvalidConfigurationException, SkyConfigurationException {
        // This is expected to be only done once, so it is OK to not store some values
        Path mainConfigFile = plugin.getDataFolder().toPath().resolve(Names.MAIN);
        SkyFileConfig mainConfig = new SkyFileConfig(mainConfigFile, plugin.getLogger());
        mainConfig.load();

        if (arenaFolder == null) {
            arenaFolder = plugin.getDataFolder().toPath().resolve(Names.ARENAS);
        }
        if (!Files.exists(arenaFolder)) {
            Files.createDirectories(arenaFolder);
        } else if (!Files.isDirectory(arenaFolder)) {
            throw new SkyConfigurationException("File " + arenaFolder.toAbsolutePath() + " exists but is not a directory");
        }

        int version = mainConfig.getSetInt(MainConfigKeys.VERSION, MainConfigDefaults.VERSION);
        if (version > 2) {
            throw new SkyConfigurationException("Version '" + version + "' as listed under " + MainConfigKeys.VERSION + " in file " + mainConfigFile.toAbsolutePath() + " is unknown.");
        }
        mainConfig.getConfig().set(MainConfigKeys.VERSION, MainConfigDefaults.VERSION);

        SkyStatic.setDebug(mainConfig.getSetBoolean(MainConfigKeys.DEBUG, MainConfigDefaults.DEBUG));
        skipUuidCheck = mainConfig.getSetBoolean(MainConfigKeys.SKIP_UUID_CHECK, MainConfigDefaults.SKIP_UUID_CHECK);
        String arenaOrderString = mainConfig.getSetString(MainConfigKeys.ARENA_ORDER, MainConfigDefaults.ARENA_ORDER.toString());
        arenaOrder = ArenaOrder.getOrder(arenaOrderString);
        if (arenaOrder == null) {
            throw new SkyConfigurationException("Invalid ArenaOrder '" + arenaOrderString + "' found under " + MainConfigKeys.ARENA_ORDER + " in file " + mainConfigFile.toAbsolutePath() + ". Valid values: " + Arrays.toString(ArenaOrder.values()));
        }

        messagePrefix = ConfigColorCode.translateCodes(mainConfig.getSetString(MainConfigKeys.MESSAGE_PREFIX, MainConfigDefaults.MESSAGE_PREFIX));
        mainConfig.setStringIfNot(MainConfigKeys.MESSAGE_PREFIX, messagePrefix);
        inventorySaveEnabled = mainConfig.getSetBoolean(MainConfigKeys.SAVE_INVENTORY, MainConfigDefaults.SAVE_INVENTORY);

        List<String> enabledArenaNames = mainConfig.getSetStringList(MainConfigKeys.ENABLED_ARENAS, MainConfigDefaults.ENABLED_ARENAS);
        enabledArenas = new ArrayList<>(enabledArenaNames.size());
        if (enabledArenaNames.isEmpty()) {
            throw new SkyConfigurationException("No arenas enabled");
        }

        locale = mainConfig.getSetString(MainConfigKeys.LOCALE, MainConfigDefaults.LOCALE);

        // Score
        enableScore = mainConfig.getSetBoolean(MainConfigKeys.Score.ENABLE, MainConfigDefaults.Score.ENABLE);
        winScoreDiff = mainConfig.getSetInt(MainConfigKeys.Score.WIN_DIFF, MainConfigDefaults.Score.WIN_DIFF);
        deathScoreDiff = mainConfig.getSetInt(MainConfigKeys.Score.DEATH_DIFF, MainConfigDefaults.Score.DEATH_DIFF);
        killScoreDiff = mainConfig.getSetInt(MainConfigKeys.Score.KILL_DIFF, MainConfigDefaults.Score.KILL_DIFF);
        scoreSaveInterval = mainConfig.getSetLong(MainConfigKeys.Score.SAVE_INTERVAL, MainConfigDefaults.Score.SAVE_INTERVAL);
        // Score.SQL
        scoreUseSql = mainConfig.getSetBoolean(MainConfigKeys.Score.USE_SQL, MainConfigDefaults.Score.USE_SQL);
        scoreSqlHost = mainConfig.getSetString(MainConfigKeys.Score.SQL_HOST, MainConfigDefaults.Score.SQL_HOST);
        scoreSqlPort = mainConfig.getSetInt(MainConfigKeys.Score.SQL_PORT, MainConfigDefaults.Score.SQL_PORT);
        scoreSqlDatabase = mainConfig.getSetString(MainConfigKeys.Score.SQL_DATABASE, MainConfigDefaults.Score.SQL_DATABASE);
        scoreSqlUsername = mainConfig.getSetString(MainConfigKeys.Score.SQL_USERNAME, MainConfigDefaults.Score.SQL_USERNAME);
        scoreSqlPassword = mainConfig.getSetString(MainConfigKeys.Score.SQL_PASSWORD, MainConfigDefaults.Score.SQL_PASSWORD);
        // Economy
        economyEnabled = mainConfig.getSetBoolean(MainConfigKeys.Economy.ENABLE, MainConfigDefaults.Economy.ENABLE);
        economyKillReward = mainConfig.getSetInt(MainConfigKeys.Economy.KILL_REWARD, MainConfigDefaults.Economy.KILL_REWARD);
        economyWinReward = mainConfig.getSetInt(MainConfigKeys.Economy.WIN_REWARD, MainConfigDefaults.Economy.WIN_REWARD);
        economyRewardMessages = mainConfig.getSetBoolean(MainConfigKeys.Economy.MESSAGE, MainConfigDefaults.Economy.MESSAGE);

        arenaDistanceApart = mainConfig.getSetInt(MainConfigKeys.ARENA_DISTANCE_APART, MainConfigDefaults.ARENA_DISTANCE_APART);

        commandWhitelistEnabled = mainConfig.getSetBoolean(MainConfigKeys.CommandWhitelist.WHITELIST_ENABLED, MainConfigDefaults.CommandWhitelist.WHITELIST_ENABLED);
        commandWhitelistABlacklist = mainConfig.getSetBoolean(MainConfigKeys.CommandWhitelist.IS_BLACKLIST, MainConfigDefaults.CommandWhitelist.IS_BLACKLIST);
        commandWhitelistCommandRegex = createCommandRegex(mainConfig.getSetStringList(MainConfigKeys.CommandWhitelist.COMMAND_WHITELIST, MainConfigDefaults.CommandWhitelist.COMMAND_WHITELIST));

        // Report disable
        disableReport = mainConfig.getConfig().getBoolean("disable-report", false);

        // per-arena messages
//        perArenaDeathMessagesEnabled = mainConfig.getSetBoolean(MainConfigKeys.PER_ARENA_DEATH_MESSAGES_ENABLED, MainConfigDefaults.PER_ARENA_DEATH_MESSAGES_ENABLED);
//        perArenaWinMessagesEnabled = mainConfig.getSetBoolean(MainConfigKeys.PER_ARENA_WIN_MESSAGES_ENABLED, MainConfigDefaults.PER_ARENA_WIN_MESSAGES_ENABLED);

        // Hooks
        multiverseCoreHookEnabled = mainConfig.getSetBoolean(MainConfigKeys.Hooks.MULTIVERSE_CORE, MainConfigDefaults.Hooks.MULTIVERSE_CORE);
        multiverseInventoriesHookEnabled = mainConfig.getSetBoolean(MainConfigKeys.Hooks.MULTIVERSE_INVENTORIES, MainConfigDefaults.Hooks.MULTIVERSE_INVENTORIES);
        worldeditHookEnabled = mainConfig.getSetBoolean(MainConfigKeys.Hooks.WORLDEDIT, MainConfigDefaults.Hooks.WORLDEDIT);

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
        Path file = arenaFolder.resolve(name + ".yml");
        if (!Files.exists(file)) {
            String fileName = Paths.get(Names.ARENAS, name + ".yml").toString();
            try {
                plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException ex) {
                throw new SkyConfigurationException(name + " is in " + MainConfigKeys.ENABLED_ARENAS + " but file " + file.toAbsolutePath() + " could not be found.");
            }
        }
        SkyArenaConfig arenaConfig = arenaLoader.loadArena(file, name, messagePrefix);
        arenaConfig.setParent(parentArena);
        enabledArenas.add(arenaConfig);

        saveArena(file, arenaConfig, String.format(Headers.ARENA, name));
    }

    private void loadParent() throws SkyConfigurationException {
        Path path = plugin.getDataFolder().toPath().resolve("arena-parent.yml");
        if (!Files.exists(path)) {
            String fileName = "arena-parent.yml";
            try {
                plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException ex) {
                throw new SkyConfigurationException("arena-parent.yml could not be found in plugin jar.", ex);
            }
        }
        SkyArenaConfig arenaConfig = arenaLoader.loadArena(path, "parent-arena", messagePrefix);
        parentArena = arenaConfig;
        parentArena.confirmAllValuesExist();
        saveArena(path, arenaConfig, String.format(Headers.PARENT));
    }

    public void saveArena(Path path, SkyArenaConfig arenaConfig, String header) {
        YamlConfiguration newConfig = new YamlConfiguration();
        newConfig.options().header(header).indent(2);
        arenaConfig.serialize(newConfig);
        try {
            newConfig.save(path.toFile());
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save arena config to file " + path.toAbsolutePath(), ex);
        }
    }

    @Override
    public void saveArena(SkyArenaConfig arena) {
        arena.getMessages().setPrefix(messagePrefix);
        arena.setParent(parentArena);
        saveArena(arena.getFile(), arena, String.format(Headers.ARENA, arena.getArenaName()));
    }

//    @Override
//    public boolean arePerArenaDeathMessagesEnabled() {
//        return perArenaDeathMessagesEnabled;
//    }

//    @Override
//    public boolean arePerArenaWinMessagesEnabled() {
//        return perArenaWinMessagesEnabled;
//    }

    @Override
    public List<SkyArenaConfig> getEnabledArenas() {
        return Collections.unmodifiableList(enabledArenas);
    }

    @Override
    public boolean areEconomyRewardMessagesEnabled() {
        return economyRewardMessages;
    }

    @Override
    public Path getArenaFolder() {
        return arenaFolder;
    }

    @Override
    public SkyArenaConfig getParentArena() {
        return parentArena;
    }

    @Override
    public ArenaOrder getArenaOrder() {
        return arenaOrder;
    }

    @Override
    public String getMessagePrefix() {
        return messagePrefix;
    }

    @Override
    public boolean isInventorySaveEnabled() {
        return inventorySaveEnabled;
    }

    @Override
    public boolean isEnableScore() {
        return enableScore;
    }

    @Override
    public int getDeathScoreDiff() {
        return deathScoreDiff;
    }

    @Override
    public int getWinScoreDiff() {
        return winScoreDiff;
    }

    @Override
    public int getKillScoreDiff() {
        return killScoreDiff;
    }

    @Override
    public long getScoreSaveInterval() {
        return scoreSaveInterval;
    }

    @Override
    public int getArenaDistanceApart() {
        return arenaDistanceApart;
    }

    @Override
    public boolean isCommandWhitelistEnabled() {
        return commandWhitelistEnabled;
    }

    @Override
    public boolean isCommandWhitelistABlacklist() {
        return commandWhitelistABlacklist;
    }

    @Override
    public Pattern getCommandWhitelistCommandRegex() {
        return commandWhitelistCommandRegex;
    }

    @Override
    public boolean isEconomyEnabled() {
        return economyEnabled;
    }

    @Override
    public int getEconomyWinReward() {
        return economyWinReward;
    }

    @Override
    public int getEconomyKillReward() {
        return economyKillReward;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public boolean isDisableReport() {
        return disableReport;
    }

    @Override
    public boolean isScoreUseSql() {
        return scoreUseSql;
    }

    @Override
    public String getScoreSqlHost() {
        return scoreSqlHost;
    }

    @Override
    public int getScoreSqlPort() {
        return scoreSqlPort;
    }

    @Override
    public String getScoreSqlUsername() {
        return scoreSqlUsername;
    }

    @Override
    public String getScoreSqlPassword() {
        return scoreSqlPassword;
    }

    @Override
    public String getScoreSqlDatabase() {
        return scoreSqlDatabase;
    }

    @Override
    public boolean isMultiverseCoreHookEnabled() {
        return multiverseCoreHookEnabled;
    }

    @Override
    public boolean isMultiverseInventoriesHookEnabled() {
        return multiverseInventoriesHookEnabled;
    }

    @Override
    public boolean isWorldeditHookEnabled() {
        return worldeditHookEnabled;
    }

    @Override
    public boolean isSkipUuidCheck() {
        return skipUuidCheck;
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
                + "http://dabo.guru/skywars/configuring-skywars%n"
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

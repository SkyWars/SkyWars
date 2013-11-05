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
import java.io.InputStream;
import java.net.URL;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.translations.SkyTranslations;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TranslationsConfiguration implements SkyTranslations {

    private final SkyWars plugin;
    private final File configFile;
    private final File newConfigFile;
    private Locale locale;
    private String language;
    private Map<TransKey, String> values;

    public TranslationsConfiguration(SkyWars plugin) throws SkyConfigurationException {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "messages.yml");
        this.newConfigFile = new File(plugin.getDataFolder(), "messages.new.yml");
        this.values = new EnumMap<>(TransKey.class);
        this.load();
    }

    private void load() throws SkyConfigurationException {
        try {
            configFile.createNewFile();
        } catch (IOException ex) {
            throw new SkyConfigurationException("IOException creating new file " + configFile.getAbsolutePath(), ex);
        }
        FileConfiguration config = loadMain();
        Map<TransKey, String> internal = loadInternal();
        if (!config.contains("messages-version")) {
            config.set("messages-version", TransKey.VERSION);
        }
        if (!config.contains("messages-language")) {
            config.set("messages-locale", language);
        }
        int version = config.getInt("messages-version");
        if (!config.contains("auto-update")) {
            config.set("auto-update", true);
        }
        boolean autoUpdate = config.getBoolean("auto-update");
        boolean autoUpdating = autoUpdate && (version < TransKey.VERSION || !config.getString("messages-locale").equals(language));
        if (autoUpdating) {
            config.set("messages-version", TransKey.VERSION);
            config.set("messages-locale", language);
            this.values = internal;
        } else {
            this.values = new EnumMap<>(TransKey.class);
        }

        if (!autoUpdating) {
            for (TransKey key : TransKey.values()) {
                if (config.contains(key.key)) {
                    values.put(key, config.getString(key.key));
                    config.set(key.key, null);
                } else {
                    values.put(key, internal.get(key));
                }
            }
        }
        for (Map.Entry<TransKey, String> entry : values.entrySet()) {
            config.set(entry.getKey().key, entry.getValue());
            entry.setValue(ChatColor.translateAlternateColorCodes('&', entry.getValue()));
        }
        config.options().header(HEADER);
        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to save translations config file", ex);
        }
        if (version < TransKey.VERSION && !autoUpdate) {
            FileConfiguration newConfig = new YamlConfiguration();
            newConfig.options().pathSeparator('%').header(String.format(NEW_HEADER, TransKey.VERSION));
            for (TransKey key : TransKey.values()) {
                newConfig.set(key.key, internal.get(key));
            }
            try {
                newConfig.save(newConfigFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't save messages.new.yml", ex);
            }
        }
    }

    private FileConfiguration loadMain() throws SkyConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        config.options().pathSeparator('%');
        try {
            config.load(configFile);
        } catch (IOException ex) {
            throw new SkyConfigurationException("IOException loading messages file " + configFile.getAbsolutePath(), ex);
        } catch (InvalidConfigurationException ex) {
            throw new SkyConfigurationException("Messages file " + configFile.getAbsolutePath() + " is invalid", ex);
        }
        return config;
    }

    private Map<TransKey, String> loadInternal() throws SkyConfigurationException {
        locale = Locale.getDefault();
        String file = "/messages-" + locale.getLanguage() + ".yml";
        URL path = getClass().getResource(file);
        if (path == null) {
            plugin.getLogger().log(Level.INFO, "[Translations] Couldn''t find translation for locale {0}.", locale.getDisplayLanguage());
            locale = Locale.ENGLISH;
            file = "/messages-en.yml";
            path = getClass().getResource(file);
            if (path == null) {
                throw new SkyConfigurationException("There is no messages-en.yml file in the SkyWars jar.");
            }
        }
        language = locale.getLanguage();
        plugin.getLogger().log(Level.INFO, "[Translations] Loading locale {0}.", new Object[]{locale, file});
        YamlConfiguration config = new YamlConfiguration();
        config.options().pathSeparator('%');
        try (InputStream is = path.openStream()) {
            config.load(is);
        } catch (IOException | InvalidConfigurationException ex) {
            throw new SkyConfigurationException("Couldn't load internal translation file " + file);
        }
        Map<TransKey, String> internal = new EnumMap<>(TransKey.class);
        for (TransKey key : TransKey.values()) {
            if (config.contains(key.key)) {
                internal.put(key, config.getString(key.key));
            } else {
                throw new SkyConfigurationException("Internal translations file " + file + " does not contain key " + key.key);
            }
        }
        return internal;
    }

    @Override
    public String get(TransKey key) {
        return values.get(key);
    }
    private final String HEADER = "### messages.yml ###\n"
            + "Note! If you are editing this file, set auto-update to false. \n"
            + "If auto-update is left true, all changed values will be overwritten.\n"
            + "\n"
            + "When auto-update is false, new values will be added, but existing ones won't\n"
            + "be touched. When there is a new version of existing values available, SkyWars\n"
            + "will create a 'messages.new.yml' file containing the updated messages, that\n"
            + "you are free to copy from.\n"
            + "\n"
            + "The messages-version key is used to keep track of when updated messages are\n"
            + "available, no matter what the setting of auto-update is.";
    private final String NEW_HEADER = "### messages.new.yml ###\n"
            + "This file was generated because you have auto-update set to false in the\n"
            + "messages.yml file, and there are updated messages available.\n"
            + "\n"
            + "If you've updated the configuration to your desire, you can set the\n"
            + "config-version in messages.yml to %s to stop this file from re-generating,\n"
            + "then delete it.\n"
            + "\n"
            + "No changes to this file will persist!";
}

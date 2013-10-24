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
import java.util.EnumMap;
import java.util.Map;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.api.translations.SkyTranslations;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TranslationsConfiguration implements SkyTranslations {

    private final File configFile;
    private final Map<TransKey, String> values;

    public TranslationsConfiguration(SkyWars plugin) throws SkyConfigurationException {
        this.configFile = new File(plugin.getDataFolder(), "messages.yml");
        this.values = new EnumMap<>(TransKey.class);
        this.load();
    }

    private void load() throws SkyConfigurationException {
        try {
            configFile.createNewFile();
        } catch (IOException ex) {
            throw new SkyConfigurationException("IOException creating new file " + configFile.getAbsolutePath(), ex);
        }
        FileConfiguration config = new YamlConfiguration();
        config.options().pathSeparator('%');
        try {
            config.load(configFile);
        } catch (IOException ex) {
            throw new SkyConfigurationException("IOException loading messages file " + configFile.getAbsolutePath(), ex);
        } catch (InvalidConfigurationException ex) {
            throw new SkyConfigurationException("Messages file " + configFile.getAbsolutePath() + " is invalid", ex);
        }
        for (TransKey key : TransKey.values()) {
            if (config.contains(key.key)) {
                values.put(key, config.getString(key.key));
                config.set(key.key, null);
            } else {
                values.put(key, key.defaultValue);
            }
        }
        for (Map.Entry<TransKey, String> entry : values.entrySet()) {
            config.set(entry.getKey().key, entry.getValue());
        }
    }

    @Override
    public String get(TransKey key) {
        return values.get(key);
    }
}

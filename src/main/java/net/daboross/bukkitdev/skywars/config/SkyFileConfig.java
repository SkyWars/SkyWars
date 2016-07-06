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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class SkyFileConfig {

    private final Path configFile;
    @SuppressWarnings("NonConstantLogger")
    private final Logger logger;
    private YamlConfiguration config;

    public SkyFileConfig(final Path file, final Logger logger) {
        configFile = file;
        this.logger = logger;
    }

    public void load() throws IOException, InvalidConfigurationException {
        Path folder = configFile.getParent();
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        } else if (!Files.isDirectory(folder)) {
            throw new IOException("File " + folder.toAbsolutePath() + " is not a directory.");
        }
        if (!Files.exists(configFile)) {
            try {
                Files.createFile(configFile);
            } catch (IOException ex) {
                throw new IOException("Couldn't create file " + configFile.toAbsolutePath(), ex);
            }
        } else if (!Files.isRegularFile(configFile)) {
            throw new IOException("File or directory " + configFile.toAbsolutePath() + " is not a file");
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile.toFile());
        } catch (IOException ex) {
            throw new IOException("Failed to load " + configFile.toAbsolutePath() + " as a YAML configuration", ex);
        } catch (InvalidConfigurationException ex) {
            throw new InvalidConfigurationException("Failed to load " + configFile.toAbsolutePath() + " as a YAML configuration", ex);
        }
    }

    public void save(String header) throws IOException {
        config.options().header(header).indent(2);
        try {
            config.save(configFile.toFile());
        } catch (IOException ex) {
            throw new IOException("Failed to save to " + configFile.toAbsolutePath(), ex);
        }
    }

    public int getSetInt(String path, int defaultInt) throws InvalidConfigurationException {
        if (config.isInt(path)) {
            return config.getInt(path);
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.toAbsolutePath() + " is not an integer");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultInt, configFile});
            config.set(path, defaultInt);
            return defaultInt;
        }
    }

    public long getSetLong(String path, long defaultInt) throws InvalidConfigurationException {
        if (config.isInt(path)) {
            return config.getLong(path);
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.toAbsolutePath() + " is not a long");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultInt, configFile});
            config.set(path, defaultInt);
            return defaultInt;
        }
    }

    public boolean getSetBoolean(String path, boolean defaultBoolean) throws InvalidConfigurationException {
        if (config.isBoolean(path)) {
            return config.getBoolean(path);
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.toAbsolutePath() + " is not a boolean (true/false)");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultBoolean, configFile});
            config.set(path, defaultBoolean);
            return defaultBoolean;
        }
    }

    public String getSetString(String path, String defaultString) throws InvalidConfigurationException {
        Object obj = config.get(path);
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Integer || obj instanceof Double) {
            return obj.toString();
        } else if (obj != null) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.toAbsolutePath() + " is not a boolean (true/false)");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultString, configFile});
            config.set(path, defaultString);
            return defaultString;
        }
    }

    public void setStringIfNot(String path, String string) throws InvalidConfigurationException {
        String str = getSetString(path, string);
        if (!str.equals(string)) {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, string, configFile});
            config.set(path, string);
        }
    }

    public List<String> getSetStringList(String path, List<String> defaultList) throws InvalidConfigurationException {
        if (config.isList(path)) {
            List<?> unknownList = config.getList(path);
            List<String> stringList = new ArrayList<>(unknownList.size());
            for (Object obj : unknownList) {
                if (obj instanceof String) {
                    stringList.add((String) obj);
                } else if (obj instanceof Double || obj instanceof Integer || obj instanceof Boolean) {
                    stringList.add(obj.toString());
                } else {
                    throw new InvalidConfigurationException("Object " + obj + " found in list " + path + " in file " + configFile.toAbsolutePath() + " is not a string");
                }
            }
            return stringList;
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile + " is not a list");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultList, configFile});
            config.set(path, defaultList);
            return defaultList;
        }
    }

    public List<Long> getSetLongList(String path, List<Long> defaultList) throws InvalidConfigurationException {
        if (config.isList(path)) {
            List<?> unknownList = config.getList(path);
            List<Long> longList = new ArrayList<>(unknownList.size());
            for (Object obj : unknownList) {
                if (obj instanceof Number) {
                    longList.add(((Number) obj).longValue());
                } else {
                    throw new InvalidConfigurationException("Object " + obj + " found in list " + path + " in file " + configFile.toAbsolutePath() + " is not a number");
                }
            }
            return longList;
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile + " is not a list");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultList, configFile});
            config.set(path, defaultList);
            return defaultList;
        }

    }

    public Map<String, String> getSetStringMap(String path, Map<String, String> defaultValues) throws InvalidConfigurationException {
        if (config.isConfigurationSection(path)) {
            ConfigurationSection section = config.getConfigurationSection(path);
            Map<String, Object> entries = section.getValues(false);
            Map<String, String> values = new HashMap<>(entries.size());
            for (Map.Entry<String, Object> entry : entries.entrySet()) {
                Object obj = entry.getValue();
                if (obj instanceof String) {
                    values.put(entry.getKey(), (String) obj);
                } else if (obj instanceof Double || obj instanceof Integer || obj instanceof Boolean) {
                    values.put(entry.getKey(), obj.toString());
                } else {
                    throw new InvalidConfigurationException("Object " + obj + " found in map " + path + " in file " + configFile.toAbsolutePath() + " is not an integerr");
                }
            }
            return values;
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile + " is not a map");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultValues, configFile});
            ConfigurationSection section = config.createSection(path);
            for (Map.Entry<String, String> entry : defaultValues.entrySet()) {
                section.set(entry.getKey(), entry.getValue());
            }
            return defaultValues;
        }
    }

    public ConfigurationSection getSetSection(String path, Map<String, String> defaultValues) throws InvalidConfigurationException {
        if (config.isConfigurationSection(path)) {
            return config.getConfigurationSection(path);
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile + " is not a configuration section");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, defaultValues, configFile});
            ConfigurationSection section = config.createSection(path);
            for (Map.Entry<String, String> entry : defaultValues.entrySet()) {
                section.set(entry.getKey(), entry.getValue());
            }
            return section;
        }
    }

    public String[] getSetFixedArray(final String path, final String[] defaultValues) throws InvalidConfigurationException {
        if (config.isList(path)) {
            List<?> list = config.getList(path);
            if (list.isEmpty()) {
                config.set(path, Arrays.asList(defaultValues));
                return defaultValues.clone();
            } else if (list.size() < defaultValues.length) {
                throw new InvalidConfigurationException("Too few strings in list " + path + " in file " + configFile + ": expected " + defaultValues.length + ", found " + list.size() + ".");
            } else if (list.size() > defaultValues.length) {
                throw new InvalidConfigurationException("Too many strings in list " + path + " in file " + configFile + ": expected " + defaultValues.length + ", found " + list.size() + ".");
            }
            String[] result = new String[defaultValues.length];
            for (int i = 0; i < defaultValues.length; i++) {
                result[i] = String.valueOf(list.get(i));
            }
            return result;
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile + " is not an array.");
        } else {
            config.set(path, Arrays.asList(defaultValues));
            return defaultValues.clone();
        }
    }

    public void overwriteValue(String path, Object value) {
        logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, value, configFile});
        config.set(path, value);
    }

    public void removeValues(String... paths) {
        for (String path : paths) {
            removeValue(path);
        }
    }

    public void removeValue(String path) {
        if (config.contains(path)) {
            logger.log(Level.INFO, "Removing deprecated value {0} in file {1}", new Object[]{path, configFile});
            config.set(path, null);
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

}

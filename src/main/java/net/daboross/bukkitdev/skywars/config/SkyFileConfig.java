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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@RequiredArgsConstructor
public class SkyFileConfig {

    @Getter
    private final File configFile;
    @Getter
    @SuppressWarnings("NonConstantLogger")
    private final Logger logger;
    @Getter
    private YamlConfiguration config;

    public void load() throws IOException, InvalidConfigurationException {
        File folder = configFile.getParentFile();
        if (!folder.exists()) {
            boolean mkdirs = folder.mkdirs();
            if (!mkdirs) {
                throw new IOException("Couldn't make directory " + folder.getAbsolutePath());
            }
        } else if (!folder.isDirectory()) {
            throw new IOException("File " + folder.getAbsolutePath() + " is not a directory.");
        }
        if (!configFile.exists()) {
            try {
                boolean createNewFile = configFile.createNewFile();
                if (!createNewFile) {
                    throw new IOException("Couldn't make file " + configFile.getAbsolutePath());
                }
            } catch (IOException ex) {
                throw new IOException("Couldn't make file " + configFile.getAbsolutePath(), ex);
            }
        } else if (!configFile.isFile()) {
            throw new IOException("File or directory " + configFile.getAbsolutePath() + " is not a file");
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ex) {
            throw new IOException("Failed to load " + configFile.getAbsolutePath() + " as a YAML configuration", ex);
        } catch (InvalidConfigurationException ex) {
            throw new InvalidConfigurationException("Failed to load " + configFile.getAbsolutePath() + " as a YAML configuration", ex);
        }
    }

    public void save(String header) throws IOException {
        config.options().header(header).indent(2);
        try {
            config.save(configFile);
        } catch (IOException ex) {
            throw new IOException("Failed to save to " + configFile.getAbsolutePath(), ex);
        }
    }

    public int getSetInt(String path, int defaultInt) throws InvalidConfigurationException {
        if (config.isInt(path)) {
            return config.getInt(path);
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.getAbsolutePath() + " is not an integer");
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
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.getAbsolutePath() + " is not a long");
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
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.getAbsolutePath() + " is not a boolean (true/false)");
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
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile.getAbsolutePath() + " is not a boolean (true/false)");
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
                    throw new InvalidConfigurationException("Object " + obj + " found in list " + path + " in file " + configFile.getAbsolutePath() + " is not an integerr");
                }
            }
            return stringList;
        } else if (config.contains(path)) {
            throw new InvalidConfigurationException("Object " + config.get(path) + " found under " + path + " in file " + configFile + " is not a list");
        } else {
            logger.log(Level.INFO, "Setting {0} to {1} in file {2}", new Object[]{path, "an empty list", configFile});
            config.set(path, defaultList);
            return defaultList;
        }
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
}

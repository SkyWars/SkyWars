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
package net.daboross.bukkitdev.skywars.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.api.location.SkyLocationStore;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyPortalData;
import net.daboross.bukkitdev.skywars.api.location.SkySignData;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;

public class LocationStore implements Listener, SkyLocationStore {

    private final SkyWars plugin;
    private final List<SkyPortalData> portals = new ArrayList<>();
    private final List<SkySignData> allSigns = new ArrayList<>();
    private final Map<String, List<SkySignData>> signsByQueue = new HashMap<>();
    private final Map<SkyBlockLocation, SkySignData> signsByLocation = new HashMap<>();
    private SkyPlayerLocation lobbyPosition;
    private FileConfiguration storage;
    private Path configFile;

    public LocationStore(SkyWars plugin) throws SkyConfigurationException {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(SkyBlockLocation.class);
        ConfigurationSerialization.registerClass(SkyPlayerLocation.class);
        ConfigurationSerialization.registerClass(SkyBlockLocationRange.class);
        ConfigurationSerialization.registerClass(SkySignData.class);
        ConfigurationSerialization.registerClass(SkyPortalData.class);
        load();
    }

    private void load() throws SkyConfigurationException {
        if (configFile == null) {
            configFile = plugin.getDataFolder().toPath().resolve("locations.yml");
        }
        storage = YamlConfiguration.loadConfiguration(configFile.toFile());
        int configVersion = storage.getInt("storage-specification-version");
        if (configVersion > 2) {
            throw new SkyConfigurationException("Unknown configuration version for locations.yml. Did you downgrade? If so, delete or move locations.yml to reset.");
        }
        Object lobbyO = storage.get("lobby");
        if (lobbyO != null) {
            if (lobbyO instanceof SkyBlockLocation) {
                lobbyPosition = new SkyPlayerLocation((SkyBlockLocation) lobbyO);
            } else if (lobbyO instanceof SkyPlayerLocation) {
                lobbyPosition = (SkyPlayerLocation) lobbyO;
            } else {
                plugin.getLogger().log(Level.WARNING, "Expected SkyBlockLocation, found {} as lobby in {}! Removing item from config file.", new Object[]{lobbyO, configFile});
            }
        } else {
            List<World> worlds = Bukkit.getWorlds();
            if (worlds.isEmpty()) {
                lobbyPosition = null;
            } else {
                Location spawn = worlds.get(0).getSpawnLocation();
                lobbyPosition = new SkyPlayerLocation(spawn);
            }
        }
        List<?> portalList = storage.getList("portals");
        if (portalList != null) {
            for (Object obj : portalList) {
                if (obj instanceof SkyPortalData) {
                    portals.add((SkyPortalData) obj);
                } else if (obj instanceof SkyBlockLocation) {
                    portals.add(new SkyPortalData((SkyBlockLocation) obj, null));
                } else {
                    plugin.getLogger().log(Level.WARNING, "Expected SkyBlockLocation or SkyPortalData, found {} in portals list in {}! Removing item from config file.", new Object[]{obj, configFile});
                }
            }
        }

        List<?> signList = storage.getList("signs");
        if (signList != null) {
            for (Object object : signList) {
                // TODO: how to handle signs with old queue names??
                if (object instanceof SkySignData) {
                    allSigns.add((SkySignData) object);
                } else if (object instanceof SkyBlockLocation) {
                    allSigns.add(migrateOldBlockSign((SkyBlockLocation) object));
                } else {
                    plugin.getLogger().log(Level.WARNING, "Expected SkyBlockLocation or SkySignData, found {} in signs list in {}! Removing item from config file.", new Object[]{object, configFile});
                }
            }
        }
        for (SkySignData data : allSigns) {
            signsByLocation.put(data.location, data);
            List<SkySignData> inThisQueue = signsByQueue.get(data.queueName);
            if (inThisQueue == null) {
                inThisQueue = new ArrayList<>();
                signsByQueue.put(data.queueName, inThisQueue);
            }
            inThisQueue.add(data);
        }
    }

    private SkySignData migrateOldBlockSign(SkyBlockLocation loc) {
        plugin.getLogger().log(Level.INFO, "Migrating old sign at {} to new storage format! Downgrading the plugin from now on out will unregister this sign.", new Object[]{loc});
        return new SkySignData(loc, null, null);
    }

    @Override
    public void save() {
        if (storage != null) {
            plugin.getLogger().log(Level.INFO, "Saving configuration");
            storage.set("portals", portals);
            storage.set("lobby", lobbyPosition);
            storage.set("signs", allSigns);
            storage.set("storage-specification-version", 2);
            try {
                storage.save(configFile.toFile());
            } catch (IOException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to save to location store", ex);
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "For some reason storage is trying to save when storage was never loaded");
        }
    }

    @Override
    public SkySignData getSignAt(final SkyBlockLocation loc) {
        return signsByLocation.get(loc);
    }

    @Override
    public List<SkySignData> getQueueSigns(final String queueName) {
        if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
            return signsByQueue.get(queueName);
        } else {
            return allSigns;
        }
    }

    @Override
    public void registerSign(SkySignData data) {
        if (signsByLocation.containsKey(data.location)) {
            removeSign(signsByLocation.get(data.location));
        }
        allSigns.add(data);
        signsByLocation.put(data.location, data);
        signsByQueue.get(data.queueName).remove(data);
    }

    @Override
    public void removeSign(final SkySignData data) {
        signsByLocation.remove(data.location);
        signsByQueue.get(data.queueName).remove(data);
        allSigns.remove(data);
    }

    @Override
    public SkyPlayerLocation getLobbyPosition() {
        if (lobbyPosition == null || Bukkit.getWorld(lobbyPosition.world) == null) {
            List<World> worlds = Bukkit.getWorlds();
            plugin.getLogger().log(Level.WARNING, "Lobby location not defined, or lobby world not loaded! Creating new lobby location from {0}'s spawn.", worlds.get(0).getSpawnLocation());
            lobbyPosition = new SkyPlayerLocation(worlds.get(0).getSpawnLocation());
        }
        return lobbyPosition;
    }

    @Override
    public void setLobbyPosition(SkyPlayerLocation lobbyPosition) {
        Validate.notNull(lobbyPosition, "Lobby position cannot be null");
        this.lobbyPosition = lobbyPosition;
    }

    @Override
    public List<SkyPortalData> getPortals() {
        return portals;
    }
}

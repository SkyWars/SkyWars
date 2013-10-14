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
package net.daboross.bukkitdev.skywars.storage;

import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.api.location.SkyLocationStore;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;
import net.daboross.bukkitdev.skywars.world.Statics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class LocationStore implements Listener, SkyLocationStore {

    private final JavaPlugin plugin;
    private final List<SkyBlockLocation> portals = new ArrayList<>();
    private SkyPlayerLocation lobbyPosition;
    private FileConfiguration storage;
    private File configFile;

    public LocationStore(JavaPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(SkyBlockLocation.class);
        ConfigurationSerialization.registerClass(SkyPlayerLocation.class);
        ConfigurationSerialization.registerClass(SkyBlockLocationRange.class);
        load();
    }

    private void load() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "locations.yml");
        }
        storage = YamlConfiguration.loadConfiguration(configFile);
        Object lobbyO = storage.get("lobby");
        if (lobbyO != null) {
            if (lobbyO instanceof SkyBlockLocation) {
                lobbyPosition = new SkyPlayerLocation((SkyBlockLocation) lobbyO);
            } else if (lobbyO instanceof SkyPlayerLocation) {
                lobbyPosition = (SkyPlayerLocation) lobbyO;
            } else {
                plugin.getLogger().warning("Lobby is not ArenaLocation");
            }
        } else {
            List<World> worlds = Bukkit.getWorlds();
            if (worlds.isEmpty()) {
                lobbyPosition = new SkyPlayerLocation(0, 0, 0, Statics.ARENA_WORLD_NAME);
            } else {
                Location spawn = worlds.get(0).getSpawnLocation();
                lobbyPosition = new SkyPlayerLocation(spawn);
            }
        }
        List<?> list = storage.getList("portals");
        if (list
                != null) {
            for (Object obj : list) {
                if (obj instanceof SkyBlockLocation) {
                    portals.add((SkyBlockLocation) obj);
                } else {
                    plugin.getLogger().warning("Non-ArenaLocation found in portals list");
                }
            }
        }
    }

    @Override
    public void save() {
        if (storage != null) {
            plugin.getLogger().log(Level.INFO, "Saving configuration");
            storage.set("portals", portals);
            storage.set("lobby", lobbyPosition);
            try {
                storage.save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to save to location store", ex);
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "For some reason storage is trying to save when storage was never loaded");
        }
    }

    @Override
    public SkyPlayerLocation getLobbyPosition() {
        return lobbyPosition;
    }

    @Override
    public void setLobbyPosition(@NonNull SkyPlayerLocation lobbyPosition) {
        this.lobbyPosition = lobbyPosition;
    }

    @Override
    public List<SkyBlockLocation> getPortals() {
        return portals;
    }
}

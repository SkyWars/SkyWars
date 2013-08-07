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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.world.Statics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author daboross
 */
public class LocationStore {

    private final JavaPlugin plugin;
    private final List<SkyLocation> portals = new ArrayList<SkyLocation>();
    private SkyLocation lobbyPosition;
    private FileConfiguration storage;
    private File configFile;

    public LocationStore(JavaPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(SkyLocation.class);
        load();
    }

    private void load() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "locations.yml");
        }
        storage = YamlConfiguration.loadConfiguration(configFile);
        Object lobbyO = storage.get("lobby");
        if (lobbyO != null) {
            if (lobbyO instanceof SkyLocation) {
                lobbyPosition = (SkyLocation) lobbyO;
            } else {
                plugin.getLogger().warning("Lobby is not ArenaLocation");
            }
        } else {
            lobbyPosition = new SkyLocation(0, 0, 0, Statics.ARENA_WORLD_NAME);
        }
        List<?> list = storage.getList("portals");
        if (list
                != null) {
            for (Object obj : list) {
                if (obj instanceof SkyLocation) {
                    portals.add((SkyLocation) obj);
                } else {
                    plugin.getLogger().warning("Non-ArenaLocation found in portals list");
                }
            }
        }
    }

    public void save() {
        if (storage != null) {
            storage.set("portals", portals);
            storage.set("lobby", lobbyPosition);
            try {
                storage.save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to save to location store", ex);
            }
        }
    }

    public SkyLocation getLobbyPosition() {
        return lobbyPosition;
    }

    public void setLobbyPosition(SkyLocation lobbyPosition) {
        this.lobbyPosition = lobbyPosition;
    }

    public List<SkyLocation> getPortals() {
        return portals;
    }
}

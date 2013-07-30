package net.daboross.bukkitdev.skywars.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
    private final List<ArenaLocation> portals = new ArrayList<ArenaLocation>();
    private ArenaLocation lobbyPosition;
    private FileConfiguration storage;
    private File configFile;

    public LocationStore(JavaPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(ArenaLocation.class);
        load();
    }

    private void load() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "locations.yml");
        }
        storage = YamlConfiguration.loadConfiguration(configFile);
        Object lobbyO = storage.get("lobby");
        if (lobbyO != null) {
            if (lobbyO instanceof ArenaLocation) {
                lobbyPosition = (ArenaLocation) lobbyO;
            } else {
                plugin.getLogger().warning("Lobby is not ArenaLocation");
            }
        }
        List<?> list = storage.getList("portals");
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof ArenaLocation) {
                    portals.add((ArenaLocation) obj);
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

    public ArenaLocation getLobbyPosition() {
        return lobbyPosition;
    }

    public void setLobbyPosition(ArenaLocation lobbyPosition) {
        this.lobbyPosition = lobbyPosition;
    }

    public List<ArenaLocation> getPortals() {
        return portals;
    }
}

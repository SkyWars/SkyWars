package net.daboross.bukkitdev.skywars.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 *
 * @author daboross
 */
@SerializableAs("CopsAndRobberLocation")
public class ArenaLocation implements ConfigurationSerializable {

    public final int x;
    public final int y;
    public final int z;
    public final String world;

    public ArenaLocation(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public ArenaLocation(Block block) {
        this(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }

    public ArenaLocation(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    public boolean isSame(Block block) {
        return block != null && x == block.getX() && y == block.getY() && z == block.getZ();
    }

    public Location toLocation() {
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            Bukkit.getLogger().log(Level.WARNING, "[CopsAndRobbers] World ''{0}'' not found!", world);
            return null;
        }
        return new Location(bukkitWorld, x, y, z);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("xpos", x);
        map.put("ypos", y);
        map.put("zpos", z);
        map.put("world", world);
        return map;
    }

    public static ArenaLocation deserialize(Map<String, Object> map) {
        Object xObject = map.get("xpos"),
                yObject = map.get("ypos"),
                zObject = map.get("zpos"),
                worldObject = map.get("world");
        if (xObject == null || yObject == null || zObject == null || worldObject == null
                || !(xObject instanceof Integer)
                || !(yObject instanceof Integer)
                || !(zObject instanceof Integer)) {
            return null;
        }
        Integer x = (Integer) xObject, y = (Integer) yObject, z = (Integer) zObject;
        String worldString = worldObject.toString();
        return new ArenaLocation(x, y, z, worldString);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArenaLocation)) {
            return false;
        }
        ArenaLocation l = (ArenaLocation) obj;
        return l.x == x && l.y == y && l.z == z && l.world.equals(world);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        hash = 79 * hash + this.z;
        hash = 79 * hash + (this.world != null ? this.world.hashCode() : 0);
        return hash;
    }
}

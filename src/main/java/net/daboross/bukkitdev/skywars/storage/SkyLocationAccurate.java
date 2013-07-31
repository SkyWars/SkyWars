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
import org.bukkit.entity.Entity;

/**
 *
 * @author daboross
 */
@SerializableAs("SkyLocationAccurate")
public class SkyLocationAccurate implements ConfigurationSerializable {

    public final double x;
    public final double y;
    public final double z;
    public final String world;

    public SkyLocationAccurate(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public SkyLocationAccurate(Block block) {
        this(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }

    public SkyLocationAccurate(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    public SkyLocationAccurate(Entity entity) {
        this(entity.getLocation());
    }

    public SkyLocationAccurate add(double x, double y, double z) {
        return new SkyLocationAccurate(this.x + x, this.y + y, this.z + z, world);
    }

    public SkyLocation round() {
        return new SkyLocation((int) x, (int) y, (int) z, world);
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

    public static SkyLocationAccurate deserialize(Map<String, Object> map) {
        Object worldO = map.get("world");
        Double x = get(map.get("xpos")),
                y = get(map.get("ypos")),
                z = get(map.get("zpos"));
        if (x == null || y == null || z == null || worldO == null
                || !(x instanceof Double)
                || !(y instanceof Double)
                || !(z instanceof Double)) {
            return null;
        }
        String world = worldO.toString();
        return new SkyLocationAccurate(x, y, z, world);
    }

    private static Double get(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Integer) {
            return Double.valueOf(((Integer) o).doubleValue());
        }
        if (o instanceof Double) {
            return (Double) o;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SkyLocation)) {
            return false;
        }
        SkyLocation l = (SkyLocation) obj;
        return l.x == x && l.y == y && l.z == z && l.world.equals(world);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        hash = 97 * hash + (this.world != null ? this.world.hashCode() : 0);
        return hash;
    }
}

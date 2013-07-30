/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.world;

import java.util.Arrays;
import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author daboross
 */
public class WorldCopier {

    private static final SkyLocation warriorsMin = new SkyLocation(-205, 183, 78, "SkyworldWarriors");
    private static final SkyLocation warriorsMax = new SkyLocation(-149, 190, 136, "SkyworldWarriors");

    public static void copyArena(SkyLocation toMin) {
    }

    public static void copy(SkyLocation fromMin, SkyLocation fromMax, SkyLocation toMin) {
        if (!fromMin.world.equals(fromMax.world)) {
            throw new IllegalArgumentException();
        }
        World fromWorld = Bukkit.getWorld(fromMin.world);
        if (fromWorld == null) {
            throw new IllegalArgumentException();
        }
        World toWorld = Bukkit.getWorld(toMin.world);
        if (toWorld == null) {
            throw new IllegalArgumentException();
        }
        int xLength = fromMax.x - fromMin.x;
        int yLength = fromMax.y - fromMin.y;
        int zLength = fromMax.z - fromMin.z;
        for (int x = 0; x <= xLength; x++) {
            for (int y = 0; y <= yLength; y++) {
                for (int z = 0; z <= zLength; z++) {
                    Block from = fromWorld.getBlockAt(fromMin.x + x, fromMin.y + y, fromMin.z + z);
                    Block to = toWorld.getBlockAt(toMin.x + x, toMin.y + y, toMin.z + z);
                    to.setType(from.getType());
                    to.setData(from.getData());
                    BlockState fromState = from.getState();
                    if (fromState instanceof Chest) {
                        Chest toChest = (Chest) to.getState();
                        Chest fromChest = (Chest) fromState;
                        ItemStack[] contents = fromChest.getBlockInventory().getContents();
                        toChest.getBlockInventory().setContents(Arrays.copyOf(contents, contents.length));
                        toChest.update(true);
                    }
                }
            }
        }
    }
}

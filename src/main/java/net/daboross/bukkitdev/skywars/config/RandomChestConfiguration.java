/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.RandomChests;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.kits.SkyKitItem;
import net.daboross.bukkitdev.skywars.api.kits.impl.SkyKitItemConfig;
import net.daboross.bukkitdev.skywars.kits.SkyKitDecoder;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

public class RandomChestConfiguration implements RandomChests {

    private final SkyWars plugin;
    private final Random random;
    private List<ChestLevel> levels = new ArrayList<>();

    public RandomChestConfiguration(final SkyWars plugin) throws IOException, InvalidConfigurationException, SkyConfigurationException {
        this.plugin = plugin;
        this.random = new Random();
        load();
    }

    private void load() throws IOException, InvalidConfigurationException, SkyConfigurationException {
        SkyStatic.debug("Loading chests.yml");
        Path chestsFile = plugin.getDataFolder().toPath().resolve("chests.yml");
        if (!Files.exists(chestsFile)) {
            plugin.saveResource("chests.yml", true);
        }
        YamlConfiguration config = new YamlConfiguration();

        config.load(chestsFile.toFile());

        int version = config.getInt("version", 1);
        if (version > 1) {
            throw new InvalidConfigurationException("Future version in chests.yml!");
        }

        levels.clear();

        HashMap<String, ChestLevel> incompleteLevels = new HashMap<>();

        ConfigurationSection levelsSection = config.getConfigurationSection("levels");
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (levelsSection == null) {
            plugin.getLogger().log(Level.WARNING, "Not loading chests.yml: no levels section found");
            return;
        }
        if (itemsSection == null) {
            plugin.getLogger().log(Level.WARNING, "Not loading chests.yml: no items section found");
            return;
        }

        for (String key : levelsSection.getKeys(false)) {
            if (levelsSection.isConfigurationSection(key)) {
                // is this bad? I'm not sure. it's a hack, for sure - but it does let us use getSetInt(),
                // and it allows us to display accurate paths in case of error.
                ConfigurationSection levelSection = levelsSection.getConfigurationSection(key);
                if (!levelSection.isInt("item-value")) {
                    throw new SkyConfigurationException("Invalid chests.yml: level `" + key + "` is missing item-value!");
                }
                if (!levelSection.isInt("chance")) {
                    throw new SkyConfigurationException("Invalid chests.yml: level `" + key + "` is missing chance!");
                }
                int itemValue = levelSection.getInt("item-value");
                int chance = levelSection.getInt("chance");
                incompleteLevels.put(key, new ChestLevel(itemValue, chance, null));
            } else {
                throw new SkyConfigurationException("Invalid chests.yml: non-map thing in levels: " + levelsSection.get(key));
            }
        }

        for (String key : itemsSection.getKeys(false)) {
            if (itemsSection.isList(key)) {
                ChestLevel incompleteLevel = incompleteLevels.remove(key);
                if (incompleteLevel == null) {
                    throw new SkyConfigurationException("Invalid chests.yml: level `" + key + "` has a section under items, but no section under levels!");
                }
                List<?> objectList = itemsSection.getList(key);
                List<SkyKitItem> itemList = new ArrayList<>(objectList.size());
                for (Object o : objectList) {
                    if (o instanceof Map) {
                        //noinspection unchecked
                        itemList.add(SkyKitDecoder.decodeItem((Map<String, Object>) o));
                    } else if (o instanceof String) {
                        String string = o.toString();
                        String materialString;
                        int amount;
                        if (!string.contains(",")) {
                            materialString = string;
                            amount = 1;
                        } else {
                            String[] split = string.split(",", 2);
                            materialString = split[0];
                            try {
                                amount = Integer.parseInt(split[1]);
                            } catch (NumberFormatException ex) {
                                throw new SkyConfigurationException("Invalid amount number for item in chests.yml: not an integer: " + split[1]);
                            }
                        }
                        Material material = Material.matchMaterial(materialString);
                        if (material == null) {
                            throw new SkyConfigurationException("Error in chests.yml: the type string '" + materialString + "' is invalid. Check https://dabo.guru/projects/skywars/configuring-kits for a list of valid material names (at the bottom of the page).");
                        }
                        itemList.add(new SkyKitItemConfig(material, amount, null, null));
                    } else {
                        throw new SkyConfigurationException("Invalid thing in items list for level in chests.yml: " + o);
                    }
                }
                if (itemList.isEmpty()) {
                    throw new SkyConfigurationException("Invalid chests.yml: level `" + key + "` items list is empty!");
                }
                levels.add(new ChestLevel(incompleteLevel.itemValue, incompleteLevel.chance, itemList));
            } else {
                throw new SkyConfigurationException("Invalid chests.yml: non-list thing in items: " + itemsSection.get(key));
            }
        }

        if (!incompleteLevels.isEmpty()) {
            if (incompleteLevels.size() == 1) {
                throw new SkyConfigurationException("Invalid chests.yml: level " + incompleteLevels.keySet().iterator().next() + " has a section under levels, but no section under items!");
            } else {
                throw new SkyConfigurationException("Invalid chests.yml: multiple levels (" + new ArrayList<>(incompleteLevels.keySet()) + ") have sections under levels but no sections under items!");
            }
        }
    }

    @Override
    public void fillChest(final Inventory inventory, final int chestLevel, final int minValue, final int maxValue) {
        int totalChance = 0;
        List<ChestLevel> acceptableLevels = new ArrayList<>(levels);
        for (ChestLevel level : levels) {
            if (level.itemValue >= minValue && level.itemValue <= maxValue) {
                acceptableLevels.add(level);
                totalChance += level.chance;
            }
        }
        if (acceptableLevels.isEmpty()) {
            SkyStatic.log(Level.SEVERE, "Warning: No acceptable chest levels found when filling chest with minValue={0}, maxValue={1}! Chest will be completely empty.", minValue, maxValue);
            return;
        }
        int totalValue = 0;
        while (totalValue <= chestLevel) {
            ChestLevel level = null;
            int chanceIndex = random.nextInt(totalChance);
            int accumulatedChance = 0;
            // This is a somewhat convoluted way to give each level the right probability of being picked.
            for (ChestLevel testLevel : acceptableLevels) {
                accumulatedChance += testLevel.chance;
                if (accumulatedChance > chanceIndex) {
                    level = testLevel;
                    break;
                }
            }
            Validate.notNull(level, "Never null"); // should never be null
            SkyKitItem item = level.items.get(random.nextInt(level.items.size()));
            inventory.addItem(item.toItem());

            totalValue += level.itemValue;
        }
    }

    private static class ChestLevel {

        private final int itemValue;
        private final int chance;
        private final List<SkyKitItem> items;

        private ChestLevel(final int itemValue, final int chance, final List<SkyKitItem> items) {
            this.itemValue = itemValue;
            this.chance = chance;
            this.items = items;
        }
    }
}

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
package net.daboross.bukkitdev.skywars.kits;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.kits.SkyKits;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkyKitConfiguration implements SkyKits {

    private final SkyWars plugin;
    private final Map<String, SkyKit> kits = new LinkedHashMap<>();
    private final List<SkyKit> disabledKits = new ArrayList<>();

    public SkyKitConfiguration(SkyWars plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        load();
    }

    private void load() throws IOException, InvalidConfigurationException {
        SkyStatic.debug("Loading kits");
        Path kitFile = plugin.getDataFolder().toPath().resolve("kits.yml");
        if (!Files.exists(kitFile)) {
            plugin.saveResource("kits.yml", true);
        }
        YamlConfiguration config = new YamlConfiguration();
        config.load(kitFile.toFile());
        if (config.getInt("configuration-version") <= 1) {
            if (config.getInt("configuration-version") <= 0) {
                updateVersion0To1(config);
            }
            updateVersion1To2(config);
            config.options().header(String.format(KIT_HEADER)).indent(2);
            config.save(kitFile.toFile());
        }

        for (String key : config.getKeys(false)) {
            if (key.equals("configuration-version")) continue;
            if (config.isConfigurationSection(key)) {
                SkyKit kit;
                try {
                    kit = SkyKitDecoder.decodeKit(config.getConfigurationSection(key), key);
                } catch (SkyConfigurationException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Error loading kit! " + key + " won't be accessible until this is fixed!", ex);
                    continue;
                }
                if (kit.getCost() != 0 && plugin.getEconomyHook() == null) {
                    plugin.getLogger().log(Level.FINE, "Not enabling kit {0} due to it having a cost and economy support not being enabled.", key);
                    disabledKits.add(kit);
                    continue;
                }
                kits.put(ChatColor.stripColor(key.toLowerCase()), kit);
                SkyStatic.debug("Loaded kit %s", kit);
            } else {
                plugin.getLogger().log(Level.WARNING, "There is a non-kit value in the kits.yml file ''{0}''.", config.get(key));
            }
        }
    }

    private void updateVersion0To1(FileConfiguration config) throws IOException, InvalidConfigurationException {
        FileConfiguration defaultConfig = new YamlConfiguration();
        try (InputStream stream = plugin.getResourceAsStream("kits.yml"); Reader reader = new InputStreamReader(stream)) {
            defaultConfig.load(reader);
        }

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            ConfigurationSection defaultSection = defaultConfig.getConfigurationSection(key);
            if (section == null) {
                // This will be warned against when actually loading the configuration.
                // For now, let's just focus on updating the values which we can access.
                continue;
            }
            if (!section.contains("description")) {
                if (defaultSection != null && defaultSection.contains("description")) {
                    section.set("description", defaultSection.get("description"));
                } else {
                    section.set("description", SkyTrans.get(TransKey.CONFIG_KIT_DEFAULT_DESCRIPTION, key));
                }
            }
            if (!section.contains("totem")) {
                if (defaultSection != null && defaultSection.contains("totem")) {
                    section.set("totem", defaultSection.get("totem"));
                } else {
                    section.set("totem", KitConstants.DEFAULT_TOTEM.toString());
                }
            }
        }
        config.set("configuration-version", 1);
    }

    private void updateVersion1To2(FileConfiguration config) throws IOException, InvalidConfigurationException {
        FileConfiguration defaultConfig = new YamlConfiguration();
        try (InputStream stream = plugin.getResourceAsStream("kits.yml"); Reader reader = new InputStreamReader(stream)) {
            defaultConfig.load(reader);
        }

        for (String key : config.getKeys(false)) {
            ConfigurationSection kitSection = config.getConfigurationSection(key);
            if (kitSection == null) {
                // This will be warned against when actually loading the configuration.
                // For now, let's just focus on updating the values which we can access.
                continue;
            }
            List<Map<?, ?>> items = kitSection.getMapList("items");
            for (Map<?, ?> itemMap : items) {
                if (itemMap.containsKey("potion")) {
                    Object potionMapO = itemMap.get("potion");
                    if (!(potionMapO instanceof Map)) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    Map<String, Object> potionMap = (Map<String, Object>) potionMapO;
                    if (potionMap.containsKey("level")) {
                        int level = Integer.parseInt(String.valueOf(potionMap.get("level")));
                        if (level > 1) {
                            potionMap.put("upgraded", true);
                        }
                        potionMap.remove("level");
                    }
                }
            }
        }
        config.set("configuration-version", 2);
    }

    @Override
    public void save() throws IOException {
        Path kitFile = plugin.getDataFolder().toPath().resolve("kits.yml");
        if (!Files.exists(kitFile)) {
            plugin.saveResource("kits.yml", true);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(kitFile.toFile());
        for (SkyKit kit : kits.values()) {
            SkyKitEnconder.encodeKit(kit, config);
        }
        for (SkyKit kit : disabledKits) {
            SkyKitEnconder.encodeKit(kit, config);
        }
        config.options().header(String.format(KIT_HEADER)).indent(2);
        config.save(kitFile.toFile());
    }

    @Override
    public void addKit(SkyKit kit) {
        Validate.notNull(kit);
        Validate.isTrue(!kits.containsKey(kit.getName()), "Kit already exists!");
        kits.put(kit.getName().toLowerCase(), kit);
    }

    @Override
    public Set<String> getKitNames() {
        return Collections.unmodifiableSet(kits.keySet());
    }

    @Override
    public Collection<SkyKit> getAllKits() {
        return Collections.unmodifiableCollection(kits.values());
    }

    @Override
    public SkyKit getKit(String name) {
        return kits.get(ChatColor.stripColor(name.toLowerCase()));
    }

    @Override
    public List<SkyKit> getAvailableKits(Player p) {
        List<SkyKit> list = new ArrayList<>(kits.size() > 10 ? kits.size() / 2 : kits.size());
        for (SkyKit kit : kits.values()) {
            String perm = kit.getPermission();
            int cost = kit.getCost();
            if ((perm == null || p.hasPermission(perm)) && (cost == 0 || plugin.getEconomyHook().canAfford(p, cost))) {
                list.add(kit);
            }
        }
        return list;
    }

    @Override
    public List<SkyKit> getUnavailableKits(Player p) {
        List<SkyKit> list = new ArrayList<>(kits.size() > 10 ? kits.size() / 2 : kits.size());
        for (SkyKit kit : kits.values()) {
            String perm = kit.getPermission();
            int cost = kit.getCost();
            if ((perm != null && !p.hasPermission(perm)) || (cost != 0 && !plugin.getEconomyHook().canAfford(p, cost))) {
                list.add(kit);
            }
        }
        return list;
    }

    private static final String KIT_HEADER = "####### kits.yml #######%n" +
            "%n" +
            "Kit configuration%n" +
            "#%n" +
            "For documentation, please visit%n" +
            "https://dabo.guru/projects/skywars/configuring-kits%n" +
            "#########";
}

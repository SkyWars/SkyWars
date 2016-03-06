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
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkyKitConfiguration implements SkyKits {

    private final SkyWars plugin;
    private final Map<String, SkyKit> kits = new LinkedHashMap<>();

    public SkyKitConfiguration(SkyWars plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        SkyStatic.debug("Loading kits");
        Path kitFile = plugin.getDataFolder().toPath().resolve("kits.yml");
        if (!Files.exists(kitFile)) {
            plugin.saveResource("kits.yml", true);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile.toFile());
        for (String key : config.getKeys(false)) {
            if (config.isConfigurationSection(key)) {
                SkyKit kit;
                try {
                    kit = SkyKitDecoder.decodeKit(config.getConfigurationSection(key), key);
                } catch (SkyConfigurationException ex) {
                    plugin.getLogger().log(Level.SEVERE, ex.getMessage());
                    continue;
                }
                if (kit.getCost() != 0 && plugin.getEconomyHook() == null) {
                    plugin.getLogger().log(Level.WARNING, "Not enabling kit {0} due to it having a cost and economy support not being enabled.", key);
                    continue;
                }
                kits.put(key.toLowerCase(), kit);
                SkyStatic.debug("Loaded kit %s", kit);
            } else {
                plugin.getLogger().log(Level.WARNING, "There is a non-kit value in the kits.yml file ''{0}''.", config.get(key));
            }
        }
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
        return kits.get(name.toLowerCase());
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

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
package net.daboross.bukkitdev.skywars.world;

import java.util.HashMap;
import java.util.Map;
import net.daboross.bukkitdev.bukkitstorageprotobuf.MemoryBlockArea;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;

public class LoadedArena {

    private final SkyArenaConfig arenaConfig;
    private final MemoryBlockArea cachedBlocks;
    private final Map<SkyBlockLocation, ChestConfig> chestsToFill;

    public LoadedArena(final SkyArenaConfig config, final MemoryBlockArea blocks) {
        arenaConfig = config;
        cachedBlocks = blocks;
        chestsToFill = new HashMap<>();
        loadChests();
    }

    private void loadChests() {
    }

    public static class ChestConfig {

    }
}

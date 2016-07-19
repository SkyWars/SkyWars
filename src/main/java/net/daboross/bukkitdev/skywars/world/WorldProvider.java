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
package net.daboross.bukkitdev.skywars.world;

import java.io.IOException;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import org.bukkit.World;

public interface WorldProvider {

    void loadArena(SkyArenaConfig arena, boolean forceReload) throws IOException;

    void clearLoadedArenas();

    /**
     * Starts a multi-part copy operation of the arena, which will complete in around ticksTillCompletion.
     * <p>
     * If you *need* it to be done at a certain time, set another timer for that time and run {@code
     * operation.completeOperationNow()} on the returned operation.
     *
     * @param arenaWorld          The world to copy to.
     * @param arena               The arena to copy.
     * @param target              The target location to copy the arena to,
     * @param ticksTillCompletion Time (in server ticks) before the provider should try and finish the copying
     *                            completely
     * @return an operation handle, which can be used to complete or cancel the copy operation.
     */
    OperationHandle startCopyOperation(World arenaWorld, SkyArena arena, SkyBlockLocation target, long ticksTillCompletion);

    void copyArena(World arenaWorld, SkyArena arena, SkyBlockLocation target);

    /**
     * Starts a multi-part destroy operation, similar to startCopyOperation().
     * <p>
     * This won't neccessarily be done by ticksTillCompletion, but it will be done shortly after if it isn't.
     *
     * @param arenaWorld          The world to copy to.
     * @param arena               The arena to get destruction parameters from
     * @param target              The target location which the arena was copied to.
     * @param ticksTillCompletion Time (in server ticks) with which to give the provider to complete the operation.
     * @return an operation handle, which can be used to complete or cancel the copy operation.
     */
    OperationHandle startDestroyOperation(World arenaWorld, SkyArena arena, SkyBlockLocation target, long ticksTillCompletion);

    void destroyArena(World arenaWorld, SkyArena arena, SkyBlockLocation target);
}

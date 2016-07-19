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

import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;

public interface OperationHandle {

    /**
     * Cancels the operation, without completing any more steps/parts!
     * <p>
     * It is recommended to only perform this on a copy operation, and only do so if you are going to immediately do a
     * clear operation in the same place.
     */
    void cancelOperation();

    /**
     * Forces the operation to complete now. If the operation has already completed, this will return immediately.
     * <p>
     * If the operation hasn't completed, all remaining steps will be taken immediately. All tasks to run on completion
     * will also be run, in this thread.
     */
    void completeOperationNow();

    /**
     * Sets the value to return with getTargetLocationId(). Value is not used internally at all.
     *
     * @param locationId the id to return when getTargetLocationId() is returned.
     */
    void setTargetLocationId(final int locationId);

    /**
     * Gets the id set by setTargetLocationId(int).
     *
     * @return the location id.
     */
    int getTargetLocationId();

    /**
     * Gets the "zero location," or the operation target.
     *
     * @return the zero / minimum location.
     */
    SkyBlockLocation getZeroLocation();

    /**
     * Runs the given runnable on operation finish. Runs in bukkit thread, in sync.
     * <p>
     * If already finished, runnable will be immediately run.
     *
     * @param runnable Runnable to run when finished.
     */
    void runOnFinish(Runnable runnable);
}

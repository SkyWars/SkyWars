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
package net.daboross.bukkitdev.skywars.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class GenericTimer {

    private final Plugin plugin;
    private final boolean runTaskAsync;
    private final TaskChainPart firstTask;
    private final Object taskLock = new Object();
    private int bukkitTaskId = -1;


    public GenericTimer(Plugin plugin, List<TaskDefinition> tasksBeforeFinish, boolean runTaskAsync) {
        this.plugin = plugin;
        this.runTaskAsync = runTaskAsync;
        List<TaskDefinition> tasks = new ArrayList<>(tasksBeforeFinish);
        Collections.sort(tasks);
        this.firstTask = new TaskChainPart(tasks);
    }

    /**
     * Starts the timer to end in a given number of seconds.
     * This will cancel all other times the timer has been started.
     *
     * @param seconds The number of seconds till a task with secondsBeforeEndToExecuteThis=0.
     */
    public void startIn(long seconds) {
        synchronized (taskLock) {
            if (bukkitTaskId != -1) {
                SkyStatic.debug("[Timer] Canceling task!");
                plugin.getServer().getScheduler().cancelTask(bukkitTaskId);
                // bukkitTaskId is set to another value in runnable.start(long).
            }
            GenericTimerRunnable runnable = new GenericTimerRunnable(firstTask);
            runnable.start(seconds);
        }
    }

    public void cancelAll() {
        synchronized (taskLock) {
            if (bukkitTaskId != -1) {
                SkyStatic.debug("[Timer] Canceling task!");
                plugin.getServer().getScheduler().cancelTask(bukkitTaskId);
                bukkitTaskId = -1;
            }
        }
    }

    private class GenericTimerRunnable implements Runnable {
        private TaskChainPart nextTask;

        public GenericTimerRunnable(TaskChainPart firstTask) {
            nextTask = firstTask;
        }

        public void start(long secondsTillEnd) {
            nextTask = nextTask.getNextTaskForRemainingTime(secondsTillEnd);
            synchronized (taskLock) {
                while (nextTask != null && nextTask.secondsBeforeEndToExecuteThis == secondsTillEnd) {
                    nextTask.executeNow();
                    nextTask = nextTask.nextPart;
                }
                if (nextTask == null) {
                    bukkitTaskId = -1;
                } else {
                    long secondsTillNextTask = secondsTillEnd - nextTask.secondsBeforeEndToExecuteThis;
                    SkyStatic.debug("[Timer] Waiting %s seconds!", secondsTillNextTask);
                    long ticksTillNextTask = secondsTillNextTask * 20;
                    bukkitTaskId = plugin.getServer().getScheduler()
                            .runTaskLaterAsynchronously(plugin, this, ticksTillNextTask).getTaskId();
                }
            }
        }

        @Override
        public void run() {
            start(nextTask.secondsBeforeEndToExecuteThis);
        }
    }

    private class TaskChainPart {
        private final long secondsBeforeEndToExecuteThis;
        private final TaskChainPart nextPart;
        private final Runnable runnable;

        public TaskChainPart(List<TaskDefinition> sortedRemainingTasks) {
            if (sortedRemainingTasks.isEmpty()) {
                this.runnable = null;
                this.nextPart = null;
                this.secondsBeforeEndToExecuteThis = 0;
            } else {
                TaskDefinition definition = sortedRemainingTasks.remove(sortedRemainingTasks.size() - 1);
                this.runnable = definition.runnableToExecute;
                this.secondsBeforeEndToExecuteThis = definition.secondsBeforeEndToExecuteThis;
                this.nextPart = new TaskChainPart(sortedRemainingTasks); // Already removed one.
            }
        }

        /**
         * Returns the next task that would execute with the remaining seconds.
         *
         * @param secondsRemaining Time till timer end.
         * @return The first task in the task chain (maybe this) for which
         * secondsRemaining => secondsBeforeEndToExecuteThis
         */
        public TaskChainPart getNextTaskForRemainingTime(long secondsRemaining) {
            Validate.isTrue(secondsRemaining >= 0);
            if (secondsRemaining >= secondsBeforeEndToExecuteThis) {
                return this;
            } else {
                return nextPart.getNextTaskForRemainingTime(secondsRemaining);
            }
        }

        public void executeNow() {
            if (runnable != null) {
                SkyStatic.debug("[Timer] Executing task!");
                BukkitScheduler sc = plugin.getServer().getScheduler();
                if (runTaskAsync) {
                    sc.runTaskAsynchronously(plugin, runnable);
                } else {
                    sc.runTask(plugin, runnable);
                }
            } else {
                SkyStatic.debug("[Timer] Null runnable!");
            }
        }
    }

    public static class TaskDefinition implements Comparable<TaskDefinition> {
        private final long secondsBeforeEndToExecuteThis;
        private final Runnable runnableToExecute;

        public TaskDefinition(long secondsBeforeEndToExecuteThis, Runnable runnableToExecute) {
            this.secondsBeforeEndToExecuteThis = secondsBeforeEndToExecuteThis;
            this.runnableToExecute = runnableToExecute;
        }

        @Override
        public int compareTo(TaskDefinition other) {
            long diff = this.secondsBeforeEndToExecuteThis - other.secondsBeforeEndToExecuteThis;
            return diff == 0 ? 0 : diff < 0 ? -1 : 1;
        }
    }
}

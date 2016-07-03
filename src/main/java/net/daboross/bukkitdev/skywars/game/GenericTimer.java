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

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

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
     * Starts the timer to execute in a given number of seconds.
     * This will cancel all other times the timer has been started.
     *
     * @param seconds
     */
    public void startIn(long seconds) {
        synchronized (taskLock) {
            if (bukkitTaskId != -1) {
                plugin.getServer().getScheduler().cancelTask(bukkitTaskId);
            }
            GenericTimerRunnable runnable = new GenericTimerRunnable(firstTask);
            runnable.start(seconds);
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
                    long remainingTicks = secondsTillEnd * 20;
                    bukkitTaskId = plugin.getServer().getScheduler()
                            .runTaskLaterAsynchronously(plugin, this, remainingTicks).getTaskId();
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
         * @param secondsRemaining
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
                BukkitScheduler sc = plugin.getServer().getScheduler();
                if (runTaskAsync) {
                    sc.runTaskAsynchronously(plugin, runnable);
                } else {
                    sc.runTask(plugin, runnable);
                }
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

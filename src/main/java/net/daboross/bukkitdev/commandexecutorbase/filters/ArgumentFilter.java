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
package net.daboross.bukkitdev.commandexecutorbase.filters;

import net.daboross.bukkitdev.commandexecutorbase.CommandFilter;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ArgumentFilter implements CommandFilter {

    private final ArgumentCondition condition;
    private final int conditionValue;
    private final String deniedMessage;
    private final boolean showHelp;

    public ArgumentFilter(ArgumentCondition condition, int conditionValue, String deniedMessage, boolean showHelp) {
        this.condition = condition;
        this.conditionValue = conditionValue;
        this.deniedMessage = deniedMessage;
        this.showHelp = showHelp;
    }

    public ArgumentFilter(ArgumentCondition condition, int conditionValue, String deniedMessage) {
        this(condition, conditionValue, deniedMessage, true);
    }

    @Override
    public boolean canContinue(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        return condition.is(conditionValue, subCommandArgs.length);
    }

    @Override
    public String[] getDeniedMessage(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        return showHelp ? new String[]{deniedMessage, subCommand.getHelpMessage(baseCommandLabel)} : new String[]{deniedMessage};
    }

    public enum ArgumentCondition {

        GREATER_THAN {
            @Override
            public boolean is(int conditionValue, int valueToCheck) {
                return valueToCheck > conditionValue;
            }
        }, LESS_THAN {
            @Override
            public boolean is(int conditionValue, int valueToCheck) {
                return valueToCheck < conditionValue;
            }
        }, EQUALS {
            @Override
            public boolean is(int conditionValue, int valueToCheck) {
                return valueToCheck == conditionValue;
            }
        };

        public abstract boolean is(int conditionValue, int valueToCheck);
    }
}

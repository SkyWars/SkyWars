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
package net.daboross.bukkitdev.commandexecutorbase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * @author daboross
 */
public class CommandExecutorBase implements TabExecutor {

    private final Map<String, SubCommand> aliasToCommandMap = new HashMap<String, SubCommand>();
    private final List<SubCommand> subCommands = new ArrayList<SubCommand>();
    private final String commandPermission;

    public CommandExecutorBase(String commandPermission) {
        this.commandPermission = commandPermission;
    }

    public final void addSubCommand(SubCommand subCommand) {
        if (subCommand == null) {
            throw new IllegalArgumentException("Null SubCommand");
        }
        subCommands.add(subCommand);
        aliasToCommandMap.put(subCommand.getName(), subCommand);
        for (String alias : subCommand.getAliases()) {
            aliasToCommandMap.put(alias, subCommand);
        }
        subCommand.usingCommand(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SubCommand subCommand = getSubCommand(sender, cmd, label, args);
        if (subCommand != null) {
            String[] subCommandArgs = ArrayHelpers.getSubArray(args, 1, args.length - 1);
            if (checkFilters(sender, cmd, subCommand, label, args[0], subCommandArgs)) {
                subCommand.runCommand(sender, cmd, label, args[0], subCommandArgs);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            ArrayList<String> resultList = new ArrayList<String>();
            for (String alias : aliasToCommandMap.keySet()) {
                resultList.add(alias);
            }
            return resultList;
        } else if (args.length == 1) {
            ArrayList<String> resultList = new ArrayList<String>();
            for (String alias : aliasToCommandMap.keySet()) {
                if (alias.startsWith(args[0].toLowerCase())) {
                    if (hasHelpConditions(sender, aliasToCommandMap.get(alias))) {
                        resultList.add(alias);
                    }
                }
            }
            return resultList;
        } else {
            SubCommand subCommand = aliasToCommandMap.get(args[0].toLowerCase());
            if (subCommand == null) {
                return Collections.EMPTY_LIST;
            } else {
                return subCommand.tabComplete(sender, cmd, label, subCommand, args[0], ArrayHelpers.getSubArray(args, 1, args.length - 1));
            }
        }
    }

    private void sendInvalidSubCommandMessage(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ColorList.REG + "The subcommand '" + ColorList.SUBCMD + args[0] + ColorList.REG + "' does not exist for the command '" + ColorList.CMD + "/" + label + ColorList.REG + "'");
    }

    private void sendHelpMessage(CommandSender sender, String baseCommandLabel) {
        sender.sendMessage(String.format(ColorList.TOP_FORMAT, "Help"));
        for (SubCommand subCommandVar : subCommands) {
            if (hasHelpConditions(sender, subCommandVar)) {
                sender.sendMessage(getHelpMessage(subCommandVar, baseCommandLabel));
            }
        }
    }

    private void sendNoPermissionMessage(CommandSender sender, String label) {
        sender.sendMessage(ColorList.ERR + "You don't have permission to use " + ColorList.CMD + "/" + label);
    }

    SubCommand getSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!hasPermission(sender)) {
            sendNoPermissionMessage(sender, label);
            return null;
        }
        if (args.length < 1) {
            sendHelpMessage(sender, label);
            return null;
        }
        SubCommand command = aliasToCommandMap.get(args[0].toLowerCase());
        if (command == null) {
            sendInvalidSubCommandMessage(sender, label, args);
            return null;
        }
        return command;
    }

    boolean hasPermission(CommandSender sender) {
        return (commandPermission == null || sender.hasPermission(commandPermission) || !(sender instanceof Player));
    }

    void addAlias(SubCommand subCommand, String alias) {
        if (subCommands.contains(subCommand)) {
            aliasToCommandMap.put(alias, subCommand);
        } else {
            throw new IllegalArgumentException("SubCommand not added");
        }
    }

    void addAliases(SubCommand subCommand, String... aliases) {
        if (subCommands.contains(subCommand)) {
            for (String alias : aliases) {
                aliasToCommandMap.put(alias, subCommand);
            }
        } else {
            throw new IllegalArgumentException("SubCommand not added");
        }
    }

    static String getHelpMessage(SubCommand subCommand, String baseCommandLabel) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(ColorList.CMD).append("/").append(baseCommandLabel).append(ColorList.SUBCMD).append(" ");
        resultBuilder.append(ColorList.SUBCMD).append(subCommand.getName());
        for (String alias : subCommand.getAliases()) {
            resultBuilder.append(ColorList.DIVIDER).append("|").append(ColorList.SUBCMD).append(alias);
        }
        resultBuilder.append(" ");
        if (!subCommand.getArgumentNames().isEmpty()) {
            resultBuilder.append(ColorList.ARGS_SURROUNDER);
            for (String argument : subCommand.getArgumentNames()) {
                resultBuilder.append("<").append(ColorList.ARGS).append(argument).append(ColorList.ARGS_SURROUNDER).append("> ");
            }
        }
        resultBuilder.append(ColorList.HELP).append(subCommand.getHelpMessage());
        return resultBuilder.toString();
    }

    static String getHelpMessage(SubCommand subCommand, String baseCommandLabel, String subCommandLabel) {
        if (!(subCommand.getAliases().contains(subCommandLabel.toLowerCase()) || subCommandLabel.equalsIgnoreCase(subCommand.getName()))) {
            throw new IllegalArgumentException("Alias '" + subCommandLabel + "' doesn't belong to given SubCommand '" + subCommand.getName() + "'");
        }
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(ColorList.CMD).append("/").append(baseCommandLabel).append(ColorList.SUBCMD).append(" ");
        resultBuilder.append(ColorList.SUBCMD).append(subCommand.getName());
        for (String alias : subCommand.getAliases()) {
            resultBuilder.append(ColorList.DIVIDER).append("|").append(ColorList.SUBCMD).append(alias);
        }
        resultBuilder.append(" ");
        if (!subCommand.getArgumentNames().isEmpty()) {
            resultBuilder.append(ColorList.ARGS_SURROUNDER);
            for (String argument : subCommand.getArgumentNames()) {
                resultBuilder.append("<").append(ColorList.ARGS).append(argument).append(ColorList.ARGS_SURROUNDER).append("> ");
            }
        }
        resultBuilder.append(ColorList.HELP).append(subCommand.getHelpMessage());
        return resultBuilder.toString();
    }

    static boolean hasHelpConditions(CommandSender sender, SubCommand subCommand) {
        for (CommandPreCondition condition : subCommand.getHelpConditions()) {
            if (!condition.canContinue(sender, subCommand)) {
                return false;
            }
        }
        return true;
    }

    static boolean checkFilters(CommandSender sender, Command baseCommand, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        for (CommandFilter filter : subCommand.getCommandFilters()) {
            if (!filter.canContinue(sender, baseCommand, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs)) {
                sender.sendMessage(filter.getDeniedMessage(sender, baseCommand, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs));
                return false;
            }
        }
        return true;
    }
}

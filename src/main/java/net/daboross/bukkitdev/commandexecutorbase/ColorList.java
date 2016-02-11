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

import org.bukkit.ChatColor;

/**
 * This is a class that holds what colors CommandExecutorBase should use for different purposes.
 *
 * @author daboross
 */
public final class ColorList {

    /**
     * This is the main color.
     */
    public static final String REG = ChatColor.DARK_AQUA.toString();
    /**
     * This is the color used at the top of a list or info panel (usually use this panel in the part that is explaining
     * what the information is about).
     */
    public static final String TOP = ChatColor.DARK_GREEN.toString();
    /**
     * Separator at the top of a list. <br>In '-- Player Info for azdef --', '--' would be this color.
     */
    public static final String TOP_SEPERATOR = ChatColor.BLUE.toString();
    /**
     * This is the color for player usernames. <br>In 'john did this', 'john' would be this color.
     */
    public static final String NAME = ChatColor.DARK_GREEN.toString();
    /**
     * This is the color for general data information (times, dates, counts). <br>'50 minutes ago' would be this color.
     */
    public static final String DATA = NAME;
    /**
     * This is the color for commands (in help messages). <br>'/command' would be this color
     */
    public static final String CMD = ChatColor.GREEN.toString();
    /**
     * This is be the color for help text. <br>'this command does x' would be this color
     */
    public static final String HELP = ChatColor.WHITE.toString();
    /**
     * This is the color for subcommands. <br>in '/command sub',
     */
    public static final String SUBCMD = ChatColor.BLUE.toString();
    /**
     * This is the color for arguments of a command (in help messages). <br>in '/command sub [player] [page]', 'player'
     * and 'page' would be this color
     */
    public static final String ARGS = ChatColor.AQUA.toString();
    /**
     * This is the color for arguments of a command (in help messages). <br>in '/command sub [player] [page]', '[' and
     * ']' would be this color.
     */
    public static final String ARGS_SURROUNDER = ChatColor.DARK_AQUA.toString();
    /**
     * This is the color for messages saying that there is an error or the user can't use the command.
     */
    public static final String ERR = ChatColor.DARK_RED.toString();
    /**
     * This is the color for the arguments that have caused an error, or are illegal.
     */
    public static final String ERR_ARGS = ChatColor.RED.toString();
    /**
     * This is the color that the server's name should be displayed in.
     */
    public static final String SERVER = ChatColor.AQUA.toString();
    /**
     * This is the color for the divider slash in various places. <br>In 'player/nick', '/' would be this color.
     */
    public static final String DIVIDER = ChatColor.DARK_GRAY.toString();
    /**
     * This is the color that broadcasts should be.
     */
    public static final String BROADCAST = ChatColor.GREEN.toString();
    /**
     * Broadcast name for use with String.format(). Takes the broadcaster's name as an argument.
     */
    public static final String BROADCAST_NAME_FORMAT = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "%s" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    /**
     * Format for the top part of a long bit of information. First %s is the name of this part of stuff.
     */
    public static final String TOP_FORMAT = TOP_SEPERATOR + " -- " + TOP + "%s" + TOP_SEPERATOR + " --";
}

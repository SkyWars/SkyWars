/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class VersionCommand extends SubCommand {

    private static final String CREDITS = "Skyblock Warriors map created by SwipeShot";
    private static final String VERSION = ColorList.REG + "SkyWars plugin " + ColorList.DATA + "v%s" + ColorList.REG + " created by Dabo Ross";
    private final SkyWarsPlugin plugin;

    public VersionCommand(SkyWarsPlugin plugin) {
        super("status", true, "skywars.version", "Gives version");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length != 0) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        sender.sendMessage(CREDITS);
        sender.sendMessage(String.format(VERSION, plugin.getDescription().getVersion()));
    }
}

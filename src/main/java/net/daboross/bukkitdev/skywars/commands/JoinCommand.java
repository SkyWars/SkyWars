/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class JoinCommand extends SubCommand {

    private final String CONFIRMATION = ColorList.REG + "You have joined the queue.";
    private final String ALREADY_QUEUED = ColorList.REG + "You were already queued";
    private final SkyWarsPlugin plugin;

    public JoinCommand(SkyWarsPlugin plugin) {
        super("join", false, "skywars.join", "Joins the queue for the next game");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player player = (Player) sender;
        if (subCommandArgs.length != 0) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (plugin.getGameQueue().inQueue(player.getName())) {
            sender.sendMessage(ALREADY_QUEUED);
        } else {
            sender.sendMessage(CONFIRMATION);
            plugin.getGameQueue().queuePlayer(player.getName());
        }
    }
}

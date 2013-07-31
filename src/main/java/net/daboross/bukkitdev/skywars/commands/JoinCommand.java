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
    private final String ALREADY_QUEUED = ColorList.ERR + "You were already in the queue.";
    private final String IN_GAME = ColorList.REG + "You can't join now, you are already in a game.";
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
        String name = player.getName().toLowerCase();
        if (plugin.getCurrentGames().getGameID(name) != null) {
            sender.sendMessage(IN_GAME);
        } else if (plugin.getGameQueue().inQueue(name)) {
            sender.sendMessage(ALREADY_QUEUED);
        } else {
            sender.sendMessage(CONFIRMATION);
            plugin.getGameQueue().queuePlayer(name);
        }
    }
}

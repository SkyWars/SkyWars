/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.game.GameIdHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class StatusCommand extends SubCommand {

    private final SkyWarsPlugin plugin;

    public StatusCommand(SkyWarsPlugin plugin) {
        super("status", true, "skywars.status", "Gives game status");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length != 0) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        GameIdHandler idh = plugin.getIdHandler();
        sender.sendMessage(String.format(ColorList.TOP_FORMAT, "SkyWars Status"));
        sender.sendMessage(ColorList.REG + "In Queue: " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(plugin.getGameQueue().getQueueCopy(), ColorList.REG + ", " + ColorList.DATA));
        sender.sendMessage(String.format(ColorList.TOP_FORMAT, "Current Arenas"));
        for (Integer id : idh.getCurrentIds()) {
            sender.sendMessage(ColorList.DATA + id + ColorList.REG + ": " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(idh.getPlayers(id), ColorList.REG + ", " + ColorList.DATA));
        }
    }
}

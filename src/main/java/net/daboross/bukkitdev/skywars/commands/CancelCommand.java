/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

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
public class CancelCommand extends SubCommand {

    private final SkyWarsPlugin plugin;

    public CancelCommand(SkyWarsPlugin plugin) {
        super("cancel", true, "skywars.cancel", "Cancels a current game with the given id");
        addArgumentNames("ID");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length == 0) {
            sender.sendMessage(ColorList.ERR + "Not enough arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        } else if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        int id;
        try {
            id = Integer.parseInt(subCommandArgs[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " isn't an integer!");
            return;
        }
        GameIdHandler idh = plugin.getIdHandler();
        if (idh.getPlayers(id) == null) {
            sender.sendMessage(ColorList.ERR + "There aren't any games with the id " + ColorList.ERR_ARGS + id);
            return;
        }
        sender.sendMessage(ColorList.REG + "Canceling game " + ColorList.DATA + id);
        plugin.getGameHandler().endGame(id, true);
    }
}

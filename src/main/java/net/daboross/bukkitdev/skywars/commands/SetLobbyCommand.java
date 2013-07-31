/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.storage.SkyLocation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class SetLobbyCommand extends SubCommand {

    private static final String CONFIRMATION = ColorList.REG + "The lobby is now at your current location.";
    private final SkyWarsPlugin plugin;

    public SetLobbyCommand(SkyWarsPlugin plugin) {
        super("setlobby", false, "skywars.setlobby", "Sets the lobby position");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length != 0) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        Player player = (Player) sender;
        plugin.getLocationStore().setLobbyPosition(new SkyLocation(player));
        sender.sendMessage(CONFIRMATION);
    }
}

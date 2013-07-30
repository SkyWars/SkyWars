/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.subcommands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.storage.ArenaLocation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class SetPortal extends SubCommand {

    private static final String CONFIRMATION = ColorList.REG + "You have set a portal at your current location.";
    private final SkyWarsPlugin plugin;

    public SetPortal(SkyWarsPlugin plugin) {
        super("setportal", false, "skywars.setportal", "Sets a portal for automatically joining the game at your current location");
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
        plugin.getLocationStore().getPortals().add(new ArenaLocation(player.getLocation()));
        sender.sendMessage(CONFIRMATION);
    }
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class LobbyCommand extends SubCommand {

    private final SkyWarsPlugin plugin;

    public LobbyCommand(SkyWarsPlugin plugin) {
        super("lobby", false, "skywars.lobby", "Teleports you to the lobby");
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
        if (plugin.getCurrentGames().getGameID(player.getName()) != null) {
            sender.sendMessage(Messages.Lobby.IN_GAME);
        } else {
            player.teleport(plugin.getLocationStore().getLobbyPosition().toLocation());
            sender.sendMessage(Messages.Lobby.CONFIRMATION);
        }
    }
}

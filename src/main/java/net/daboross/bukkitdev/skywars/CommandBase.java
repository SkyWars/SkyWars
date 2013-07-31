/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 */
package net.daboross.bukkitdev.skywars;

import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.skywars.commands.CancelCommand;
import net.daboross.bukkitdev.skywars.commands.JoinCommand;
import net.daboross.bukkitdev.skywars.commands.LeaveCommand;
import net.daboross.bukkitdev.skywars.commands.SetLobbyCommand;
import net.daboross.bukkitdev.skywars.commands.SetPortalCommand;
import net.daboross.bukkitdev.skywars.commands.StatusCommand;
import net.daboross.bukkitdev.skywars.commands.VersionCommand;
import org.bukkit.command.CommandExecutor;

/**
 *
 * @author daboross
 */
public class CommandBase {

    private final SkyWarsPlugin plugin;
    private final CommandExecutorBase base;

    public CommandBase(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.base = new CommandExecutorBase(null);
        this.initCommands();
    }

    private void initCommands() {
        base.addSubCommand(new JoinCommand(plugin));
        base.addSubCommand(new LeaveCommand(plugin));
        base.addSubCommand(new SetLobbyCommand(plugin));
        base.addSubCommand(new SetPortalCommand(plugin));
        base.addSubCommand(new CancelCommand(plugin));
        base.addSubCommand(new StatusCommand(plugin));
        base.addSubCommand(new VersionCommand(plugin));
    }

    public CommandExecutor getExecutor() {
        return base;
    }
}

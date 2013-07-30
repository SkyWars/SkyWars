/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 */
package net.daboross.bukkitdev.skywars;

import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.skywars.subcommands.CancelCommand;
import net.daboross.bukkitdev.skywars.subcommands.JoinCommand;
import net.daboross.bukkitdev.skywars.subcommands.LeaveCommand;
import net.daboross.bukkitdev.skywars.subcommands.SetLobby;
import net.daboross.bukkitdev.skywars.subcommands.SetPortal;
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
        base.addSubCommand(new SetLobby(plugin));
        base.addSubCommand(new SetPortal(plugin));
        base.addSubCommand(new CancelCommand(plugin));
    }

    public CommandExecutor getExecutor() {
        return base;
    }
}

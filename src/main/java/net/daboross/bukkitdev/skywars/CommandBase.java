/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars;

import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;

/**
 *
 * @author daboross
 */
public class CommandBase {

    private final SkyWarsPlugin plugin;
    private final CommandExecutorBase base;

    public CommandBase(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        this.base=new CommandExecutorBase(null);
    }
}

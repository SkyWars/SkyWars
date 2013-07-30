/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 */
package net.daboross.bukkitdev.skywars;

import java.io.IOException;
import net.daboross.bukkitdev.skywars.storage.LocationStore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

/**
 *
 * @author daboross
 */
public class SkyWarsPlugin extends JavaPlugin {

    private LocationStore locationStore;

    @Override
    public void onEnable() {
        setupMetrics();
        locationStore = new LocationStore(this);
        setupPermissions();
        setupCommands();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("SkyWars doesn't know about the command /" + cmd.getName());
        return true;
    }

    private void setupMetrics() {
        MetricsLite metrics = null;
        try {
            metrics = new MetricsLite(this);
        } catch (IOException ex) {
            getLogger().warning("Unable to create Metrics");
        }
        if (metrics != null) {
            metrics.start();
        }
    }

    private void setupPermissions() {
        PluginManager pm = getServer().getPluginManager();
        Permission star = getPermission("skywars.*", pm);
        Permission join = getPermission("skywars.join", pm);
        Permission leave = getPermission("skywars.leave", pm);
        Permission setLobbySpawn = getPermission("skywars.setlobbyspawn", pm);
        Permission setPortal = getPermission("skywars.setportal", pm);
        Permission cancel = getPermission("skywars.cancel", pm);
        star.setDefault(PermissionDefault.FALSE);
        join.setDefault(PermissionDefault.TRUE);
        leave.setDefault(PermissionDefault.TRUE);
        setLobbySpawn.setDefault(PermissionDefault.OP);
        setPortal.setDefault(PermissionDefault.OP);
        cancel.setDefault(PermissionDefault.OP);
        join.addParent(star, true);
        leave.addParent(star, true);
        setLobbySpawn.addParent(star, true);
        setPortal.addParent(star, true);
        cancel.addParent(star, true);
    }

    private Permission getPermission(String name, PluginManager pm) {
        Permission permission = pm.getPermission(name);
        if (permission == null) {
            permission = new Permission(name);
        }
        return permission;
    }

    private void setupCommands() {
        PluginCommand main = getCommand("skywars");
        if (main != null) {
            main.setExecutor(new CommandBase(this).getExecutor());
        }
    }

    public LocationStore getLocationStore() {
        return locationStore;
    }
}

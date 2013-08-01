/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 */
package net.daboross.bukkitdev.skywars;

import java.io.IOException;
import java.util.List;
import net.daboross.bukkitdev.skywars.game.CurrentGames;
import net.daboross.bukkitdev.skywars.game.GameHandler;
import net.daboross.bukkitdev.skywars.game.GameIdHandler;
import net.daboross.bukkitdev.skywars.game.GameQueue;
import net.daboross.bukkitdev.skywars.listeners.CommandListener;
import net.daboross.bukkitdev.skywars.listeners.DeathListener;
import net.daboross.bukkitdev.skywars.listeners.PortalListener;
import net.daboross.bukkitdev.skywars.listeners.QuitListener;
import net.daboross.bukkitdev.skywars.listeners.SpawnListener;
import net.daboross.bukkitdev.skywars.storage.LocationStore;
import net.daboross.bukkitdev.skywars.world.SkyWorldHandler;
import net.daboross.bukkitdev.skywars.world.VoidGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.generator.ChunkGenerator;
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
    private GameQueue gameQueue;
    private CurrentGames currentGames;
    private GameHandler gameHandler;
    private GameIdHandler idHandler;
    private SkyWorldHandler worldCreator;

    @Override
    public void onEnable() {
        setupMetrics();
        locationStore = new LocationStore(this);
        gameQueue = new GameQueue(this);
        currentGames = new CurrentGames();
        gameHandler = new GameHandler(this);
        idHandler = new GameIdHandler();
        worldCreator = new SkyWorldHandler();
        setupPermissions();
        setupCommands();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SpawnListener(), this);
        pm.registerEvents(new DeathListener(this), this);
        pm.registerEvents(new QuitListener(this), this);
        pm.registerEvents(new PortalListener(this), this);
        pm.registerEvents(new CommandListener(this), this);
    }

    @Override
    public void onDisable() {
        locationStore.save();
        List<Integer> ids = idHandler.getCurrentIds();
        for (int id : ids) {
            if (idHandler.getPlayers(id) != null) {
                gameHandler.endGame(id, false);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("SkyWars doesn't know about the command /" + cmd.getName());
        return true;
    }

    private void setupMetrics() {
        MetricsLite metrics;
        try {
            metrics = new MetricsLite(this);
        } catch (IOException ex) {
            return;
        }
        metrics.start();
    }

    private void setupPermissions() {
        PluginManager pm = getServer().getPluginManager();
        Permission star = getPermission("skywars.*", pm);
        Permission join = getPermission("skywars.join", pm);
        Permission leave = getPermission("skywars.leave", pm);
        Permission setLobby = getPermission("skywars.setlobby", pm);
        Permission setPortal = getPermission("skywars.setportal", pm);
        Permission cancel = getPermission("skywars.cancel", pm);
        Permission status = getPermission("skywars.status", pm);
        Permission version = getPermission("skywars.version", pm);
        Permission lobby = getPermission("skywars.lobby", pm);
        star.setDefault(PermissionDefault.FALSE);
        join.setDefault(PermissionDefault.TRUE);
        leave.setDefault(PermissionDefault.TRUE);
        setLobby.setDefault(PermissionDefault.OP);
        setPortal.setDefault(PermissionDefault.OP);
        cancel.setDefault(PermissionDefault.OP);
        status.setDefault(PermissionDefault.TRUE);
        version.setDefault(PermissionDefault.TRUE);
        lobby.setDefault(PermissionDefault.TRUE);
        join.addParent(star, true);
        leave.addParent(star, true);
        setLobby.addParent(star, true);
        setPortal.addParent(star, true);
        cancel.addParent(star, true);
        status.addParent(star, true);
        version.addParent(star, true);
        lobby.addParent(star, true);
        updateAndAddAll(pm, star, join, leave, setLobby, setPortal, cancel, status, version, lobby);

    }

    private Permission getPermission(String name, PluginManager pm) {
        Permission permission = pm.getPermission(name);
        if (permission == null) {
            permission = new Permission(name);
        }
        return permission;
    }

    private void updateAndAddAll(PluginManager pm, Permission... permissions) {
        for (Permission permission : permissions) {
            updateAndAdd(pm, permission);
        }
    }

    private void updateAndAdd(PluginManager pm, Permission permission) {
        Permission oldPerm = pm.getPermission(permission.getName());
        if (oldPerm == null) {
            pm.addPermission(permission);
        }
        permission.recalculatePermissibles();
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

    public GameQueue getGameQueue() {
        return gameQueue;
    }

    public CurrentGames getCurrentGames() {
        return currentGames;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public GameIdHandler getIdHandler() {
        return idHandler;
    }

    public SkyWorldHandler getWorldHandler() {
        return worldCreator;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }
}

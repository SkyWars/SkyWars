/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author daboross
 */
public class PermissionHandler {

    private final String permissionBase;
    private final Permission basePermission;

    public PermissionHandler(String permissionBase) {
        this.permissionBase = permissionBase;
        basePermission = this.getPermission(Bukkit.getPluginManager(), permissionBase + ".*");
    }

    public void setupPermissions() {
        PluginManager pm = Bukkit.getPluginManager();
        addEveryonePermissions(pm, "join", "leave", "status", "version", "lobby");
        addOpOnlyPermissions(pm, "setlobby", "setportal", "cancel");
        updateBasePermission(pm);
    }

    private void addOpOnlyPermissions(PluginManager pm, String... permissions) {
        for (int i = 0; i < permissions.length; i++) {
            String name = permissionBase + permissions[i];
            Permission permission = getPermission(pm, permissionBase + permissions[i]);
            permission.setDefault(PermissionDefault.OP);
            basePermission.getChildren().put(name, Boolean.TRUE);
            updateAndAdd(pm, permission);
        }
    }

    private void addEveryonePermissions(PluginManager pm, String... permissions) {
        for (int i = 0; i < permissions.length; i++) {
            String name = permissionBase + permissions[i];
            Permission permission = getPermission(pm, permissionBase + permissions[i]);
            permission.setDefault(PermissionDefault.TRUE);
            basePermission.getChildren().put(name, Boolean.TRUE);
            updateAndAdd(pm, permission);
        }
    }

    private void updateBasePermission(PluginManager pm) {
        updateAndAdd(pm, basePermission);
    }

    private Permission getPermission(PluginManager pm, String name) {
        Permission permission = pm.getPermission(name);
        if (permission == null) {
            permission = new Permission(name);
        }
        return permission;
    }

    private void updateAndAdd(PluginManager pm, Permission permission) {
        Permission oldPerm = pm.getPermission(permission.getName());
        if (oldPerm == null) {
            pm.addPermission(permission);
        }
        permission.recalculatePermissibles();
    }
}

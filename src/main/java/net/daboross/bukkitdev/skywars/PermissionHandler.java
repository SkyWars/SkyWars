/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

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
        addOpOnlyPermissions(pm, "setlobby", "setportal", "cancel", "cancelall", "cfgdebug", "setup");
        updateBasePermission(pm);
    }

    private void addOpOnlyPermissions(PluginManager pm, String... permissions) {
        for (String perm : permissions) {
            String name = permissionBase + perm;
            Permission permission = getPermission(pm, permissionBase + perm);
            permission.setDefault(PermissionDefault.OP);
            basePermission.getChildren().put(name, Boolean.TRUE);
            updateAndAdd(pm, permission);
        }
    }

    private void addEveryonePermissions(PluginManager pm, String... permissions) {
        for (String perm : permissions) {
            String name = permissionBase + "." + perm;
            Permission permission = getPermission(pm, name);
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

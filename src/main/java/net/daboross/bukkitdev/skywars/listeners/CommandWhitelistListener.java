/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.listeners;

import java.util.logging.Level;
import java.util.regex.Pattern;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandWhitelistListener implements Listener {

    private final SkyWarsPlugin plugin;

    public CommandWhitelistListener(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent evt) {
        SkyConfiguration config = plugin.getConfiguration();
        if (config.isCommandWhitelistEnabled()) {
            if (plugin.getCurrentGameTracker().isInGame(evt.getPlayer().getName())) {
                Pattern pattern = config.getCommandWhitelistCommandRegex();
                if (pattern != null) {
                    if (config.isCommandWhitelistABlacklist() == pattern.matcher(evt.getMessage()).find()) {
                        plugin.getLogger().log(Level.INFO, "[CommandWhitelist] Blocked command ''{0}'' sent by {1}", new Object[]{evt.getMessage(), evt.getPlayer().getName()});
                        evt.setCancelled(true);
                    }
                }
            }
        }
    }
}

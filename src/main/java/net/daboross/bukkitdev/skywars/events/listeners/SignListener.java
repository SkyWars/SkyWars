/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.events.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * TODO: This class should probably be split into three separate ones.
 * <p>
 * Keeping track of signs should be in SignManager in "skywars.game", sign updating events should be in
 * "SignUpdateListener" in "skywars.events.listeners" and sign creation events should be in "SignCreateListener" in
 * "skywars.listeners".
 */
public class SignListener implements Listener {

    private final SkyWars plugin;
    private final List<SkyBlockLocation> toRemove = new ArrayList<>();
    private final String[] lines = new String[4];
    private final boolean[] dynamic = new boolean[4];

    public SignListener(final SkyWars plugin) {
        this.plugin = plugin;
        String[] lineStrings = plugin.getConfiguration().getJoinSignLines();
        for (int i = 0; i < 4; i++) {
            lines[i] = ChatColor.translateAlternateColorCodes('&', lineStrings[i]);
            dynamic[i] = lineStrings[i].contains("{max}") || lineStrings[i].contains("{count}") || lineStrings[i].contains("{name}");
        }
    }

    @EventHandler()
    public void onSignPlace(SignChangeEvent evt) {
        boolean matches = false;
        for (int i = 0; i < 4; i++) {
            if (!dynamic[i]) {
                if (ChatColor.stripColor(lines[i]).equalsIgnoreCase(evt.getLine(i))) {
                    matches = true;
                }
                // We only care about whether or not the first non-dynamic line matches.
                break;
            }
        }
        if (matches) {
            if (!evt.getPlayer().hasPermission("skywars.setsign")) {
                // Only tell them if they actually are placing a sign
                // (we want to change the text for the event regardless
                // if they do have permission though, in case it is un-cancelled).
                if (!evt.isCancelled()) {
                    evt.getPlayer().sendMessage(SkyTrans.get(TransKey.NO_PERMISSION_CANNOT_PLACE_JOIN_SIGN));
                }
                return;
            }
            List<SkyBlockLocation> signs = plugin.getLocationStore().getSigns();
            SkyBlockLocation location = new SkyBlockLocation(evt.getBlock());
            if (!signs.contains(location)) {
                // Since we only remove broken signs when updating, we need to check to ensure
                // that we don't add a sign twice. If added twice, it would purely slow down updating
                // signs, and the duplicate would never be removed.
                signs.add(location);
            }
            SkyArena nextArena = plugin.getGameQueue().getPlannedArena();
            int current = plugin.getGameQueue().getNumPlayersInQueue();
            for (int i = 0; i < 4; i++) {
                if (dynamic[i]) {
                    evt.setLine(i, lines[i]
                            .replace("{max}", Integer.toString(nextArena.getNumPlayers()))
                            .replace("{count}", Integer.toString(current))
                            .replace("{name}", nextArena.getArenaName()));
                } else {
                    evt.setLine(i, lines[i]);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent evt) {
        if (evt.getClickedBlock() != null
                && (evt.getClickedBlock().getType() == Material.WALL_SIGN
                || evt.getClickedBlock().getType() == Material.SIGN_POST)) {
            BlockState state = evt.getClickedBlock().getState();
            if (state instanceof Sign
                    && plugin.getLocationStore().getSigns().contains(new SkyBlockLocation(evt.getClickedBlock()))
                    && testSign((Sign) state)) {
                Player p = evt.getPlayer();
                UUID uuid = p.getUniqueId();

                if (!p.hasPermission("skywars.join")) {
                    p.sendMessage(SkyTrans.get(TransKey.NO_PERMISSION_CANNOT_USE_JOIN_SIGN));
                    return;
                }


                if (plugin.getGameQueue().inQueue(uuid)) {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_QUEUED));
                    // Kit GUI is automatically shown when joining, but it should also be shown if already queued.
                    plugin.getKitGui().autoOpenGuiIfApplicable(p);
                } else if (plugin.getGameQueue().inSecondaryQueue(uuid)) {
                    p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_ALREADY_IN_SECONDARY_QUEUE));
                    p.sendMessage(SkyTrans.get(TransKey.SECONDARY_QUEUE_EXPLANATION));
                } else if (!plugin.getCurrentGameTracker().isInGame(uuid)) {
                    // Should be pretty impossible to click a join sign while already in a game.
                    p.sendMessage(SkyTrans.get(TransKey.CMD_JOIN_CONFIRMATION));
                    plugin.getGameQueue().queuePlayer(p);
                }
            }
        }
    }

    public void onQueueJoin(PlayerJoinQueueInfo info) {
        updateSigns();
    }

    public void onQueueLeave(PlayerLeaveQueueInfo info) {
        updateSigns();
    }

    public void onGameStart(GameStartInfo info) {
        updateSigns();
    }

    public void updateSigns() {
        List<SkyBlockLocation> signs = plugin.getLocationStore().getSigns();
        if (signs.isEmpty()) {
            return;
        }

        SkyArena nextArena = plugin.getGameQueue().getPlannedArena();
        int current = plugin.getGameQueue().getNumPlayersInQueue();

        for (SkyBlockLocation location : signs) {
            Block block = location.toBlock();
            if (block != null) {
                BlockState state = block.getState();
                if (!(state instanceof Sign)) {
                    toRemove.add(location);
                    continue;
                }
                Sign sign = (Sign) state;
                // Check if sign matches
                // TODO: currently, if the player updates sign configuration, all signs must also be updated.
                // This is done purely so as to avoid needing to catch a PlayerBreakBlockEvent in order to remove signs,
                // but it might want to be done differently if possible? Maybe it should check the first non-dynamic line
                // like when placing a sign.
                if (!testSign(sign)) {
                    toRemove.add(location);
                    continue;
                }
                for (int i = 0; i < 4; i++) {
                    if (dynamic[i]) {
                        sign.setLine(i, lines[i]
                                .replace("{max}", Integer.toString(nextArena.getNumPlayers()))
                                .replace("{count}", Integer.toString(current))
                                .replace("{name}", nextArena.getArenaName()));
                    } else {
                        sign.setLine(i, lines[i]);
                    }
                }
                // Don't update physics.
                state.update(false, false);
            }
        }

        for (SkyBlockLocation location : toRemove) {
            // This collection is not a copy, but rather the original storage for signs.
            signs.remove(location);
        }
    }

    private boolean testSign(Sign sign) {
        for (int i = 0; i < 4; i++) {
            if (!dynamic[i]) {
                if (!sign.getLine(i).equals(lines[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}

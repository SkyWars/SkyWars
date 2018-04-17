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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyLocationStore;
import net.daboross.bukkitdev.skywars.api.location.SkySignData;
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
    private final String[] lines = new String[4];
    private final boolean[] dynamic = new boolean[4];

    public SignListener(final SkyWars plugin) {
        this.plugin = plugin;
        String[] lineStrings = plugin.getConfiguration().getJoinSignLines();
        for (int i = 0; i < 4; i++) {
            lines[i] = ChatColor.translateAlternateColorCodes('&', lineStrings[i]);
            // TODO: handle edge case where conflicting colors are added, and then sanitized by the game. (or at least just make sure to check if this is the case when checking if a sign is ours)
            // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/util/CraftChatMessage.java?until=e13d1196863d5dcfea3d17b79238427dfb2c61b2#93
            dynamic[i] = lineStrings[i].contains("{max}") || lineStrings[i].contains("{count}") || lineStrings[i].contains("{name}") || lineStrings[i].contains("{queue}");
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

            // TODO: some way to make signs of custom arenas
            String queueName;
            if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
                // TODO: this is super inefficient.
                List<String> queueNames = new ArrayList<>(plugin.getConfiguration().getQueueNames());
                queueName = queueNames.get(ThreadLocalRandom.current().nextInt(queueNames.size()));
            } else {
                queueName = null;
            }
            SkySignData newData = new SkySignData(new SkyBlockLocation(evt.getBlock()), new ArrayList<>(Arrays.asList(evt.getLine(0), evt.getLine(1), evt.getLine(2), evt.getLine(3))), queueName);

            plugin.getLocationStore().registerSign(newData);

            SkyArena nextArena = plugin.getGameQueue().getPlannedArena(queueName);
            String arenaName = nextArena.getArenaName();
            SkyStatic.debug("[SignListener.onSignPlace] Found arena name '%s'", arenaName);
            int current = plugin.getGameQueue().getNumPlayersInQueue(queueName);
            for (int i = 0; i < 4; i++) {
                if (dynamic[i]) {
                    String replacedLine = lines[i]
                            .replace("{max}", Integer.toString(nextArena.getNumPlayers()))
                            .replace("{count}", Integer.toString(current))
                            .replace("{name}", arenaName);
                    if (queueName != null) {
                        replacedLine = replacedLine.replace("{queue}", queueName);
                    }
                    evt.setLine(i, replacedLine);
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
            SkySignData dataHere = plugin.getLocationStore().getSignAt(new SkyBlockLocation(evt.getClickedBlock()));
            if (state instanceof Sign
                    && dataHere != null
                    && testSign(dataHere, (Sign) state)) {
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
                    plugin.getGameQueue().queuePlayer(p, dataHere.queueName);
                }
            }
        }
    }

    public void onQueueJoin(PlayerJoinQueueInfo info) {
        updateSigns(info.getQueueName());
    }

    public void onQueueLeave(PlayerLeaveQueueInfo info) {
        updateSigns(info.getQueueName());
    }

    public void onGameStart(GameStartInfo info) {
        updateSigns(info.getQueueName());
    }

    public void updateSigns(String queueName) {
        SkyLocationStore store = plugin.getLocationStore();
        List<SkySignData> signs = store.getQueueSigns(queueName);
        if (signs == null || signs.isEmpty()) {
            return;
        }

        SkyArena nextArena = plugin.getGameQueue().getPlannedArena(queueName);
        int current = plugin.getGameQueue().getNumPlayersInQueue(queueName);

        String arenaName = nextArena.getArenaName();
        SkyStatic.debug("[SignListener.updateSigns] Found arena name '%s'", arenaName);
        List<SkySignData> toRemove = new ArrayList<>();
        for (SkySignData signData : signs) {
            SkyBlockLocation location = signData.location;
            Block block = location.toBlock();
            if (block != null) {
                SkyStatic.debug("Updating sign at %s.", location);
                BlockState state = block.getState();
                if (!(state instanceof Sign)) {
                    toRemove.add(signData);
                    continue;
                }
                Sign sign = (Sign) state;
                // Check if sign matches
                // TODO: currently, if the player updates sign configuration, all signs must also be updated.
                // This is done purely so as to avoid needing to catch a PlayerBreakBlockEvent in order to remove signs,
                // but it might want to be done differently if possible? Maybe it should check the first non-dynamic line
                // like when placing a sign.
                if (!testSign(signData, sign)) {
                    SkyStatic.debug("Sign at %s does not match, removing.", location);
                    toRemove.add(signData);
                    continue;
                }
                for (int i = 0; i < 4; i++) {
                    if (dynamic[i]) {
                        sign.setLine(i, lines[i]
                                .replace("{max}", Integer.toString(nextArena.getNumPlayers()))
                                .replace("{count}", Integer.toString(current))
                                .replace("{name}", arenaName));
                    } else {
                        sign.setLine(i, lines[i]);
                    }
                }
                // Don't update physics.
                state.update(false, false);
            }
        }

        for (SkySignData data : toRemove) {
            store.removeSign(data);
        }
    }

    private boolean testSign(SkySignData expected, Sign sign) {
        for (int i = 0; i < 4; i++) {
            if (!dynamic[i]) {
                if (!sign.getLine(i).equals(lines[i]) && !sign.getLine(i).equals(expected.lastLines.get(i))) {
                    SkyStatic.debug("Sign does not match! Non-dynamic line %s has %s on record, %s on sign.", i, lines[i], sign.getLine(i));
                    return false;
                }
            }
        }
        return true;
    }
}

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
package net.daboross.bukkitdev.skywars.events;

import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.events.ArenaPlayerDeathEvent;
import net.daboross.bukkitdev.skywars.api.events.ArenaPlayerKillPlayerEvent;
import net.daboross.bukkitdev.skywars.api.events.GameEndEvent;
import net.daboross.bukkitdev.skywars.api.events.GameStartEvent;
import net.daboross.bukkitdev.skywars.api.events.LeaveGameEvent;
import net.daboross.bukkitdev.skywars.api.events.PlayerEnterQueueEvent;
import net.daboross.bukkitdev.skywars.api.events.PlayerLeaveQueueEvent;
import net.daboross.bukkitdev.skywars.api.events.RespawnAfterLeaveGameEvent;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerDeathInArenaInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerJoinSecondaryQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerKillPlayerInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveSecondaryQueueInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import org.apache.commons.lang.Validate;

public class GameEventDistributor {

    private final SkyWarsPlugin plugin;
    private final String errorFormat;

    public GameEventDistributor(SkyWarsPlugin plugin) {
        this.plugin = plugin;
        String version = SkyStatic.getImplementationVersion();
        if (version.equals("git-unknown")) {
            version = SkyStatic.getVersion();
        } else {
            version += " - " + SkyStatic.getVersion();
        }
        this.errorFormat = "Couldn't broadcast %s in " + SkyStatic.getPluginName() + " version " + version + ":";
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(GameStartInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getIDHandler().onGameStart(info);
            plugin.getCurrentGameTracker().onGameStart(info);
            plugin.getBroadcaster().broadcastStart(info);
            plugin.getTeamScoreBoardListener().onGameStart(info);
            plugin.getAttackerStorage().onGameStart(info);
            plugin.getSignListener().onGameStart(info);
            plugin.getGameQueueTimer().onGameStart(info);
            // -- Before gameStart1 --
            plugin.getWorldHandler().onGameStart0(info);
            // -- After gameStart0 --
            plugin.getInventorySaveListener().onGameStart(info);
            // -- After InventorySaveListener --
            plugin.getWorldHandler().onGameStart1(info); // teleports players
            // -- After WorldHandler, InventorySaveListener --
            plugin.getResetHealth().onGameStart(info);
            plugin.getBackupInvClearListener().onGameStart(info); // total backup, can be useful if an inventory saving plugin for some reason saves inventory for old game.
            // -- After InventorySaveListener --
            plugin.getPlayers().onGameStart(info);
            plugin.getKitApplyListener().onGameStart(info);
            // -- After All --
            plugin.getServer().getPluginManager().callEvent(new GameStartEvent(plugin, info.getGame(), info.getPlayers()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "GameStart"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(GameEndInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Initial --
            plugin.getIDHandler().onGameEnd(info);
            // -- Normal --
            plugin.getBroadcaster().broadcastEnd(info);
            plugin.getTeamScoreBoardListener().onGameEnd(info);
            if (plugin.getScore() != null) {
                plugin.getScore().onGameEnd(info);
            }
            if (plugin.getEcoRewards() != null) {
                plugin.getEcoRewards().onGameEnd(info);
            }
            // -- High --
            plugin.getWorldHandler().onGameEnd(info);
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new GameEndEvent(plugin, info.getGame(), info.getAlivePlayers()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "GameEnd"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerLeaveGameInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getPlayers().onLeaveGame(info);
            plugin.getCurrentGameTracker().onPlayerLeaveGame(info);
            plugin.getTeamScoreBoardListener().onPlayerLeaveGame(info);
            plugin.getInventorySaveListener().onPlayerLeaveGame(info); // just clears the inventory.
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new LeaveGameEvent(plugin, info.getId(), info.getPlayer(), info.getReason()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerLeaveGame"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerRespawnAfterGameEndInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getResetHealth().onPlayerRespawn(info);
            plugin.getInventorySaveListener().onPlayerRespawn(info);
            plugin.getPlayers().onRespawn(info);
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new RespawnAfterLeaveGameEvent(plugin, info.getPlayer()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerRespawnAfterGameEnd"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerKillPlayerInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            if (plugin.getScore() != null) {
                plugin.getScore().onKill(info);
            }
            if (plugin.getEcoRewards() != null) {
                plugin.getEcoRewards().onPlayerKillPlayer(info);
            }
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new ArenaPlayerKillPlayerEvent(plugin, info.getGameId(), info.getKillerName(), info.getKilled()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerKillPlayer"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerDeathInArenaInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            if (plugin.getScore() != null) {
                plugin.getScore().onDeath(info);
            }
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new ArenaPlayerDeathEvent(plugin, info.getGameId(), info.getKilled()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerDeathInArena"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerJoinQueueInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getPlayers().onJoinQueue(info);
            plugin.getGameQueueTimer().onJoinQueue(info);
            plugin.getKitQueueNotifier().onQueueJoin(info);
            plugin.getSignListener().onQueueJoin(info); // update sign counts
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new PlayerEnterQueueEvent(plugin, info.getPlayer()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerJoinQueue"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerLeaveQueueInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getPlayers().onLeaveQueue(info);
            plugin.getGameQueueTimer().onLeaveQueue(info);
            plugin.getSignListener().onQueueLeave(info);
            // -- After --
            plugin.getServer().getPluginManager().callEvent(new PlayerLeaveQueueEvent(plugin, info.getPlayer()));
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerLeaveQueue"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerJoinSecondaryQueueInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getPlayers().onJoinSecondaryQueue(info);
            // -- After --
            // TODO: Do we want an event for this?
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerJoinSecondaryQueue"), t);
        }
    }

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void distribute(PlayerLeaveSecondaryQueueInfo info) {
        Validate.notNull(info, "Info cannot be null");
        try {
            // -- Normal --
            plugin.getPlayers().onLeaveSecondaryQueue(info);
            // -- After --
            // TODO: Do we want an event for this?
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, String.format(errorFormat, "PlayerLeaveSecondaryQueue"), t);
        }
    }
}

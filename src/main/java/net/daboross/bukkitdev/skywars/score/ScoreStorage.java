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
package net.daboross.bukkitdev.skywars.score;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.daboross.bukkitdev.bukkitsavetimer.SaveTimer;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.StartupFailedException;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.players.OfflineSkyPlayer;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.storage.Callback;
import net.daboross.bukkitdev.skywars.api.storage.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.storage.SkyInternalPlayer;
import net.daboross.bukkitdev.skywars.api.storage.SkyStorage;
import net.daboross.bukkitdev.skywars.api.storage.SkyStorageBackend;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerDeathInArenaInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerKillPlayerInfo;
import org.bukkit.entity.Player;

public class ScoreStorage extends SkyStorage {

    private final SkyWarsPlugin plugin;
    private final SkyStorageBackend backend;
    private final SaveTimer saveTimer;
    private final SaveTimer updateIndividualRanksTimer;

    @SuppressWarnings("UseSpecificCatch")
    public ScoreStorage(SkyWarsPlugin plugin) throws StartupFailedException {
        this.plugin = plugin;
        Class<? extends SkyStorageBackend> backendClass = getBackend();
        if (backendClass == null) {
            if (plugin.getConfiguration().isScoreUseSql()) {
                backendClass = SQLScoreStorage.class;
                plugin.getLogger().log(Level.INFO, "[Score] Using SQL backend");
            } else {
                backendClass = JSONScoreStorage.class;
                plugin.getLogger().log(Level.INFO, "[Score] Using JSON backend");
            }
        } else {
            plugin.getLogger().log(Level.INFO, "[Score] Using custom backend: '" + backendClass.getName() + "'");
        }
        try {
            Constructor<? extends SkyStorageBackend> constructor = backendClass.getConstructor(SkyWars.class);
            this.backend = constructor.newInstance(plugin);
        } catch (Throwable ex) {
            throw new StartupFailedException("[Score] Failed to initialize storage backend", ex);
        }
        long saveInterval = plugin.getConfiguration().getScoreSaveInterval();
        if (saveInterval > 0) {
            saveTimer = new SaveTimer(plugin, new SaveAndUpdateLeaderboardRunnable(), TimeUnit.SECONDS, saveInterval, true);
        } else {
            saveTimer = null;
        }
        long updateIndividualRanksInterval = plugin.getConfiguration().getScoreIndividualRankUpdateInterval();
        if (backendClass != JSONScoreStorage.class && updateIndividualRanksInterval > 0) {
            updateIndividualRanksTimer = new SaveTimer(plugin, new UpdateOnlinePlayerRanksRunnable(), TimeUnit.SECONDS, updateIndividualRanksInterval, true);
        } else {
            updateIndividualRanksTimer = null;
        }
        updateLeaderboard();
    }

    public void onKill(PlayerKillPlayerInfo info) {
        SkyConfiguration config = plugin.getConfiguration();
        addScore(info.getKillerUuid(), config.getKillScoreDiff());
    }

    public void onDeath(PlayerDeathInArenaInfo info) {
        SkyConfiguration config = plugin.getConfiguration();
        addScore(info.getKilled().getUniqueId(), config.getDeathScoreDiff());
    }

    public void onGameEnd(GameEndInfo info) {
        SkyConfiguration config = plugin.getConfiguration();
        List<Player> alive = info.getAlivePlayers();
        if (!alive.isEmpty() && alive.size() <= info.getGame().getArena().getTeamSize()) {
            for (Player p : alive) {
                addScore(p.getUniqueId(), config.getWinScoreDiff());
            }
        }
    }

    @Override
    public void addScore(UUID uuid, int diff) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(uuid);
        if (skyPlayer != null) {
            skyPlayer.addScore(diff);
        } else {
            // Player is offline, use backend method directly.
            backend.addScore(uuid, diff);
        }
        dataChanged();
    }

    @Override
    public void getScore(final UUID uuid, final ScoreCallback callback) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(uuid);
        if (skyPlayer != null) {
            callback.scoreGetCallback(skyPlayer.getScore());
        } else {
            backend.getScore(uuid, callback);
        }
    }

    @Override
    public void setScore(final UUID uuid, final int score) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(uuid);
        if (skyPlayer != null) {
            skyPlayer.setScore(score);
        } else {
            // Player is offline, use backend method directly.
            backend.setScore(uuid, score);
        }
        dataChanged();
    }

    @Override
    public void getRank(final UUID uuid, final ScoreCallback callback) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(uuid);
        if (skyPlayer != null) {
            callback.scoreGetCallback(skyPlayer.getRank());
        } else {
            backend.getRank(uuid, callback);
        }
    }

    @Override
    public SkyInternalPlayer loadPlayer(final Player player) {
        return backend.loadPlayer(player);
    }

    @Override
    public void getOfflinePlayer(final UUID uuid, final Callback<OfflineSkyPlayer> callback) {
        SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(uuid);
        if (skyPlayer != null) {
            callback.call(skyPlayer);
        } else {
            backend.getOfflinePlayer(uuid, callback);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void getOfflinePlayer(final String name, final Callback<OfflineSkyPlayer> callback) {
        SkyPlayer skyPlayer = null;
        Player bukkitPlayer = plugin.getServer().getPlayerExact(name);
        if (bukkitPlayer != null) {
            skyPlayer = plugin.getPlayers().getPlayer(bukkitPlayer);
        }
        if (skyPlayer != null) {
            callback.call(skyPlayer);
        } else {
            backend.getOfflinePlayer(name, callback);
        }
    }

    @Override
    public void dataChanged() {
        if (saveTimer != null) {
            saveTimer.dataChanged();
        }
        if (updateIndividualRanksTimer != null) {
            updateIndividualRanksTimer.dataChanged();
        }
    }

    @Override
    public List<? extends OfflineSkyPlayer> getTopPlayers(final int count) {
        return backend.getTopPlayers(count);
    }

    public synchronized void save() throws IOException {
        backend.save();
    }

    public synchronized void updateLeaderboard() {
        backend.updateLeaderboard();
    }

    public synchronized void updateIndividualRanks() {
        backend.updateOnlineIndividualRanks();
    }

    private class SaveAndUpdateLeaderboardRunnable implements Runnable {

        @Override
        public void run() {
            SkyStatic.debug("Saving score");
            try {
                save();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save score storage backend", ex);
            }
            SkyStatic.debug("Done saving score");
            SkyStatic.debug("Updating score leaderboard");
            updateLeaderboard();
            SkyStatic.debug("Done updating score leaderboard");
        }
    }

    private class UpdateOnlinePlayerRanksRunnable implements Runnable {

        @Override
        public void run() {
            SkyStatic.debug("Saving score");
            try {
                save();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save score storage backend", ex);
            }
            SkyStatic.debug("Done saving score");
            SkyStatic.debug("Updating online individual ranks");
            updateIndividualRanks();
            SkyStatic.debug("Done updating online individual ranks");
        }
    }
}

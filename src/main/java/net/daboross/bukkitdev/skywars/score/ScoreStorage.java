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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.daboross.bukkitdev.bukkitsavetimer.SaveTimer;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.StartupFailedException;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.score.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.score.ScoreStorageBackend;
import net.daboross.bukkitdev.skywars.api.score.SkyScore;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerDeathInArenaInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerKillPlayerInfo;
import org.bukkit.entity.Player;

public class ScoreStorage extends SkyScore {

    private final SkyWarsPlugin plugin;
    private final ScoreStorageBackend backend;
    private final SaveTimer timer;

    @SuppressWarnings("UseSpecificCatch")
    public ScoreStorage(SkyWarsPlugin plugin) throws StartupFailedException {
        this.plugin = plugin;
        Class<? extends ScoreStorageBackend> backendClass = getBackend();
        if (backendClass == null) {
            if (plugin.getConfiguration().isScoreUseSql()) {
                backendClass = SQLScoreStorage.class;
                plugin.getLogger().log(Level.INFO, "[Score] Using SQL backend - Warning: SQL Score backend is incomplete, and may completely fail"); // TODO: Remove warning (when done with class)
            } else {
                backendClass = JSONScoreStorage.class;
                plugin.getLogger().log(Level.INFO, "[Score] Using JSON backend");
            }
        } else {
            plugin.getLogger().log(Level.INFO, "[Score] Using custom backend: '" + backendClass.getName() + "'");
        }
        try {
            Constructor<? extends ScoreStorageBackend> constructor = backendClass.getConstructor(SkyWars.class);
            this.backend = constructor.newInstance(plugin);
        } catch (Throwable ex) {
            throw new StartupFailedException("[Score] Failed to initialize storage backend", ex);
        }
        long saveInterval = plugin.getConfiguration().getScoreSaveInterval();
        if (saveInterval > 0) {
            timer = new SaveTimer(plugin, new SaveRunnable(), TimeUnit.SECONDS, plugin.getConfiguration().getScoreSaveInterval(), true);
        } else {
            timer = null;
        }
    }

    public void onKill(PlayerKillPlayerInfo info) {
        SkyConfiguration config = plugin.getConfiguration();
        addScore(info.getKillerName(), config.getKillScoreDiff());
    }

    public void onDeath(PlayerDeathInArenaInfo info) {
        SkyConfiguration config = plugin.getConfiguration();
        addScore(info.getKilled().getName(), config.getDeathScoreDiff());
    }

    public void onGameEnd(GameEndInfo info) {
        SkyConfiguration config = plugin.getConfiguration();
        List<Player> alive = info.getAlivePlayers();
        if (!alive.isEmpty() && alive.size() <= info.getGame().getArena().getTeamSize()) {
            for (Player p : alive) {
                addScore(p.getName(), config.getWinScoreDiff());
            }
        }
    }

    @Override
    public synchronized void addScore(String name, int diff) {
        SkyStatic.debug("Adding %s score to %s", diff, name);
        if (timer != null) {
            timer.dataChanged();
        }
        backend.addScore(name, diff);
    }

    @Override
    public synchronized void getScore(String name, ScoreCallback callback) {
        backend.getScore(name, callback);
    }

    @Override
    public int getCachedOnlineScore(final String name) {
        return backend.getCachedOnlineScore(name);
    }

    @Override
    public void loadCachedScore(final String name) {
        backend.loadCachedScore(name);
    }

    public synchronized void save() throws IOException {
        backend.save();
    }

    private class SaveRunnable implements Runnable {

        @Override
        public void run() {
            SkyStatic.debug("AutoSaving score");
            try {
                save();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save score storage backend", ex);
            }
            SkyStatic.debug("Done AutoSaving score");
        }
    }
}

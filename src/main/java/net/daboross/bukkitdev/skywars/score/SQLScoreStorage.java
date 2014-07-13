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
package net.daboross.bukkitdev.skywars.score;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.daboross.bukkitdev.asyncsql.AsyncSQL;
import net.daboross.bukkitdev.asyncsql.ResultHolder;
import net.daboross.bukkitdev.asyncsql.ResultRunnable;
import net.daboross.bukkitdev.asyncsql.ResultSQLRunnable;
import net.daboross.bukkitdev.asyncsql.SQLConnectionInfo;
import net.daboross.bukkitdev.asyncsql.SQLRunnable;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.storage.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.storage.SkyStorageBackend;
import net.daboross.bukkitdev.skywars.player.AbstractSkyPlayer;
import org.bukkit.entity.Player;

public class SQLScoreStorage extends SkyStorageBackend {

    private final Map<UUID, Integer> scoreCache = new HashMap<>();
    private final HashSet<UUID> unsavedValues = new HashSet<>();
    private final AsyncSQL sql;
    private final String tableName = "skywars_user";

    public SQLScoreStorage(final SkyWars skywars) throws SQLException {
        super(skywars);
        SkyConfiguration config = skywars.getConfiguration();
        SQLConnectionInfo connectionInfo = new SQLConnectionInfo(config.getScoreSqlHost(), config.getScoreSqlPort(),
                config.getScoreSqlDatabase(), config.getScoreSqlUsername(), config.getScoreSqlPassword());
        sql = new AsyncSQL(skywars, skywars.getLogger(), connectionInfo);
        createTable();
    }

    private void createTable() {
        sql.run("create user table", new SQLRunnable() {
            @Override
            public void run(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`uuid` VARCHAR(36), `username` VARCHAR(32), `user_score` INT, PRIMARY KEY (`uuid`));"
                )) {
                    statement.execute();
                }
            }
        });
    }

    @Override
    public void addScore(final UUID uuid, final int diff) {
        cacheAdd(uuid, diff, true);
        sql.run("add " + diff + " score to " + uuid, new SQLRunnable() {
            @Override
            public void run(final Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO `" + tableName + "` (uuid, user_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE `user_score` = `user_score` + ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    statement.setInt(2, diff);
                    statement.setInt(3, diff);
                    statement.execute();
                }
            }
        });
    }

    @Override
    public void setScore(final UUID uuid, final int score) {
        cacheSet(uuid, score, true);
        sql.run("set " + uuid + "'s score to " + score, new SQLRunnable() {
            @Override
            public void run(final Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO `" + tableName + "` (uuid, user_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE `user_score` = ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    statement.setInt(2, score);
                    statement.setInt(3, score);
                    statement.execute();
                }
            }
        });
    }

    @Override
    public void getScore(final UUID uuid, final ScoreCallback callback) {
        sql.run("get score for " + uuid, new ResultSQLRunnable<Integer>() {
            @Override
            public void run(final Connection connection, final ResultHolder<Integer> result) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT `user_score` FROM `" + tableName + "` WHERE `uuid` = ?")) {
                    statement.setString(1, uuid.toString());
                    try (ResultSet set = statement.executeQuery()) {
                        if (!set.first()) {
                            result.set(0); // I'm assuming that scores should be 0 by default here
                            return;
                        }
                        result.set(set.getInt("user_score"));
                    }
                }
            }
        }, new ResultRunnable<Integer>() {
            @Override
            public void runWithResult(final Integer value) {
                callback.scoreGetCallback(value);
            }
        });
    }

    public void initUsername(final UUID uuid, final String username) {
        sql.run("set " + uuid + "'s username to" + username, new SQLRunnable() {
            @Override
            public void run(final Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        // I'm assuming that the default value should be 0 here
                        "INSERT INTO `" + tableName + "` (uuid, username, user_score) VALUES (?, ?, 0) ON DUPLICATE KEY UPDATE `username` = ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, username);
                    statement.setString(3, username);
                    statement.execute();
                }
            }
        });
    }

    @Override
    public void save() throws IOException {
        for (UUID unsavedUuid : unsavedValues) {
            setScore(unsavedUuid, cacheGet(unsavedUuid));
        }
    }

    @Override
    public SQLSkyPlayer loadPlayer(final Player player) {
        final UUID uuid = player.getUniqueId();
        getScore(uuid, new ScoreCallback() {
            @Override
            public void scoreGetCallback(final int score) {
                cacheSet(uuid, score, true); // true because this score is saved.
            }
        });
        initUsername(uuid, player.getName()); // be sure to set their name
        return new SQLSkyPlayer(player);
    }

    private Integer cacheGet(final UUID uuid) {
        synchronized (scoreCache) {
            return scoreCache.get(uuid);
        }
    }

    private void cacheSet(final UUID uuid, final int value, boolean saving) {
        synchronized (scoreCache) {
            scoreCache.put(uuid, value);
            if (saving) {
                // this is just a shortcut, so the remove is inside the synchronized block
                unsavedValues.remove(uuid);
            } else {
                unsavedValues.add(uuid);
            }
        }
    }

    private void cacheAdd(final UUID uuid, final int value, boolean saving) {
        synchronized (scoreCache) {
            if (scoreCache.containsKey(uuid)) {
                scoreCache.put(uuid, scoreCache.get(uuid) + value);
            }
            if (saving) {
                // this is just a shortcut, so the remove is inside the synchronized block
                unsavedValues.remove(uuid);
            } else {
                unsavedValues.add(uuid);
            }
        }
    }

    private class SQLSkyPlayer extends AbstractSkyPlayer {

        public SQLSkyPlayer(final Player player) {
            super(player);
        }

        @Override
        public void loggedOut() {
            SQLScoreStorage.this.setScore(uuid, cacheGet(uuid));
        }

        @Override
        public int getScore() {
            return cacheGet(uuid);
        }

        @Override
        public void setScore(final int score) {
            cacheSet(uuid, score, false);
        }

        @Override
        public void addScore(final int diff) {
            cacheAdd(uuid, diff, false);
        }
    }
}

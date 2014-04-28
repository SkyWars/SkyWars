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
import java.sql.SQLException;
import java.util.HashMap;
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
import net.daboross.bukkitdev.skywars.api.score.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.score.ScoreStorageBackend;
import org.bukkit.entity.Player;

// TODO: Implement
public class SQLScoreStorage extends ScoreStorageBackend {

    private final Map<UUID, Integer> scoreCache = new HashMap<>();
    private final AsyncSQL sql;
    private final String tableName = "skywars_user";

    protected SQLScoreStorage(final SkyWars skywars) throws SQLException {
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
                        "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`uuid` VARCHAR(32), `username` VARCHAR(32), `user_score` INT, PRIMARY KEY (`uuid`));"
                )) {
                    statement.execute();
                }
            }
        });
    }

    @Override
    public void addScore(final UUID uuid, final int diff) {
        sql.run("add " + diff + " score to " + uuid, new SQLRunnable() {
            @Override
            public void run(final Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO `" + tableName + "` (username, user_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE `user_score` = `user_score` + ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    statement.setInt(2, diff);
                    statement.setInt(3, diff);
                    statement.executeQuery().getString("parent");
                }
            }
        });
    }

    @Override
    public void setScore(final UUID uuid, final int score) {
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
        callback.scoreGetCallback(0);
        sql.run("get score for " + uuid, new ResultSQLRunnable<Integer>() {
            @Override
            public void run(final Connection connection, final ResultHolder<Integer> result) throws SQLException {

            }
        }, new ResultRunnable<Integer>() {
            @Override
            public void runWithResult(final Integer value) {
                callback.scoreGetCallback(value);
            }
        });
    }

    @Override
    public void save() throws IOException {
    }

    @Override
    public int getCachedOnlineScore(final UUID uuid) {
        Integer score = cacheGet(uuid);
        return score == null ? 0 : score;
    }

    @Override
    public void loadCachedScore(final Player player) {
        final UUID uuid = player.getUniqueId();
        getScore(uuid, new ScoreCallback() {
            @Override
            public void scoreGetCallback(final int score) {
                cacheSet(uuid, score);
            }
        });
    }

    private Integer cacheGet(final UUID uuid) {
        synchronized (scoreCache) {
            return scoreCache.get(uuid);
        }
    }

    private void cacheSet(final UUID uuid, final int value) {
        synchronized (scoreCache) {
            scoreCache.put(uuid, value);
        }
    }
}

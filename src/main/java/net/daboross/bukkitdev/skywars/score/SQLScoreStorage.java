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
import java.util.Locale;
import java.util.Map;
import net.daboross.bukkitdev.asyncsql.AsyncSQL;
import net.daboross.bukkitdev.asyncsql.SQLConnectionInfo;
import net.daboross.bukkitdev.asyncsql.SQLRunnable;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.score.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.score.ScoreStorageBackend;

// TODO: Implement
public class SQLScoreStorage extends ScoreStorageBackend {

    private final Map<String, Integer> scoreCache = new HashMap<>();
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
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "` (`username` VARCHAR(32), `user_score` INT, PRIMARY KEY (`username`));");
                try {
                    statement.execute();
                } finally {
                    statement.close();
                }
            }
        });
    }

    @Override
    public void addScore(final String playerName, final int diff) {
        final String name = playerName.toLowerCase(Locale.ENGLISH);
        sql.run("add " + diff + " score to " + name, new SQLRunnable() {
            @Override
            public void run(final Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + tableName + "` (username, user_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE `user_score` = `user_score` + ?;");
                statement.setString(1, name);
                statement.setInt(2, diff);
                statement.setInt(3, diff);
            }
        });
    }

    @Override
    public void setScore(final String playerName, final int score) {
    }

    @Override
    public void getScore(final String playerName, final ScoreCallback callback) {
        callback.scoreGetCallback(0);
    }

    @Override
    public void save() throws IOException {
    }

    @Override
    public int getCachedOnlineScore(final String playerName) {
        Integer score = cacheGet(playerName);
        return score == null ? 0 : score;
    }

    @Override
    public void loadCachedScore(final String playerName) {
        getScore(playerName, new ScoreCallback() {
            @Override
            public void scoreGetCallback(final int score) {
                cacheSet(playerName, score);
            }
        });
    }

    private Integer cacheGet(final String name) {
        synchronized (scoreCache) {
            return scoreCache.get(name.toLowerCase(Locale.ENGLISH));
        }
    }

    private void cacheSet(final String name, final int value) {
        synchronized (scoreCache) {
            scoreCache.put(name.toLowerCase(Locale.ENGLISH), value);
        }
    }
}

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

import com.google.common.base.Strings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.daboross.bukkitdev.asyncsql.AsyncSQL;
import net.daboross.bukkitdev.asyncsql.ResultHolder;
import net.daboross.bukkitdev.asyncsql.ResultRunnable;
import net.daboross.bukkitdev.asyncsql.ResultSQLRunnable;
import net.daboross.bukkitdev.asyncsql.SQLConnectionInfo;
import net.daboross.bukkitdev.asyncsql.SQLRunnable;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.players.OfflineSkyPlayer;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.storage.Callback;
import net.daboross.bukkitdev.skywars.api.storage.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.storage.SkyStorageBackend;
import net.daboross.bukkitdev.skywars.player.AbstractSkyPlayer;
import org.bukkit.entity.Player;

public class SQLScoreStorage extends SkyStorageBackend {

    private final Map<UUID, Integer> scoreCache = new HashMap<>();
    private final ArrayList<CachedOfflineSqlPlayer> topPlayers = new ArrayList<>();
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
                        "INSERT INTO `" + tableName + "` (uuid, user_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_score = user_score + ?;"
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
                        "INSERT INTO `" + tableName + "` (uuid, user_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_score = ?;"
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
                        "SELECT user_score FROM `" + tableName + "` WHERE uuid = ?;")) {
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
                callback.scoreGetCallback(value == null ? 0 : value);
            }
        });
    }

    @Override
    public void getRank(final UUID uuid, final ScoreCallback callback) {
        sql.run("get rank for" + uuid, new ResultSQLRunnable<Integer>() {
            @Override
            public void run(final Connection connection, final ResultHolder<Integer> result) throws SQLException {
                // Order by UUID if equal scores
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT ranked_user.position" +
                                " FROM (SELECT uuid, @rownum := @rownum + 1 AS position" +
                                "     FROM `" + tableName + "`" +
                                "     JOIN (SELECT @rownum := 0) r" +
                                "   ORDER BY user_score DESC, uuid) ranked_user" +
                                " WHERE ranked_user.uuid = ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    try (ResultSet set = statement.executeQuery()) {
                        if (!set.first()) {
                            result.set(-1);
                            return;
                        }
                        result.set(set.getInt("position"));
                    }
                }
            }
        }, new ResultRunnable<Integer>() {
            @Override
            public void runWithResult(final Integer value) {
                callback.scoreGetCallback(value == null ? -1 : value);
            }
        });
    }

    @Override
    public void getOfflinePlayer(final UUID uuid, final Callback<OfflineSkyPlayer> callback) {
        sql.run("get offline player with uuid " + uuid, new ResultSQLRunnable<OfflineSkyPlayer>() {
            @Override
            public void run(final Connection connection, final ResultHolder<OfflineSkyPlayer> result) throws SQLException {
                // Order by UUID if equal scores
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT ranked_user.uuid, ranked_user.username, ranked_user.user_score, ranked_user.position" +
                                " FROM (SELECT uuid, username, user_score, @rownum := @rownum + 1 AS position" +
                                "     FROM `" + tableName + "`" +
                                "     JOIN (SELECT @rownum := 0) r" +
                                "   ORDER BY user_score DESC, uuid) ranked_user" +
                                " WHERE ranked_user.uuid = ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    try (ResultSet set = statement.executeQuery()) {
                        if (!set.first()) {
                            result.set(null);
                            return;
                        }
                        result.set(new CachedOfflineSqlPlayer(
                                uuid,
                                set.getString("username"),
                                set.getInt("user_score"),
                                set.getInt("position")));
                    }
                }
            }
        }, new ResultRunnable<OfflineSkyPlayer>() {
            @Override
            public void runWithResult(final OfflineSkyPlayer result) {
                callback.call(result);
            }
        });
    }

    @Override
    public void getOfflinePlayer(final String name, final Callback<OfflineSkyPlayer> callback) {
        sql.run("get offline player with name " + name, new ResultSQLRunnable<OfflineSkyPlayer>() {
            @Override
            public void run(final Connection connection, final ResultHolder<OfflineSkyPlayer> result) throws SQLException {
                // Order by UUID if equal scores
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT ranked_user.uuid, ranked_user.username, ranked_user.user_score, ranked_user.position" +
                                " FROM (SELECT uuid, username, user_score, @rownum := @rownum + 1 AS position" +
                                "     FROM `" + tableName + "`" +
                                "     JOIN (SELECT @rownum := 0) r" +
                                "   ORDER BY user_score DESC, uuid) ranked_user" +
                                " WHERE UPPER(ranked_user.username) = UPPER(?);"
                )) {
                    statement.setString(1, name);
                    try (ResultSet set = statement.executeQuery()) {
                        if (!set.first()) {
                            result.set(null);
                            return;
                        }
                        result.set(new CachedOfflineSqlPlayer(
                                UUID.fromString(set.getString("uuid")),
                                set.getString("username"),
                                set.getInt("user_score"),
                                set.getInt("position")));
                    }
                }
            }
        }, new ResultRunnable<OfflineSkyPlayer>() {
            @Override
            public void runWithResult(final OfflineSkyPlayer result) {
                callback.call(result);
            }
        });
    }

    @Override
    public List<? extends OfflineSkyPlayer> getTopPlayers(final int count) {
        return topPlayers;
    }

    public void initPlayerInDatabase(final UUID uuid, final String username, final Callback<?> callback) {
        sql.run("set " + uuid + "'s username to" + username, new ResultSQLRunnable<Object>() {
            @Override
            public void run(final Connection connection, final ResultHolder result) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        // I'm assuming that the default value should be 0 here
                        "INSERT INTO `" + tableName + "` (uuid, username, user_score) VALUES (?, ?, 0) ON DUPLICATE KEY UPDATE username = ?;"
                )) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, username);
                    statement.setString(3, username);
                    statement.execute();
                }
                // No need to set the result here, as we don't use it
            }
        }, new ResultRunnable<Object>() {
            @Override
            public void runWithResult(final Object ignored) {
                if (callback != null) {
                    callback.call(null);
                }
            }
        });
    }

    @Override
    public void save() {
        ArrayList<UUID> unsavedValuesCopy;
        synchronized (scoreCache) {
            unsavedValuesCopy = new ArrayList<>(unsavedValues);
            unsavedValues.clear();
        }
        for (UUID unsavedUuid : unsavedValuesCopy) {
            Integer score = cacheGet(unsavedUuid);
            if (score != null) {
                setScore(unsavedUuid, score);
            }
        }
    }

    @Override
    public void updateLeaderboard() {
        sql.run("select top 10 leaderboard scores", new ResultSQLRunnable<ArrayList<CachedOfflineSqlPlayer>>() {
            @Override
            public void run(final Connection connection, final ResultHolder<ArrayList<CachedOfflineSqlPlayer>> result) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT uuid, username, user_score FROM `" + tableName + "` ORDER BY user_score DESC, uuid LIMIT 10;"
                )) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        int rankNumber = 1;
                        ArrayList<CachedOfflineSqlPlayer> newTopPlayers = new ArrayList<>(10);
                        while (resultSet.next()) {
                            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                            String username = resultSet.getString("username");
                            int score = resultSet.getInt("user_score");
                            int rank = rankNumber++;
                            newTopPlayers.add(new CachedOfflineSqlPlayer(uuid, username, score, rank));
                        }
                        result.set(newTopPlayers);
                    }
                }
            }
        }, new ResultRunnable<ArrayList<CachedOfflineSqlPlayer>>() {
            @Override
            public void runWithResult(final ArrayList<CachedOfflineSqlPlayer> players) {
                if (players == null) {
                    SkyStatic.log(Level.WARNING, "Top 10 leaderboard not updated!");
                    return;
                }
                topPlayers.clear();
                topPlayers.addAll(players);
                if (topPlayers.isEmpty()) {
                    SkyStatic.debug("Warning: leaderboard is empty, no scores found.");
                }
            }
        });
    }

    @Override
    public void updateOnlineIndividualRanks() {
        Collection<? extends Player> onlinePlayers = skywars.getServer().getOnlinePlayers();
        final List<String> uuidList = new ArrayList<>(onlinePlayers.size());
        for (Player player : onlinePlayers) {
            uuidList.add(player.getUniqueId().toString());
        }
        if (uuidList.isEmpty()) {
            return;
        }
        final String[] uuidArray = uuidList.toArray(new String[uuidList.size()]);
        sql.run("update ranks for all online players", new ResultSQLRunnable<Map<UUID, Integer>>() {
            @Override
            public void run(final Connection connection, final ResultHolder<Map<UUID, Integer>> result) throws SQLException {
                // Order by UUID if equal scores
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT ranked_user.uuid, ranked_user.position" +
                                " FROM (SELECT uuid, @rownum := @rownum + 1 AS position" +
                                "     FROM `" + tableName + "`" +
                                "     JOIN (SELECT @rownum := 0) r" +
                                "   ORDER BY user_score DESC, uuid) ranked_user" +
                                " WHERE ranked_user.uuid IN (" + Strings.repeat("?, ", uuidList.size() - 1) + "?);"
                )) {
                    // This is avoiding using statement.setArray(connection.createArrayOf()), which is not supported by jdbc.
                    int parameterNum = 1;
                    for (String uuid : uuidList) {
                        statement.setString(parameterNum++, uuid);
                    }
                    try (ResultSet set = statement.executeQuery()) {
                        Map<UUID, Integer> resultMap = new HashMap<>(uuidArray.length);

                        while (set.next()) {
                            resultMap.put(UUID.fromString(set.getString("uuid")), set.getInt("position"));
                        }

                        result.set(resultMap);
                    }
                }
            }
        }, new ResultRunnable<Map<UUID, Integer>>() {
            @Override
            public void runWithResult(final Map<UUID, Integer> map) {
                if (map == null) {
                    SkyStatic.log(Level.SEVERE, "Online player ranks not updated!");
                    return;
                }
                SkyStatic.debug("Updating all cached online player ranks");
                for (Map.Entry<UUID, Integer> rankEntry : map.entrySet()) {
                    SkyPlayer player = skywars.getPlayers().getPlayer(rankEntry.getKey());
                    if (player instanceof SQLSkyPlayer) { // this also checks for null (offline)
                        ((SQLSkyPlayer) player).setCachedRank(rankEntry.getValue());
                    }
                }
            }
        });
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
        // be sure to set their name
        initPlayerInDatabase(uuid, player.getName(), new Callback<Object>() {
            @Override
            public void call(final Object ignored) {
                // We need to ensure they are in the database before we try and get their rank
                // (hence having this inside the result callable for initPlayerInDatabase()).
                getRank(uuid, new ScoreCallback() {
                    @Override
                    public void scoreGetCallback(final int rank) {
                        SkyPlayer player = skywars.getPlayers().getPlayer(uuid);
                        if (player instanceof SQLSkyPlayer) { // this also checks for null (offline)
                            ((SQLSkyPlayer) player).setCachedRank(rank);
                        } else {
                            SkyStatic.debug("Was going to initially set rank for player, but player is no longer online!");
                        }
                    }
                });
            }
        });
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

        private int cachedRank = -1;

        public SQLSkyPlayer(final Player player) {
            super(player);
        }

        @Override
        public void loggedOut() {
            Integer score = cacheGet(uuid);
            if (score != null) {
                SQLScoreStorage.this.setScore(uuid, score);
                SQLScoreStorage.this.scoreCache.remove(uuid);
            }
        }

        @Override
        public int getScore() {
            Integer score = cacheGet(uuid);
            return score == null ? -1 : score;
        }

        @Override
        public void setScore(final int score) {
            cacheSet(uuid, score, false);
        }

        @Override
        public void addScore(final int diff) {
            cacheAdd(uuid, diff, false);
        }

        @Override
        public int getRank() {
            return cachedRank;
        }

        public void setCachedRank(final Integer cachedRank) {
            this.cachedRank = cachedRank;
        }
    }

    public class CachedOfflineSqlPlayer implements OfflineSkyPlayer {

        private final UUID uuid;
        private final String name;
        private final int score;
        private final int rank;

        public CachedOfflineSqlPlayer(final UUID uuid, final String name, final int score, final int rank) {
            this.uuid = uuid;
            this.name = name == null ? "<Unknown>" : name;
            this.score = score;
            this.rank = rank;
        }

        @Override
        public UUID getUuid() {
            return uuid;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public int getRank() {
            return rank;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}

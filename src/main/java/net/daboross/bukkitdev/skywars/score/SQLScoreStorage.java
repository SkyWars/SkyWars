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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.daboross.bukkitdev.asyncsql.AsyncSQL;
import net.daboross.bukkitdev.asyncsql.SQLConnectionInfo;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import net.daboross.bukkitdev.skywars.api.points.PointStorageBackend;

// TODO: Implement
public class SQLScoreStorage extends PointStorageBackend {

    private final Map<String, Integer> scoreCache = new HashMap<>();
    private final AsyncSQL sql;

    protected SQLScoreStorage(final SkyWars skywars) throws SQLException {
        super(skywars);
        SkyConfiguration config = skywars.getConfiguration();
        SQLConnectionInfo connectionInfo = new SQLConnectionInfo(config.getScoreSqlHost(), config.getScoreSqlPort(),
                config.getScoreSqlDatabase(), config.getScoreSqlUsername(), config.getScoreSqlPassword());
        sql = new AsyncSQL(skywars, skywars.getLogger(), connectionInfo);
    }

    @Override
    public void addScore(final String player, final int diff) {
    }

    @Override
    public void setScore(final String player, final int score) {
    }

    @Override
    public int getScore(final String player) {
        return 0;
    }

    @Override
    public void save() throws IOException {
    }
}

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
package net.daboross.bukkitdev.skywars.points;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.points.PointStorageBackend;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PointStorageJSONBackend implements PointStorageBackend {

    private final File saveFile;
    private final JSONObject scores;

    public PointStorageJSONBackend( SkyWarsPlugin plugin ) throws IOException {
        this.saveFile = new File( plugin.getDataFolder(), "score.json" );
        this.scores = load();
    }

    private JSONObject load() throws IOException {
        if ( !saveFile.exists() ) {
            if ( saveFile.createNewFile() ) {
                return new JSONObject();
            } else {
                throw new IOException( "Couldn't create file " + saveFile.getAbsolutePath() );
            }
        }
        try ( FileInputStream fis = new FileInputStream( saveFile ) ) {
            return new JSONObject( new JSONTokener( fis ) );
        } catch ( JSONException ex ) {
            throw new IOException( "JSONException loading " + saveFile.getAbsolutePath(), ex );
        }
    }

    @Override
    public void save() throws IOException {
        try ( FileWriter writer = new FileWriter( saveFile ) ) {
            scores.write( writer );
        } catch ( IOException | JSONException ex ) {
            throw new IOException( "Couldn't write to " + saveFile.getAbsolutePath(), ex );
        }
    }

    @Override
    public void addScore( String player, int diff ) {
        player = player.toLowerCase( Locale.ENGLISH );
        try {
            scores.put( player, scores.getInt( player ) + diff );
        } catch ( JSONException unused ) {
            scores.put( player, diff );
        }
    }

    @Override
    public void setScore( String player, int score ) {
        player = player.toLowerCase( Locale.ENGLISH );
        scores.put( player, score );
    }

    @Override
    public int getScore( String player ) {
        player = player.toLowerCase( Locale.ENGLISH );
        try {
            return scores.getInt( player );
        } catch ( JSONException unused ) {
            return 0;
        }
    }
}

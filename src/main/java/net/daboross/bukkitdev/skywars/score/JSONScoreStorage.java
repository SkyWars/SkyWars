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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.points.PointStorageBackend;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONScoreStorage extends PointStorageBackend {

    private final File saveFileBuffer;
    private final File saveFile;
    private final JSONObject scores;

    public JSONScoreStorage(SkyWars plugin) throws IOException, FileNotFoundException {
        super(plugin);
        this.saveFile = new File(plugin.getDataFolder(), "score.json");
        this.saveFileBuffer = new File(plugin.getDataFolder(), "score.json~");
        this.scores = load();
    }

    private JSONObject load() throws IOException, FileNotFoundException {
        if (!saveFile.exists()) {
            if (saveFile.createNewFile()) {
                return new JSONObject();
            } else {
                throw new IOException("Couldn't create file " + saveFile.getAbsolutePath());
            }
        }
        if (!saveFile.isFile()) {
            throw new IOException("File '" + saveFile.getAbsolutePath() + "' is not a file (perhaps a directory?).");
        }

        try (FileInputStream fis = new FileInputStream(saveFile)) {
            return new JSONObject(new JSONTokener(fis));
        } catch (JSONException ex) {
            try (FileInputStream fis = new FileInputStream(saveFile)) {
                byte[] buffer = new byte[10];
                int read = fis.read(buffer);
                String str = new String(buffer, 0, read, Charset.forName("UTF-8"));
                if (StringUtils.isBlank(str)) {
                    skywars.getLogger().log(Level.WARNING, "Score json file is empty, perhaps it was corrupted? Ignoring it and starting new score database. If you haven't recorded any scores, this won't matter.");
                    return new JSONObject();
                }
            }
            throw new IOException("JSONException loading " + saveFile.getAbsolutePath(), ex);
        }
    }

    @Override
    public void save() throws IOException {
        if (!saveFileBuffer.exists()) {
            if (!saveFileBuffer.createNewFile()) {
                throw new IOException("Failed to create file '" + saveFileBuffer + "'.");
            }
        }
        try (FileOutputStream fos = new FileOutputStream(saveFileBuffer)) {
            try (OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"))) {
                scores.write(writer);
            }
        } catch (IOException | JSONException ex) {
            throw new IOException("Couldn't write to " + saveFileBuffer.getAbsolutePath(), ex);
        }
        try {
            Files.move(saveFileBuffer.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Failed to move buffer file '" + saveFileBuffer.getAbsolutePath() + "' to actual save location '" + saveFile + "'", ex);
        }
    }

    @Override
    public void addScore(String player, int diff) {
        player = player.toLowerCase(Locale.ENGLISH);
        try {
            scores.put(player, scores.getInt(player) + diff);
        } catch (JSONException unused) {
            scores.put(player, diff);
        }
    }

    @Override
    public void setScore(String player, int score) {
        player = player.toLowerCase(Locale.ENGLISH);
        scores.put(player, score);
    }

    @Override
    public int getScore(String player) {
        player = player.toLowerCase(Locale.ENGLISH);
        try {
            return scores.getInt(player);
        } catch (JSONException unused) {
            return 0;
        }
    }
}

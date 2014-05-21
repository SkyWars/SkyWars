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
import java.util.UUID;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.storage.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.storage.SkyInternalPlayer;
import net.daboross.bukkitdev.skywars.api.storage.SkyStorageBackend;
import net.daboross.bukkitdev.skywars.player.AbstractSkyPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONScoreStorage extends SkyStorageBackend {

    private final File saveFileBuffer;
    private final File saveFile;
    private final File oldSaveFile;
    private final JSONObject baseJson;
    private JSONObject nameToScore;
    private JSONObject uuidToStoredPlayer;

    public JSONScoreStorage(SkyWars plugin) throws IOException, FileNotFoundException {
        super(plugin);
        this.oldSaveFile = new File(plugin.getDataFolder(), "score.json");
        this.saveFile = new File(plugin.getDataFolder(), "score-v1.json");
        this.saveFileBuffer = new File(plugin.getDataFolder(), "score-v1.json~");
        this.baseJson = load();
        this.nameToScore = this.baseJson.getJSONObject("legacy-name-score");
        this.uuidToStoredPlayer = this.baseJson.getJSONObject("uuid-players-v1");
    }

    private JSONObject load() throws IOException, FileNotFoundException {
        if (!saveFile.exists()) {
            JSONObject newStorage = new JSONObject();
            newStorage.put("uuid-players-v1", new JSONObject());
            if (oldSaveFile.exists()) {
                skywars.getLogger().log(Level.INFO, "Found old score storage file, attempting to import data");
                JSONObject oldStorage = loadFile(oldSaveFile);
                newStorage.put("legacy-name-score", oldStorage); // Be lazy and just copy the whole JSONObject.
            } else {
                newStorage.put("legacy-name-score", new JSONObject());
            }
            return newStorage;
        } else {
            return loadFile(saveFile);
        }
    }

    private JSONObject loadFile(File file) throws IOException {
        if (!file.isFile()) {
            throw new IOException("File '" + file.getAbsolutePath() + "' is not a file (perhaps a directory?).");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return new JSONObject(new JSONTokener(fis));
        } catch (JSONException ex) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[10];
                int read = fis.read(buffer);
                String str = new String(buffer, 0, read, Charset.forName("UTF-8"));
                if (StringUtils.isBlank(str)) {
                    skywars.getLogger().log(Level.WARNING, "File {} is empty, perhaps it was corrupted? Ignoring it and starting new score database. If you haven't recorded any baseJson, this won't matter.", file.getAbsolutePath());
                    return new JSONObject();
                }
            }
            throw new IOException("JSONException loading " + file.getAbsolutePath(), ex);
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
                baseJson.write(writer);
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
    public SkyInternalPlayer loadPlayer(final Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        JSONObject playerObj = uuidToStoredPlayer.optJSONObject(uuid);
        if (playerObj == null) {
            playerObj = new JSONObject();
            uuidToStoredPlayer.put(uuid, playerObj);
            playerObj.put("username", name);
            if (nameToScore.has(name.toLowerCase())) {
                SkyStatic.debug("Migrated score for %s to UUID (uuid: %s)", name, uuid);
                playerObj.put("score", nameToScore.get(name.toLowerCase()));
                nameToScore.remove(name.toLowerCase());
            } else {
                playerObj.put("score", 0);
            }
        } else {
            if (!playerObj.has("username")) {
                playerObj.put("username", name);
            } else if (!playerObj.get("username").equals(name)) {
                SkyStatic.log("Username of (uuid: %s) changed from %s to %s", uuid, playerObj.get("username"), name);
                playerObj.put("username", name);
            }
            if (!playerObj.has("score")) {
                playerObj.put("score", 0);
            }
        }
        return new JSONSkyPlayer(player, playerObj);
    }

    @Override
    public void addScore(final UUID uuid, final int diff) {
        JSONObject playerObj = uuidToStoredPlayer.optJSONObject(uuid.toString());
        if (playerObj == null) {
            playerObj = new JSONObject();
            playerObj.put("score", diff); // assume the default score is 0
        } else {
            playerObj.put("score", playerObj.getInt("score") + diff);
        }
    }

    @Override
    public void setScore(final UUID uuid, final int score) {
        JSONObject playerObj = uuidToStoredPlayer.optJSONObject(uuid.toString());
        if (playerObj == null) {
            playerObj = new JSONObject();
        }
        playerObj.put("score", score);
    }

    private int getScore(final UUID uuid) {
        JSONObject playerObj = uuidToStoredPlayer.optJSONObject(uuid.toString());
        return playerObj == null ? 0 : playerObj.getInt("score");
    }

    @Override
    public void getScore(final UUID uuid, final ScoreCallback callback) {
        callback.scoreGetCallback(getScore(uuid));
    }

    public class JSONSkyPlayer extends AbstractSkyPlayer {

        private final JSONObject playerObj;

        public JSONSkyPlayer(final Player player, final JSONObject obj) {
            super(player);
            playerObj = obj;
        }

        @Override
        public void loggedOut() {
        }

        @Override
        public int getScore() {
            return playerObj.getInt("score");
        }

        @Override
        public void setScore(final int score) {
            playerObj.put("score", score);
        }

        @Override
        public void addScore(final int diff) {
            playerObj.put("score", getScore() + diff);
        }
    }
}

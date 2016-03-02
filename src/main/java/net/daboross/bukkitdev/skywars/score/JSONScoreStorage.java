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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.storage.ScoreCallback;
import net.daboross.bukkitdev.skywars.api.storage.SkyInternalPlayer;
import net.daboross.bukkitdev.skywars.api.storage.SkyStorageBackend;
import net.daboross.bukkitdev.skywars.player.AbstractSkyPlayer;
import net.daboross.jsonserialization.JsonException;
import net.daboross.jsonserialization.JsonParser;
import net.daboross.jsonserialization.JsonSerialization;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class JSONScoreStorage extends SkyStorageBackend {

    private final Path saveFileBuffer;
    private final Path saveFile;
    private final Path oldSaveFile;
    private final Map<String, Object> baseJson;
    private Map<String, Object> nameToScore;
    private Map<String, Object> uuidToStoredPlayer;

    public JSONScoreStorage(SkyWars plugin) throws IOException, FileNotFoundException {
        super(plugin);
        this.oldSaveFile = plugin.getDataFolder().toPath().resolve("score.json");
        this.saveFile = plugin.getDataFolder().toPath().resolve("score-v1.json");
        this.saveFileBuffer = plugin.getDataFolder().toPath().resolve("score-v1.json~");
        this.baseJson = load();
        this.nameToScore = getMap(this.baseJson, "legacy-name-score");
        this.uuidToStoredPlayer = getMap(this.baseJson, "uuid-players-v1");
    }

    private Map<String, Object> load() throws IOException, FileNotFoundException {
        if (!Files.exists(saveFile)) {

            Map<String, Object> newStorage = new HashMap<>();
            newStorage.put("uuid-players-v1", new HashMap<>());
            if (Files.exists(oldSaveFile)) {
                skywars.getLogger().log(Level.INFO, "Found old score storage file, attempting to import data");
                Map<String, Object> oldStorage = loadFile(oldSaveFile);
                newStorage.put("legacy-name-score", oldStorage); // Be lazy and just copy the whole Map<String, Object>.
            } else {
                newStorage.put("legacy-name-score", new HashMap<>());
            }
            return newStorage;
        } else {
            return loadFile(saveFile);
        }
    }

    private Map<String, Object> loadFile(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IOException("File '" + file.toAbsolutePath() + "' is not a file (perhaps a directory?).");
        }

        try (FileInputStream fis = new FileInputStream(file.toFile());
             InputStreamReader reader = new InputStreamReader(fis);
             BufferedReader bufReader = new BufferedReader(reader)) {
            return new JsonParser(bufReader).parseJsonObject();
        } catch (JsonException ex) {
            try (FileInputStream fis = new FileInputStream(file.toFile())) {
                byte[] buffer = new byte[10];
                int read = fis.read(buffer);
                String str = new String(buffer, 0, read, Charset.forName("UTF-8"));
                if (StringUtils.isBlank(str)) {
                    skywars.getLogger().log(Level.WARNING, "File {} is empty, perhaps it was corrupted? Ignoring it and starting new score database. If you haven't recorded any data, this won't matter.", file.toAbsolutePath());
                    return new HashMap<>();
                }
            }
            throw new IOException("JsonException loading " + file.toAbsolutePath(), ex);
        }
    }

    @Override
    public void save() throws IOException {
        if (!Files.exists(saveFileBuffer)) {
            Files.createFile(saveFileBuffer);
        }
        try (FileOutputStream fos = new FileOutputStream(saveFileBuffer.toFile())) {
            try (OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"))) {
                JsonSerialization.writeJsonObject(writer, baseJson, 0, 0);
            }
        } catch (IOException | JsonException ex) {
            throw new IOException("Couldn't write to " + saveFileBuffer.toAbsolutePath(), ex);
        }
        try {
            Files.move(saveFileBuffer, saveFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            throw new IOException("Failed to move buffer file '" + saveFileBuffer.toAbsolutePath() + "' to actual save location '" + saveFile + "'", ex);
        }
    }

    @Override
    public SkyInternalPlayer loadPlayer(final Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        Map<String, Object> playerMap = getMap(uuidToStoredPlayer, uuid);
        if (playerMap == null) {
            playerMap = new HashMap<>();
            uuidToStoredPlayer.put(uuid, playerMap);
            playerMap.put("username", name);
            if (nameToScore.containsKey(name.toLowerCase())) {
                SkyStatic.debug("Migrated score for %s to UUID (uuid: %s)", name, uuid);
                playerMap.put("score", nameToScore.get(name.toLowerCase()));
                nameToScore.remove(name.toLowerCase());
            } else {
                playerMap.put("score", 0);
            }
        } else {
            if (!playerMap.containsKey("username")) {
                playerMap.put("username", name);
            } else if (!playerMap.get("username").equals(name)) {
                SkyStatic.log("Username of (uuid: %s) changed from %s to %s", uuid, playerMap.get("username"), name);
                playerMap.put("username", name);
            }
            if (!playerMap.containsKey("score")) {
                playerMap.put("score", 0);
            }
        }
        return new JSONSkyPlayer(player, playerMap);
    }

    @Override
    public void addScore(final UUID uuid, final int diff) {
        Map<String, Object> playerMap = getMap(uuidToStoredPlayer, uuid.toString());
        if (playerMap == null) {
            playerMap = new HashMap<>();
            playerMap.put("score", diff); // assume the default score is 0
        } else {
            playerMap.put("score", getInt(playerMap, "score") + diff);
        }
    }

    @Override
    public void setScore(final UUID uuid, final int score) {
        Map<String, Object> playerMap = getMap(uuidToStoredPlayer, uuid.toString());
        if (playerMap == null) {
            playerMap = new HashMap<>();
        }
        playerMap.put("score", score);
    }

    private int getScore(final UUID uuid) {
        Map<String, Object> playerMap = getMap(uuidToStoredPlayer, uuid.toString());
        return playerMap == null ? 0 : getInt(playerMap, "score");
    }

    @Override
    public void getScore(final UUID uuid, final ScoreCallback callback) {
        callback.scoreGetCallback(getScore(uuid));
    }

    public class JSONSkyPlayer extends AbstractSkyPlayer {

        private final Map<String, Object> playerMap;

        public JSONSkyPlayer(final Player player, final Map<String, Object> obj) {
            super(player);
            playerMap = obj;
        }

        @Override
        public void loggedOut() {
        }

        @Override
        public int getScore() {
            return getInt(playerMap, "score");
        }

        @Override
        public void setScore(final int score) {
            playerMap.put("score", score);
        }

        @Override
        public void addScore(final int diff) {
            playerMap.put("score", getScore() + diff);
        }
    }

    private Map<String, Object> getMap(Map<String, Object> map, String key) {
        Object object = map.get(key);
        if (object instanceof Map) {
            //noinspection unchecked
            return (Map<String, Object>) object;
        }
        return null;
    }

    private int getInt(Map<String, Object> map, String key) {
        Object object = map.get(key);
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        if (object != null) {
            try {
                return Integer.parseInt(object.toString());
            } catch (NumberFormatException ignored) {
                throw new NullArgumentException("Invalid score found in score data: not a number! (" + map + ").");
            }
        }
        return 0;
    }
}

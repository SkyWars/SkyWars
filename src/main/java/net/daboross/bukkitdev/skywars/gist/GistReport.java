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
package net.daboross.bukkitdev.skywars.gist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;
import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArena;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.config.SkyConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 *
 */
public class GistReport {

    private static final String GIST_API = "https://api.github.com/gists";
    private static final Object GIST_API_URL_LOCK = new Object();
    private static URL GIST_API_URL;
    private static final String ISGD_API = "http://is.gd/create.php?format=simple&url=%s";

    /**
     *
     * @param configuration Configuration to generate report of
     * @return Raw report text
     */
    public static String generateReportText(@NonNull SkyConfiguration configuration) {
        StringBuilder dataB = new StringBuilder();
        for (SkyArenaConfig arena : configuration.getEnabledArenas()) {
            appendArena(dataB.append("###").append(arena.getArenaName()), arena);
        }
        appendArena(dataB.append("###Parent"), configuration.getParentArena());
        dataB.append("###Configuration\n```\n")
                .append(configuration.getRawConfig().saveToString())
                .append("\n```");
        return dataB.toString();
    }

    private static StringBuilder appendArena(StringBuilder builder, SkyArena arena) {
        YamlConfiguration arenaYaml = new YamlConfiguration();
        arena.serialize(arenaYaml);
        builder.append("\n```\n")
                .append(arenaYaml.saveToString())
                .append("\n```\n");
        return builder;
    }

    /**
     *
     * @param reportText The text generated at some point by generateReportText
     * @return A shortened URL for the report
     */
    public static String reportReport(String reportText) {
        String reportURL = gistText("SkyWars gist report", "report.md", reportText);
        return shortenURL(reportURL);
    }

    /**
     *
     * @param gistDescription Description of the gist
     * @param gistFileName File name for the gist
     * @param gistText Test for the gist
     * @return
     */
    public static String gistText(String gistDescription, String gistFileName, String gistText) {
        if (!checkGistURL()) {
            return null;
        }
        URLConnection connection;
        try {
            connection = GIST_API_URL.openConnection();
        } catch (IOException ex) {
            SkyStatic.getLogger().log(Level.WARNING, "[SkyGistReport] Failed to open a connecting with ''{0}'': {1}", new Object[]{GIST_API, ex.toString()});
            return null;
        }
        connection.setDoOutput(true);
        connection.setDoInput(true);
        String jsonOutputString;
        try {
            jsonOutputString = new JSONStringer().object()
                    .key("description").value(gistDescription)
                    .key("public").value("false")
                    .key("files").object()
                    .key("report.md").object()
                    .key("content").value(gistText)
                    .endObject().endObject().endObject().toString();
        } catch (JSONException ex) {
            SkyStatic.getLogger().log(Level.FINE, "[SkyGistReport] Failed to encode report contents in JSON: {0}", ex.toString());
            return null;
        }
        try (OutputStream outputStream = connection.getOutputStream()) {
            try (OutputStreamWriter requestWriter = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"))) {
                requestWriter.append(jsonOutputString);
                requestWriter.close();
            }
        } catch (IOException ex) {
            SkyStatic.getLogger().log(Level.FINE, "[SkyGistReport] Failed to write output to gist: {0}", ex.toString());
            return null;
        }

        JSONObject inputJson;
        try {
            inputJson = new JSONObject(readConnection(connection));
        } catch (JSONException | IOException ex) {
            SkyStatic.getLogger().log(Level.FINE, "[SkyGistReport] Failed to read response from gist: {0}", ex.toString());
            return null;
        }
        String resultUrl = inputJson.optString("html_url", null);
        return resultUrl;
    }

    /**
     *
     * @param url URL To shorten
     * @return Shortened URL
     */
    @SneakyThrows(UnsupportedEncodingException.class) // Never going to be thrown for UTF-8
    public static String shortenURL(String url) {
        final Logger logger = SkyStatic.getLogger();
        URL requestUrl;
        String requestUrlString = String.format(ISGD_API, URLEncoder.encode(url, "UTF-8"));
        try {
            requestUrl = new URL(requestUrlString);
        } catch (MalformedURLException ex) {
            logger.log(Level.FINE, "[SkyGistReport] Failed to encode url {0}: {1}", new Object[]{requestUrlString, ex.toString()});
            return url;
        }
        URLConnection connection;
        try {
            connection = requestUrl.openConnection();
            return readConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.FINE, "[SkyGistReport] Failed to read is.gd response", ex);
            return url;
        }
    }

    public static String readConnection(URLConnection connection) throws IOException {
        try (InputStream inputStream = connection.getInputStream()) {
            try (Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
                StringBuilder result = new StringBuilder();
                char[] buffer = new char[128];
                int length;
                while ((length = reader.read(buffer)) > 0) {
                    result.append(buffer, 0, length);
                }
                return result.toString();
            }
        }
    }

    private static boolean checkGistURL() {
        synchronized (GIST_API_URL_LOCK) {
            if (GIST_API_URL == null) {
                try {
                    GIST_API_URL = new URL(GIST_API);
                } catch (MalformedURLException ex) {
                    SkyStatic.getLogger().log(Level.WARNING, "[SkyGistReport] Couldn''t encode ''{0}'' as a URL: {1}; Please notify the developer with this error ASAP.", new Object[]{GIST_API, ex.toString()});
                    return false;
                }
            }
        }
        return true;
    }
}

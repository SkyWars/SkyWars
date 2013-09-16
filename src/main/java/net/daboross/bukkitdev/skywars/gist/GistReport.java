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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 *
 */
public class GistReport {

    public static String joinText(Iterable<String> iterable) {
        Iterator<String> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(iterator.next());
        while (iterator.hasNext()) {
            builder.append("\n").append(iterator.next());
        }
        return builder.toString();
    }

    public static String gistText(Logger logger, String text) {
        URL postUrl;
        try {
            postUrl = new URL("https://api.github.com/gists");
        } catch (MalformedURLException ex) {
            logger.log(Level.FINE, "Non severe error encoding api.github.com URL", ex);
            return null;
        }
        URLConnection connection;
        try {
            connection = postUrl.openConnection();
        } catch (IOException ex) {
            logger.log(Level.FINE, "Non severe error opening api.github.com connection", ex);
            return null;
        }
        connection.setDoOutput(true);
        connection.setDoInput(true);
        JSONStringer outputJson = new JSONStringer();
        try {
            outputJson.object()
                    .key("description").value("SkyWars debug")
                    .key("public").value("false")
                    .key("files").object()
                    .key("report.md").object()
                    .key("content").value(text)
                    .endObject().endObject();
        } catch (JSONException ex) {
            logger.log(Level.FINE, "Non severe error while writing report", ex);
            return null;
        }

        try (OutputStream outputStream = connection.getOutputStream()) {
            try (OutputStreamWriter requestWriter = new OutputStreamWriter(outputStream)) {
                requestWriter.append(outputJson.toString());
                requestWriter.close();
            }
        } catch (IOException ex) {
            logger.log(Level.FINE, "Non severe error writing report", ex);
            return null;
        }

        JSONObject inputJson;
        try {
            inputJson = new JSONObject(readConnection(connection));
        } catch (JSONException | IOException unused) {
            logger.log(Level.FINE, "Non severe error while reading response for report.", unused);
            return null;
        }
        String resultUrl = inputJson.optString("url", null);
        return resultUrl == null ? null : shortenURL(logger, resultUrl);
    }

    public static String shortenURL(Logger logger, String url) {
        URL requestUrl;
        try {
            requestUrl = new URL("http://is.gd/create.php?format=simple&url=" + URLEncoder.encode(url, "UTF-8"));
        } catch (MalformedURLException | UnsupportedEncodingException ex) {
            logger.log(Level.FINE, "Non severe error encoding is.gd URL", ex);
            return url;
        }
        URLConnection connection;
        try {
            connection = requestUrl.openConnection();
            return readConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.FINE, "Non severe error getting is.gd response", ex);
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
}

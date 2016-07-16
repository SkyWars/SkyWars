package net.daboross.bukkitdev.skywars.libraries.pluginstatistics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.daboross.jsonserialization.JsonException;
import net.daboross.jsonserialization.JsonSerialization;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Plugin statistics reporting. For more information, see https://github.com/daboross/plugin-statistics.
 * <p>
 * This class is copied from https://github.com/daboross/plugin-statistics (release v1), with a few extremely small
 * edits to use the shaded version of JsonSerialization instead of an internal class.
 */
public class PluginStatistics {

    private static final String API_URL_FORMAT = "https://dabo.guru/statistics/v1/%s/post";
    private static final long INTERVAL_SECONDS = 60 * 60; // Report every hour.
    private static final Object taskLock = new Object();

    private final Plugin plugin;
    private final UUID instanceUuid;
    private final boolean debug;
    private int taskId;

    public PluginStatistics(Plugin plugin, final boolean debug) {
        Validate.notNull(plugin);
        this.plugin = plugin;
        this.debug = debug;
        this.instanceUuid = UUID.randomUUID();
        this.taskId = -1;
    }

    public void start() {
        synchronized (taskLock) {
            if (taskId != -1) {
                return;
            }
            long intervalTicks = INTERVAL_SECONDS * 20;
            taskId = new ReportRunnable().runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks).getTaskId();
        }
    }

    public void stop() {
        synchronized (taskLock) {
            if (taskId == -1) {
                return;
            }
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public class ReportRunnable extends BukkitRunnable {

        @Override
        public void run() {
            String pluginName = plugin.getName();
            String pluginVersion = plugin.getDescription().getVersion();
            String serverVersion = plugin.getServer().getVersion();
            int onlinePlayers = 0;
            try {
                onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            } catch (NoSuchMethodError ex) {
                try {
                    Method m = Server.class.getMethod("getOnlinePlayers");
                    onlinePlayers = ((Player[]) m.invoke(plugin.getServer())).length;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e2) {
                    if (debug) {
                        plugin.getLogger().log(Level.WARNING, "[statistics] Unable to get online player count.", e2);
                    }
                }
            }

            Map<String, Object> dataMap = new HashMap<>(4);
            dataMap.put("instance_uuid", instanceUuid.toString());
            dataMap.put("plugin_version", pluginVersion);
            dataMap.put("server_version", serverVersion);
            dataMap.put("online_players", onlinePlayers);

            URL apiUrl;
            try {
                apiUrl = new URL(String.format(API_URL_FORMAT, URLEncoder.encode(pluginName, "UTF-8")));
            } catch (MalformedURLException | UnsupportedEncodingException ex) {
                if (debug) {
                    plugin.getLogger().log(Level.WARNING, "[statistics] Failed to encode API URL.", ex);
                }
                return;
            }

            byte[] encodedData;

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                // By using an inner try-with-resources block, the outputstreamwriter will always be flushed&closed before
                // calling byteArrayOutpuStream.toByteArray().
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8")) {
                    JsonSerialization.writeJsonObject(outputStreamWriter, dataMap, 0, 0);
                }
                encodedData = byteArrayOutputStream.toByteArray();
            } catch (IOException | JsonException ex) {
                if (debug) {
                    plugin.getLogger().log(Level.WARNING, "[statistics] Failed to encode and compress data to submit.", ex);
                }
                return;
            }

            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) apiUrl.openConnection();
            } catch (IOException | ClassCastException ex) {
                if (debug) {
                    plugin.getLogger().log(Level.WARNING, "[statistics] Failed to initiate connection.", ex);
                }
                return;
            }
            connection.addRequestProperty("Accept", "*/*");
            connection.addRequestProperty("Content-Length", String.valueOf(encodedData.length));
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.addRequestProperty("Connection", "close");
            connection.addRequestProperty("User-Agent", "plugin-statistics/v1");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            int responseCode;

            try {
                connection.connect();

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(encodedData);
                }

                responseCode = connection.getResponseCode();
            } catch (IOException ex) {
                if (debug) {
                    plugin.getLogger().log(Level.WARNING, "[statistics] Failed to connect to service.", ex);
                }
                return;
            }

            if (debug && responseCode != 200) {
                plugin.getLogger().log(Level.WARNING, "[statistics] Service returned non-OK response code: {0}", responseCode);
                try (StringWriter writer = new StringWriter()) {
                    JsonSerialization.writeJsonObject(writer, dataMap, 0, 0);
                    plugin.getLogger().log(Level.INFO, "[statistics] POST data which caused this error: {0}", writer.toString());
                } catch (JsonException | IOException ex) {
                    plugin.getLogger().log(Level.WARNING, "[statistics] Failed to pretty-print data (to show the POST request which caused the error).", ex);
                }
            }
        }
    }
}

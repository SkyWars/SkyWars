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
package net.daboross.bukkitdev.skywars.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
public class WorldUnzipper {

    private final Plugin plugin;

    public WorldUnzipper(@NonNull Plugin plugin) {
        this.plugin = plugin;
    }

    public WorldUnzipResult doWorldUnzip() {
        File output = new File(Statics.BASE_WORLD_NAME);
        if (output.exists()) {
            return WorldUnzipResult.ALREADY_THERE;
        }
        output.mkdir();
        InputStream fis = this.getClass().getResourceAsStream(Statics.ZIP_FILE_PATH);
        if (fis == null) {
            return WorldUnzipResult.ERROR;
        }
        try {
            try (ZipInputStream zis = new ZipInputStream(fis)) {
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String fileName = ze.getName();
                    File newFile = new File(output, fileName);
                    File parent = newFile.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    if (ze.isDirectory()) {
                        plugin.getLogger().log(Level.FINER, "Making dir {0}", newFile);
                        newFile.mkdir();
                    } else if (newFile.exists()) {
                        plugin.getLogger().log(Level.FINER, "Already exists {0}", newFile);
                    } else {
                        plugin.getLogger().log(Level.FINER, "Copying {0}", newFile);
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            try {
                                int next;
                                while ((next = zis.read()) != -1) {
                                    fos.write(next);
                                }
                                fos.flush();
                            } catch (IOException ex) {
                                plugin.getLogger().log(Level.WARNING, "Error copying file from zip", ex);
                                return WorldUnzipResult.ERROR;
                            }
                            fos.close();
                        }
                    }
                    try {
                        ze = zis.getNextEntry();
                    } catch (IOException ex) {
                        plugin.getLogger().log(Level.WARNING, "Error getting next zip entry", ex);
                        return WorldUnzipResult.ERROR;
                    }
                }
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error unzipping base world", ex);
            return WorldUnzipResult.ERROR;
        }
        return WorldUnzipResult.CREATED;
    }

    public static enum WorldUnzipResult {

        ERROR, CREATED, ALREADY_THERE
    }
}

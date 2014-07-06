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
package net.daboross.bukkitdev.skywars.world;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.daboross.bukkitdev.skywars.StartupFailedException;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

public class WorldUnzipper {

    public void doWorldUnzip(Logger logger) throws StartupFailedException {
        Validate.notNull(logger, "Logger cannot be null");
        Path outputDir = Bukkit.getWorldContainer().toPath().resolve(Statics.BASE_WORLD_NAME);
        if (Files.exists(outputDir)) {
            return;
        }
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new StartupFailedException("Couldn't create directory " + outputDir.toAbsolutePath() + ".");
        }

        InputStream fis = WorldUnzipper.class.getResourceAsStream(Statics.ZIP_FILE_PATH);
        if (fis == null) {
            throw new StartupFailedException("Couldn't get resource.\nError creating world. Please delete " + Statics.BASE_WORLD_NAME + " and restart server.");
        }
        try {
            try (ZipInputStream zis = new ZipInputStream(fis)) {
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String fileName = ze.getName();
                    Path newFile = outputDir.resolve(fileName);
                    Path parent = newFile.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    if (ze.isDirectory()) {
                        logger.log(Level.FINER, "Making dir {0}", newFile);
                        Files.createDirectories(newFile);
                    } else if (Files.exists(newFile)) {
                        logger.log(Level.FINER, "Already exists {0}", newFile);
                    } else {
                        logger.log(Level.FINER, "Copying {0}", newFile);
                        try (FileOutputStream fos = new FileOutputStream(newFile.toFile())) {
                            try {
                                int next;
                                while ((next = zis.read()) != -1) {
                                    fos.write(next);
                                }
                                fos.flush();
                            } catch (IOException ex) {
                                logger.log(Level.WARNING, "Error copying file from zip", ex);
                                throw new StartupFailedException("Error creating world. Please delete " + Statics.BASE_WORLD_NAME + " and restart server.");
                            }
                            fos.close();
                        }
                    }
                    try {
                        ze = zis.getNextEntry();
                    } catch (IOException ex) {
                        throw new StartupFailedException("Error getting next zip entry\nError creating world. Please delete " + Statics.BASE_WORLD_NAME + " and restart server.", ex);
                    }
                }
            }
        } catch (IOException | RuntimeException ex) {
            throw new StartupFailedException("\nError unzipping world. Please delete " + Statics.BASE_WORLD_NAME + " and restart server.", ex);
        }
    }
}

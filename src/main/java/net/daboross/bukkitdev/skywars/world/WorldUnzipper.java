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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.NonNull;
import net.daboross.bukkitdev.skywars.StartupFailedException;

public class WorldUnzipper {

    public void doWorldUnzip(@NonNull Logger logger) throws StartupFailedException {
        File output = new File(Statics.BASE_WORLD_NAME);
        if (output.exists()) {
            return;
        }
        boolean madeDir = output.mkdir();
        if (!madeDir) {
            throw new StartupFailedException("Couldn't make directory " + output.getAbsolutePath() + ". Please delete " + Statics.BASE_WORLD_NAME + " and restart server.");
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
                    File newFile = new File(output, fileName);
                    File parent = newFile.getParentFile();
                    if (parent != null) {
                        boolean madeParent = parent.mkdirs();
                        if (!madeParent) {
                            logger.log(Level.FINER, "Couldn''t make directory {0}", parent.getAbsolutePath());
                        }
                    }
                    if (ze.isDirectory()) {
                        logger.log(Level.FINER, "Making dir {0}", newFile);
                        boolean made = newFile.mkdirs();
                        if (!made) {
                            logger.log(Level.FINER, "Couldn''t make directory {0}", newFile.getAbsolutePath());
                        }
                    } else if (newFile.exists()) {
                        logger.log(Level.FINER, "Already exists {0}", newFile);
                    } else {
                        logger.log(Level.FINER, "Copying {0}", newFile);
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
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

    public static enum WorldUnzipResult {

        ERROR, CREATED, ALREADY_THERE
    }
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;

/**
 *
 * @author daboross
 */
public class WorldUnzipper {

    private static final String ZIP_FILE_PATH = "/SkyWarsBaseWorld.zip";
    private final SkyWarsPlugin plugin;

    public WorldUnzipper(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public WorldUnzipResult doWorldUnzip() {
        File output = new File(Statics.BASE_WORLD_NAME);
        if (output.exists()) {
            return WorldUnzipResult.ALREADY_THERE;
        }
        output.mkdir();
        InputStream fis = this.getClass().getResourceAsStream(ZIP_FILE_PATH);
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

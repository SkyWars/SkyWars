/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.world;

import java.io.File;
import java.io.FileNotFoundException;
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

    public boolean doWorldUnzip() {
        File output = new File(Statics.BASE_WORLD_NAME);
        if (output.exists()) {
            plugin.getLogger().log(Level.INFO, "Arena base world already exists. Not copying.");
            return true;
        }
        output.mkdir();
        File folder = output.getParentFile();
        byte[] buffer = new byte[1024];
        InputStream fis = this.getClass().getResourceAsStream(ZIP_FILE_PATH);
        if (fis == null) {
            return false;
        }
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze;
        try {
            ze = zis.getNextEntry();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(folder, fileName);
            new File(newFile.getParent()).mkdirs();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(newFile);
            } catch (FileNotFoundException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
                return false;
            }
            int len;
            try {
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
                return false;
            } finally {
                try {
                    fos.close();
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, null, ex);
                    return false;
                } finally {
                    try {
                        zis.closeEntry();
                    } catch (IOException ex) {
                        plugin.getLogger().log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            zis.close();
                        } catch (IOException ex) {
                            plugin.getLogger().log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            try {
                fos.close();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
                return false;
            } finally {
                try {
                    zis.closeEntry();
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        zis.close();
                    } catch (IOException ex) {
                        plugin.getLogger().log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                ze = zis.getNextEntry();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
                return false;
            } finally {
                try {
                    zis.closeEntry();
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        zis.close();
                    } catch (IOException ex) {
                        plugin.getLogger().log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        try {
            zis.closeEntry();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        } finally {
            try {
                zis.close();
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}

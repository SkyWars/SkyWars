/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.events;

import net.daboross.bukkitdev.skywars.SkyWarsPlugin;

/**
 *
 * @author daboross
 */
public interface UnloadListener {
    public void saveAndUnload(SkyWarsPlugin plugin);
}

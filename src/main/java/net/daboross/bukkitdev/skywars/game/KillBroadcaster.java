/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import net.daboross.bukkitdev.skywars.Messages;

/**
 *
 * @author daboross
 */
public class KillBroadcaster {

    public static String getMessage(String player, String damager, boolean causedVoid) {
        if (damager == null) {
            if (causedVoid) {
                return String.format(Messages.SUICIDE_VOID, player);
            } else {
                return String.format(Messages.SUICIDE, player);
            }
        } else {
            if (causedVoid) {
                return String.format(Messages.KILLED_VOID, damager, player);
            } else {
                return String.format(Messages.KILLED, damager, player);
            }
        }
    }
}

/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import net.daboross.bukkitdev.skywars.Messages;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 *
 * @author daboross
 */
public class KillBroadcaster {

    public static String getMessage(Player player) {
        EntityDamageEvent ede = player.getLastDamageCause();
        Entity damager = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            damager = ((EntityDamageByEntityEvent) ede).getDamager();
        }
        if (damager == null) {
            switch (ede.getCause()) {
                case VOID:
                    return String.format(Messages.SUICIDE_VOID, player.getName());
                default:
                    return String.format(Messages.SUICIDE, player.getName());
            }
        } else {
            String damagerName = (damager instanceof LivingEntity) ? ((LivingEntity) damager).getCustomName() : damager.getType().getName();
            switch (ede.getCause()) {
                case VOID:
                    return String.format(Messages.KILLED_VOID, damagerName, player.getName());
                default:
                    return String.format(Messages.KILLED, damagerName, player.getName());

            }
        }
    }
}

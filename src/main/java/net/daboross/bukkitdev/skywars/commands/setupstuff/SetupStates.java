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
package net.daboross.bukkitdev.skywars.commands.setupstuff;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class SetupStates {

    private final Map<String, SetupData> setupStates;

    public SetupStates() {
        this.setupStates = new HashMap<>();
    }

    public SetupData getSetupState(String user) {
        return setupStates.get(user.toLowerCase(Locale.ENGLISH));
    }

    public void setSetupState(String user, SetupData state) {
        if (state == null) {
            setupStates.remove(user.toLowerCase(Locale.ENGLISH));
        } else {
            setupStates.put(user.toLowerCase(Locale.ENGLISH), state);
        }
    }
}

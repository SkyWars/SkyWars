/*
 * Copyright (C) 2016 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.kits;

import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;

public class KitUtils {

    private KitUtils() {
    }

    public static String formatKitList(Iterable<SkyKit> kits) {
        StringBuilder builder = new StringBuilder();
        String comma = SkyTrans.get(TransKey.KITS_KIT_LIST_COMMA);
        for (SkyKit kit : kits) {
            if (kit.getCost() == 0) {
                builder.append(kit.getName());
            } else {
                builder.append(SkyTrans.get(TransKey.KITS_KIT_LIST_COST_ITEM, kit.getName(), kit.getCost()));
            }
            builder.append(comma);
        }
        return builder.toString();
    }
}

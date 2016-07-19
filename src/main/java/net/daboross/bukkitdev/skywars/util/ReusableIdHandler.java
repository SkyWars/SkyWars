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
package net.daboross.bukkitdev.skywars.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ReusableIdHandler {

    private final Queue<Integer> reusableIds = new ArrayDeque<>();
    private final AtomicInteger nextNewId = new AtomicInteger(0);

    public ReusableIdHandler() {
    }

    public int getNextId() {
        Integer id = reusableIds.poll();
        if (id == null) {
            id = nextNewId.incrementAndGet();
        }
        return id;
    }

    public void recycleId(int id) {
        reusableIds.add(id);
    }
}

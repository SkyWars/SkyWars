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
package net.daboross.bukkitdev.commandexecutorbase;

import java.util.Iterator;

public class ArrayHelpers {

    public static final String[] EMPTY_STRING = {};

    public static String[] getSubArray(String[] array, int startPos, int length) {
        if (startPos + length > array.length) {
            throw new ArrayIndexOutOfBoundsException("startPos + length > array.length");
        } else if (startPos < 0) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0");
        } else if (length < 0) {
            throw new ArrayIndexOutOfBoundsException("length < 0");
        } else if (length == 0) {
            return EMPTY_STRING;
        }
        String[] copy = new String[length];
        System.arraycopy(array, startPos, copy, 0, length);
        return copy;
    }

    public static <T> String combinedWithSeperator(Iterable<T> array, String seperator) {
        Iterator<T> iterator = array.iterator();
        if (!iterator.hasNext()) {
            return "";
        } else {
            String first = String.valueOf(iterator.next());
            if (!iterator.hasNext()) {
                return first;
            } else {
                StringBuilder resultBuilder = new StringBuilder(first);
                while (iterator.hasNext()) {
                    resultBuilder.append(seperator).append(iterator.next());
                }
                return resultBuilder.toString();
            }
        }
    }
}

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

    public static String combinedWithSeperator(Object[] array, String seperator) {
        if (array.length == 0) {
            return "";
        } else if (array.length == 1) {
            return String.valueOf(array[0]);
        } else {
            StringBuilder resultBuilder = new StringBuilder(String.valueOf(array[0]));
            for (int i = 1; i < array.length; i++) {
                resultBuilder.append(seperator).append(array[i]);
            }
            return resultBuilder.toString();
        }
    }
}

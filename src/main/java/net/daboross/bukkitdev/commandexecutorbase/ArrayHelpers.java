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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author daboross
 */
public class ArrayHelpers {

    public static final String[] EMPTY_STRING = {};

    public static String[] copyArray(String[] array) {
        if (array.length == 0) {
            return EMPTY_STRING;
        }
        String[] copy = new String[array.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    public static String[] copyAndInclude(String[] array, String... includedItems) {
        String[] copy = new String[array.length + includedItems.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        System.arraycopy(includedItems, 0, copy, array.length, includedItems.length);
        return copy;
    }

    public static <T> Set<T> copyToSet(T... items) {
        return new HashSet<T>(Arrays.asList(items));
    }

    public static Set<String> copyToSetLowercase(String... items) {
        Set<String> result = new HashSet<String>();
        for (String str : items) {
            result.add(str.toLowerCase(Locale.ENGLISH));
        }
        return result;
    }

    public static <T> List<T> copyToList(T... items) {
        return new ArrayList<T>(Arrays.asList(items));
    }

    public static List<String> copyToListLowercase(String... items) {
        List<String> result = new ArrayList<String>();
        for (String str : items) {
            result.add(str.toLowerCase(Locale.ENGLISH));
        }
        return result;
    }

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

    public static String combinedWithSeperator(List<?> list, String seperator) {
        if (list.isEmpty()) {
            return "";
        } else if (list.size() == 1) {
            return String.valueOf(list.get(0));
        } else {
            StringBuilder resultBuilder = new StringBuilder(String.valueOf(list.get(0)));
            for (int i = 1; i < list.size(); i++) {
                resultBuilder.append(seperator).append(list.get(i));
            }
            return resultBuilder.toString();
        }
    }
}

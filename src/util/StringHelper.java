/*
 * Copyright (C) 2012 Thinh Pham
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
package util;

/**
 *
 * @author Thinh Pham
 */
public class StringHelper {
    public static String[] split(String sb, String splitter) {
        String[] strs = new String[sb.length()];
        int splitterLength = splitter.length();
        int initialIndex = 0;
        int indexOfSplitter = sb.indexOf(splitter, initialIndex);
        int count = 0;
        if (-1 == indexOfSplitter) {
            return new String[]{sb.toString()};
        }
        while (-1 != indexOfSplitter) {
            char[] chars = new char[indexOfSplitter - initialIndex];
            sb.getChars(initialIndex, indexOfSplitter, chars, 0);
            initialIndex = indexOfSplitter + splitterLength;
            indexOfSplitter = sb.indexOf(splitter, indexOfSplitter + 1);
            strs[count] = new String(chars);
            count++;
        }
        // get the remaining chars.
        if (initialIndex + splitterLength <= sb.length()) {
            char[] chars = new char[sb.length() - initialIndex];
            sb.getChars(initialIndex, sb.length(), chars, 0);
            strs[count] = new String(chars);
            count++;
        }
        String[] result = new String[count];
        System.arraycopy(strs, 0, result, 0, count);
        return result;
    }
}

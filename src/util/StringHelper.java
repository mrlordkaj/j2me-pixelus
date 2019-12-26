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

import java.util.Random;

/**
 *
 * @author Thinh Pham
 */
public abstract class StringHelper {
    
    public static String[] split(String sb, String splitter) {
        String[] strs = new String[sb.length()];
        int splitLength = splitter.length();
        int initPos = 0;
        int splitPos = sb.indexOf(splitter, initPos);
        int count = 0;
        if (-1 == splitPos) {
            return new String[]{ sb };
        }
        while (-1 != splitPos) {
            char[] chars = new char[splitPos - initPos];
            sb.getChars(initPos, splitPos, chars, 0);
            initPos = splitPos + splitLength;
            splitPos = sb.indexOf(splitter, splitPos + 1);
            strs[count++] = new String(chars);
        }
        // get the remaining chars
        if (initPos + splitLength <= sb.length()) {
            char[] chars = new char[sb.length() - initPos];
            sb.getChars(initPos, sb.length(), chars, 0);
            strs[count++] = new String(chars);
        }
        String[] rs = new String[count];
        System.arraycopy(strs, 0, rs, 0, count);
        return rs;
    }
    
    public static byte[] randomDeviceId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuffer sb = new StringBuffer(32);
        Random rnd = new Random();
        for (int i = 0; i < 32; i++) {
            char c = chars[rnd.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString().getBytes();
    }
}

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
public class MathHelper {

    public static int round(double a) {
        if (a - (int) a >= 0.5) {
            return (int) a + 1;
        } else {
            return (int) a;
        }
    }

    public static String showTime(int seconds) {
        StringBuffer rs = new StringBuffer(8);
        int hours = (int) (seconds / 3600);
        seconds -= hours * 3600;
        int minutes = (int) (seconds / 60);
        seconds -= minutes * 60;
        if (hours < 10) {
            rs.append("0");
        }
        rs.append(hours);
        rs.append(":");
        if (minutes < 10) {
            rs.append("0");
        }
        rs.append(minutes);
        rs.append(":");
        if (seconds < 10) {
            rs.append("0");
        }
        rs.append(seconds);
        return rs.toString();
    }

    public static String RandomDeviceId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuffer sb = new StringBuffer(32);
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}

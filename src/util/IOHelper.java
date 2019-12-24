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

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Thinh Pham
 */
public class IOHelper {
    public static String read(String path) throws RuntimeException {
        try {
            InputStream is = InputStream.class.getResourceAsStream(path);
            StringBuffer sb = new StringBuffer();
            int chars;
            
            while ((chars = is.read()) != -1) {
                sb.append((char) chars);
            }
            return sb.toString();
        } catch (IOException ex) {
            throw new RuntimeException("IOReader failed to load file:" + path + " " + ex.getMessage());
        }
    }
    
    public static int getFileSize(String path) throws RuntimeException {
        try {
            int count = 0;
            InputStream is = InputStream.class.getResourceAsStream(path);
            while (is.read() != -1) count++;
            return count;
        } catch (IOException ex) {
            throw new RuntimeException("IOReader failed to load file:" + path + " " + ex.getMessage());
        }
    }
    
    public static String[] readPuzzleData(int puzzleId) throws RuntimeException {
        try {
            String[] rs = new String[3];
            InputStream _inStream = InputStream.class.getResourceAsStream("/data/puzzles.dat");
            StringBuffer buf = new StringBuffer();
            int c;

            int curLine = 1;
            while (((c = _inStream.read()) != -1)) {
                if (c == '\n') {
                    ++curLine;
                    c = _inStream.read();
                }
                if(curLine == puzzleId) {
                    if(c == '#') {
                        rs[0] = buf.toString();
                        buf = new StringBuffer();
                    } else {
                        buf.append((char) c);
                    }
                } else if(curLine > puzzleId) {
                    rs[1] = buf.toString();
                    rs[2] = read("/data/" + rs[0] + ".dat");
                    return rs;
                }
            }

            rs[1] = buf.toString();
            rs[2] = read("/data/" + rs[0] + ".dat");
            return rs;
        } catch (IOException ex) {
            throw new RuntimeException("Could not load data file: " + ex.getMessage());
        }
    }
}

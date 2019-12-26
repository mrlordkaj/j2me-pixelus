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
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public abstract class FileHelper {
    
    public static String readFile(String path) throws RuntimeException {
        try {
            InputStream is = InputStream.class.getResourceAsStream(path);
            StringBuffer sb = new StringBuffer();
            int c;
            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public static Image loadImage(String path) throws RuntimeException {
        try {
            InputStream is = Image.class.getResourceAsStream(path);
            return Image.createImage(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

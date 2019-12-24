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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author Thinh Pham
 */
public class ImageHelper {
    public static final Sprite medalSprite = new Sprite(ImageHelper.loadImage("/images/medal.png"), 24, 24);

    public static Image loadImage(String path) throws RuntimeException {
        Image image = null;

        try {
            InputStream in = Image.class.getResourceAsStream(path);
            image = Image.createImage(in);
        } catch (IOException ioe) {
            throw new RuntimeException("ImageLoader failed to load image:" + path + " " + ioe.getMessage());
        }

        return image;
    }
    
    public static Image createPixelMask(int size) {
        int imgSize = size*16;
        Image mask = Image.createImage(imgSize, imgSize);
        Graphics g = mask.getGraphics();
        g.setColor(0, 0, 0);
        for(int i = 1; i < 16; i++) {
            //vẽ chiều ngang
            g.drawLine(0, i*size, imgSize, i*size);
            //vẽ chiều dọc
            g.drawLine(i*size, 0, i*size, imgSize);
        }
        int[] rgb = new int[imgSize*imgSize];
        mask.getRGB(rgb, 0, imgSize, 0, 0, imgSize, imgSize);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffffffff) {
                rgb[i] &= 0x00ffffff;
            } else {
                rgb[i] &= 0x22000000;
            }
        }
        mask = Image.createRGBImage(rgb, imgSize, imgSize, true);
        return mask;
    }
}

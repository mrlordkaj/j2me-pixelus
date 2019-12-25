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
public abstract class ImageHelper {
    
    public static final Sprite MEDAL_SPRITE = new Sprite(ImageHelper.loadImage("/images/medal.png"), 24, 24);

    public static Image loadImage(String path) throws RuntimeException {
        try {
            InputStream is = Image.class.getResourceAsStream(path);
            return Image.createImage(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public static Image createPixelMask(int pixelSize) {
        int imgSize = pixelSize*16;
        Image mask = Image.createImage(imgSize, imgSize);
        Graphics g = mask.getGraphics();
        g.setColor(0, 0, 0);
        for (int i = 1; i < 16; i++) {
            // draw horizontal
            g.drawLine(0, i*pixelSize, imgSize, i*pixelSize);
            // draw vertical
            g.drawLine(i*pixelSize, 0, i*pixelSize, imgSize);
        }
        int[] rgb = new int[imgSize*imgSize];
        mask.getRGB(rgb, 0, imgSize, 0, 0, imgSize, imgSize);
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] &= (rgb[i] == 0xffffffff) ? 0x00ffffff : 0x22000000;
        }
        return Image.createRGBImage(rgb, imgSize, imgSize, true);
    }
}

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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author Thinh Pham
 */
public class GraphicButton {
    private Sprite sprite;
    private int x, y, width, height;
    private byte cmd;
    public int active = 0;
    
    public GraphicButton(String imagePath, byte _cmd, int _x, int _y, int _width, int _height) {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        cmd = _cmd;
        sprite = new Sprite(ImageHelper.loadImage(imagePath), width, height);
    }
    
    public GraphicButton(Image img, byte _cmd, int _x, int _y, int _width, int _height) {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        cmd = _cmd;
        sprite = new Sprite(img, width, height);
    }
    
    public byte getCommand() { return cmd; }
    
    public void paint(Graphics g) {
        sprite.setFrame(active);
        sprite.setPosition(x, y);
        sprite.paint(g);
    }
    
    public boolean contains(int _x, int _y) {
        if(_x > x && _x < x + width && _y > y && _y < y + height) return true;
        return false;
    }
}

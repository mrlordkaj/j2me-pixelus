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
public class ButtonSprite extends Button {
    
    private final Sprite sprite;
    private final byte cmd;
    public boolean active;
    
    public ButtonSprite(String imagePath, byte cmd, int x, int y, int width, int height) {
        this(ImageHelper.loadImage(imagePath), cmd, x, y, width, height);
    }
    
    public ButtonSprite(Image img, byte cmd, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.cmd = cmd;
        sprite = new Sprite(img, width, height);
        sprite.setPosition(x, y);
    }
    
    public byte getCommand() {
        return cmd;
    }
    
    public void paint(Graphics g) {
        sprite.setFrame(active ? 1 : 0);
        sprite.paint(g);
    }
}

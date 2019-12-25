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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
abstract class LazyScene extends GameScene {
    
    private static Sprite loadingSprite;
    
    Main main;
    
    LazyScene(Main parent) {
        super();
        this.main = parent;
    }
    
    final void startLazyLoad() {
        new LazyLoad(this).start();
    }
    
    boolean isLoading(Graphics g) {
        if (isLoading) {
            if (loadingSprite == null) {
                loadingSprite = new Sprite(ImageHelper.loadImage("/images/juggling.png"), 20, 26);
                loadingSprite.setPosition(Main.SCREEN_WIDTH / 2 - 46, Main.SCREEN_HEIGHT / 2);
            }
            g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
            loadingSprite.nextFrame();
            loadingSprite.paint(g);
            g.setColor(255, 255, 255);
            g.drawString("loading...", Main.SCREEN_WIDTH / 2 - 16, Main.SCREEN_HEIGHT / 2 + 20, Graphics.LEFT | Graphics.BASELINE);
        }
        return isLoading;
    }
}

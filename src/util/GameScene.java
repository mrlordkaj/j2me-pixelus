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
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author Thinh Pham
 */
public abstract class GameScene extends GameCanvas implements Runnable {
    
//#if ScreenWidth == 400
//#     public static final int SCREEN_WIDTH = 400;
//#     public static final int SCREEN_HEIGHT = 240;
//#elif ScreenWidth == 320
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 240;
//#endif
    
    private static Sprite loadingSprite;
    
    protected abstract void load();
    protected abstract void unload();
    protected abstract void update();
    
    private short framePeriod;
    protected boolean isLoading = true; // TODO: make private
    private boolean isPlaying;
    private final Thread playThread;
    
    protected GameScene() {
        super(false);
        setFullScreenMode(true);
        playThread = new Thread(this);
    }
    
    public void run() {
        while (isPlaying) {
            update();
            repaint();
            try {
                Thread.sleep(framePeriod);
            } catch (InterruptedException ex) { }
        }
    }
    
    public final void play(int framePeriod) {
        this.framePeriod = (short) framePeriod;
        if (!isPlaying) {
            isPlaying = true;
            playThread.start();
        }
    }
    
    public final void destroy() {
        isPlaying = false;
        isLoading = true;
        playThread.interrupt();
        unload();
    }
    
    public final void lazyLoad() {
        new LazyLoad(this).start();
    }
    
    protected boolean repaintLoading(Graphics g) {
        if (isLoading) {
            if (loadingSprite == null) {
                loadingSprite = new Sprite(ImageHelper.loadImage("/images/juggling.png"), 20, 26);
                loadingSprite.setPosition(SCREEN_WIDTH / 2 - 46, SCREEN_HEIGHT / 2);
            }
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            loadingSprite.nextFrame();
            loadingSprite.paint(g);
            g.setColor(255, 255, 255);
            g.drawString("loading...", SCREEN_WIDTH / 2 - 16, SCREEN_HEIGHT / 2 + 20, Graphics.LEFT | Graphics.BASELINE);
        }
        return isLoading;
    }
    
    public boolean isLoading() {
        return isLoading;
    }
}

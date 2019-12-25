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

import javax.microedition.lcdui.game.GameCanvas;

/**
 *
 * @author Thinh Pham
 */
abstract class GameScene extends GameCanvas implements Runnable {
    
    abstract void load();
    abstract void unload();
    abstract void update();
    
    short framePeriod;
    boolean isLoading = true;
    private boolean isPlaying;
    final Thread thread;
    
    GameScene() {
        super(false);
        setFullScreenMode(true);
        thread = new Thread(this);
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
    
    final void begin(int framePeriod) {
        this.framePeriod = (short) framePeriod;
        if (!isPlaying) {
            isPlaying = true;
            thread.start();
        }
    }
    
    final void destroy() {
        isPlaying = false;
        isLoading = true;
        thread.interrupt();
        unload();
    }
    
    boolean isLoading() {
        return isLoading;
    }
}

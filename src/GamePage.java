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
public abstract class GamePage extends GameCanvas implements Runnable {
    public short schedule;
    protected boolean isLoading = true, pageLooping = true;
    
    public GamePage() {
        super(false);
        setFullScreenMode(true);
    }
    
    public void run() {
        while(pageLooping) {
            update();
            repaint();
            try {
                Thread.sleep(schedule);
            } catch (InterruptedException ex) {}
        }
    }
    
    protected abstract void update();
    
    public abstract void dispose();
}

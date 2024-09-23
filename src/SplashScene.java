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

import util.GameScene;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.FileHelper;

/**
 *
 * @author Thinh Pham
 */
public class SplashScene extends GameScene {
    
    public static final String TEXT_WAITING = "Tap to continue!";
    private static final int SCREEN_NONE = 0;
    private static final int SCREEN_OPENITVN = 1;
    private static final int SCREEN_SPLASH = 2;
    
    private boolean showWaitingText = false;
    private int waiting = 0;
    private int currentScreen = SCREEN_NONE;
    private Image backgroundTexture;
    
    public SplashScene() {
        super();
        play(2000);
    }
    
    protected void load() {
        
    }
    
    protected void unload() {
        backgroundTexture = null;
    }
    
    protected void update(){
        if (currentScreen == SCREEN_SPLASH) {
            showWaitingText = !showWaitingText;
        } else {
            nextScreen();
        }
        
        if (waiting > 0) {
            if (--waiting == 0) {
                play(2000);
                nextScreen();
            }
        }
    }
    
    public void paint(Graphics g) {
        if (isLoading()) {
            g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
            return;
        }
        
        // intro page
        g.drawImage(backgroundTexture, 0, 0, Graphics.TOP | Graphics.LEFT);
        if (currentScreen == SCREEN_SPLASH && showWaitingText) {
            g.setColor(0, 0, 0);
            g.drawString(TEXT_WAITING, Main.SCREEN_WIDTH / 2 + 2, Main.SCREEN_HEIGHT - 16 + 2, Graphics.HCENTER | Graphics.BASELINE);
            g.setColor(255, 255, 255);
            g.drawString(TEXT_WAITING, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 16, Graphics.HCENTER | Graphics.BASELINE);
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if (currentScreen == SCREEN_SPLASH) {
            Main.getInstance().gotoMainMenu();
        }
    }
    
    private void nextScreen() {
        isLoading = true;
        currentScreen++;
        switch (currentScreen) {
            case SCREEN_OPENITVN:
                backgroundTexture = FileHelper.loadImage("/images/logoopenitvn.png");
                break;
                
            case SCREEN_SPLASH:
                backgroundTexture = FileHelper.loadImage("/images/splash.png");
                play(500);
                break;
        }
        isLoading = false;
    }
}

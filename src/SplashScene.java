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
import InneractiveSDK.IADView;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class SplashScene extends GameScene {
    
    public static final String TEXT_WAITING = "Tap to continue!";
    private static final int SCREEN_NONE = 0;
    private static final int SCREEN_OPENITVN = 1;
    private static final int SCREEN_SPONSOR = 2;
    private static final int SCREEN_SPLASH = 3;
    
    private boolean showWaitingText = false;
    private int waiting = 0;
    private int currentScreen = SCREEN_NONE;
    private Image backgroundTexture;
    private Vector ads;
    
    private final Main parent;
    
    public SplashScene(Main parent) {
        super();
        this.parent = parent;
        play(2000);
    }
    
    protected void load() {
        
    }
    
    protected void unload() {
        backgroundTexture = null;
        ads = null;
    }
    
    protected void update(){
        if (currentScreen == SCREEN_SPLASH)
            showWaitingText = !showWaitingText;
        else if (currentScreen != SCREEN_SPONSOR)
            nextScreen();
        
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
            if (currentScreen == SCREEN_SPONSOR) {
                g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
                g.setColor(255, 255, 255);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.drawString("Tap on a banner and all avertisments", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 38, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString("will be disabled for next 24 hours!", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 24, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString("Or click anywhere to skip.", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 10, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString("fetching banner...", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.BASELINE);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
                g.drawString("Sponsor Page", Main.SCREEN_WIDTH / 2, 24, Graphics.HCENTER | Graphics.BASELINE);
            }
            return;
        }
        
        if (currentScreen == SCREEN_SPONSOR) {
            // ads page
            g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
            g.setColor(255, 255, 255);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Tap on a banner and all avertisments", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 38, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("will be disabled for next 24 hours!", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 24, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("Or click anywhere to skip.", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 10, Graphics.HCENTER | Graphics.BASELINE);
            if (ads != null) {
                g.drawImage((Image)ads.elementAt(0), Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
            } else {
                g.drawString("Connection failed!", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2 - 10, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString("Please wait for " + waiting + " seconds...", Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2 + 10, Graphics.HCENTER | Graphics.BASELINE);
            }
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
            g.drawString("Sponsor Page", Main.SCREEN_WIDTH / 2, 24, Graphics.HCENTER | Graphics.BASELINE);
        } else {
            // intro page
            g.drawImage(backgroundTexture, 0, 0, Graphics.TOP | Graphics.LEFT);
            if (currentScreen == SCREEN_SPLASH && showWaitingText) {
                g.setColor(0, 0, 0);
                g.drawString(TEXT_WAITING, Main.SCREEN_WIDTH / 2 + 2, Main.SCREEN_HEIGHT - 16 + 2, Graphics.HCENTER | Graphics.BASELINE);
                g.setColor(255, 255, 255);
                g.drawString(TEXT_WAITING, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT - 16, Graphics.HCENTER | Graphics.BASELINE);
            }
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if (currentScreen == SCREEN_SPONSOR && ads != null) {
            int imgX1 = (Main.SCREEN_WIDTH - ((Image)ads.elementAt(0)).getWidth()) / 2;
            int imgY1 = (Main.SCREEN_HEIGHT - ((Image)ads.elementAt(0)).getHeight()) / 2;
            int imgX2 = Main.SCREEN_WIDTH - imgX1;
            int imgY2 = Main.SCREEN_HEIGHT - imgY1;
            if (x > imgX1 && x < imgX2 && y > imgY1 && y < imgY2) {
                // click on ads
                try {
                    parent.bannerPressed();
                    parent.platformRequest((String)ads.elementAt(1));
                }
                catch (ConnectionNotFoundException ex) { }
            }
            nextScreen();
        }
        else if (currentScreen == SCREEN_SPLASH) {
            parent.gotoMainMenu();
        }
    }
    
    private void nextScreen() {
        isLoading = true;
        currentScreen++;
        switch (currentScreen) {
            case SCREEN_OPENITVN:
                backgroundTexture = ImageHelper.loadImage("/images/logoopenitvn.png");
                break;
                
            case SCREEN_SPONSOR:
                if (parent.displayAds) {
                    backgroundTexture = null;
                    ads = IADView.getBannerAdData(parent, Main.NAX_CODE);
                    if (ads == null) {
                        waiting = 2;
                        play(1000);
                    }
                }
                else {
                    nextScreen();
                }
                break;
                
            case SCREEN_SPLASH:
                ads = null;
                backgroundTexture = ImageHelper.loadImage("/images/splash.png");
                play(500);
                break;
        }
        isLoading = false;
    }
}

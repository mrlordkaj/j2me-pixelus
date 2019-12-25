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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class HelpScene extends GameScene {
    
    private boolean touching = false;
    private Image backgroundImage, titleImage;
    private String[] helpContent = new String[] {
        "PIXELUS: MOBILE EDITION",
        "Version: 1.0",
        "Developed by Openitvn Forum",
        "Based on the same game of Nuclide.com",
        "",
        "==================",
        "OBJECTIVE",
        "The objective of Pixelus is to complete the mosaic puzzles in the",
        "shortest number of moves possible. The 'World Record' in the",
        "right side bar indicates the least number of moves required",
        "to complete the current puzzle.",
        "",
        "==================",
        "CONTROLS",
        "Place your cursor on the position where you want to place a tile.",
        "Tap on the ok button to place the tile. To remove a tile you have",
        "already placed, position the cursor on top of the tile you wish to",
        "remove, and tap on ok button. The tile can only be removed if it",
        "is not blocked by another tile.",
        "",
        "==================",
        "CREDITS",
        "Gameplay & Art: Nuclide.com",
        "Programming: Thinh Pham",
        "",
        "Pixelus is a trademark of Nuclide.com BVBA.",
        "Copyright © 2004 Nuclide. All rights reserved.",
        "",
        "Pixelus Mobile is a non-commercial product which is",
        "developed by members of Openitvn under limited license",
        "agreement of Nuclide. It's free, don't pay anything for!",
        "",
        "The Openitvn logo is a trademark of Openitvn in the Vietnam.",
        "Copyright © 2013 Openitvn Forum. All rights reserved.",
        "",
        "Website: http://openitvn.net",
        "Support: mrlordkaj@gmail.com"
    };
    private int marginTop = Main.SCREEN_HEIGHT + 20;
    private final Main parent;
    
    public HelpScene(Main parent) {
        super();
        this.parent = parent;
        prepareResource();
        start(80);
    }
    
    private void prepareResource() {
        backgroundImage = ImageHelper.loadImage("/images/storybackground.png");
        titleImage = ImageHelper.loadImage("/images/helptitle.png");
        isLoading = false;
    }
    
//#if ScreenWidth == 400
//#     private static final int MARGIN_TOP_MIN = 80;
//#elif ScreenWidth == 320
    private static final int MARGIN_TOP_MIN = 70;
//#endif
    
    protected void update() {
        if (!isLoading) {
            if (marginTop > MARGIN_TOP_MIN - helpContent.length * 20) {
                marginTop -= touching ? 4 : 1;
            }
            else {
                marginTop = Main.SCREEN_HEIGHT + 20;
            }
        }
    }
    
    public void paint(Graphics g) {
        if (!isLoading) {
            g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            for (int i = 0; i < helpContent.length; i++) {
                g.drawString(helpContent[i], Main.SCREEN_WIDTH / 2, marginTop + i*20, Graphics.HCENTER | Graphics.BASELINE);
            }
            g.drawImage(titleImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if (x > 0 && x < 80 && y > 0 && y < 60) {
            parent.gotoMainMenu();
        }
        else {
            touching = true;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        touching = false;
    }
    
    public void dispose() {
        isLoading = true;
        isPlaying = false;
        backgroundImage = null;
        titleImage = null;
        helpContent = null;
    }
}

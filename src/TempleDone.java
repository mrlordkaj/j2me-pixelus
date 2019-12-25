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
public class TempleDone extends StoryPage {
    
    private Image backgroundImage, frontgroundImage;
    private boolean clickToReturn = true;
    private int textY = 210;
    private String[] text;
    private final TempleScene parent;
    private final int templeId;
    
    public TempleDone(int templeId, TempleScene parent) {
        this.parent = parent;
        this.templeId = templeId;
        if (templeId == TempleScene.TEMPLE_CYLOP) {
            text = new String[] {
                "Congratulations!",
                "You have done well",
                "to learn the basics",
                "before starting out",
                "on your adventure.",
                "",
                "NOW PICK A TEMPLE",
                "ON THE ISLAND TO",
                "TEST YOUR SKILL!"
            };
        } else {
            if (templeId == TempleScene.TEMPLE_JUPITER)
                clickToReturn = false;
            text = new String[] {
                "The template of",
                Story.CHARACTER_NAMES[templeId].toUpperCase(),
                "holds no more",
                "secrets for you!",
                "Your victory is",
                "glorious, but many",
                "challenges still",
                "lie ahead.",
                "",
                "GO NOW,",
                "AND CONQUER",
                "THE NEXT TEMPLE!"
            };
        }
        backgroundImage = ImageHelper.loadImage("/images/happyendingbg.png");
        frontgroundImage = ImageHelper.loadImage("/images/happyendingfg.png");
    }
    
//#if ScreenWidth == 400
//#     private static final int CYLOP_TEXT_TOP = 30;
//#     private static final int TEMPLE_TEXT_TOP = -30;
//#elif ScreenWidth == 320
    private static final int CYLOP_TEXT_TOP = 40;
    private static final int TEMPLE_TEXT_TOP = -20;
//#endif
    
    public void update() {
        if (templeId == TempleScene.TEMPLE_CYLOP) {
            if (textY > CYLOP_TEXT_TOP)
                textY -= 1;
        } else {
            if (textY > TEMPLE_TEXT_TOP)
                textY -= 1;
        }
    }
    
    public void paint(Graphics g) {
//#if ScreenWidth == 400
//#         if (backgroundImage != null)
//#             g.drawImage(backgroundImage, 234, 132, Graphics.LEFT | Graphics.TOP);
//#         if (templeId != TempleScene.TEMPLE_JUPITER || !clickToReturn) {
//#             g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
//#             for (byte i = 0; i < text.length; i++) {
//#                 g.drawString(text[i], 298, textY + 20*i, Graphics.HCENTER | Graphics.BASELINE);
//#             }
//#             g.drawImage(frontgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
//#         }
//#elif ScreenWidth == 320
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 160, 142, Graphics.LEFT | Graphics.TOP);
        if (templeId != TempleScene.TEMPLE_JUPITER || !clickToReturn) {
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            for (byte i = 0; i < text.length; i++) {
                g.drawString(text[i], 230, textY + 20*i, Graphics.HCENTER | Graphics.BASELINE);
            }
            g.drawImage(frontgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        }
//#endif
    }
    
    public void pointerPressed(int x, int y) {
        if (templeId == TempleScene.TEMPLE_CYLOP) {
            if (textY == CYLOP_TEXT_TOP)
                parent.closeStory();
        } else {
            if (clickToReturn) {
                if (textY == TEMPLE_TEXT_TOP)
                    parent.closeStory();
            } else {
                backgroundImage = ImageHelper.loadImage("/images/gamedone.png");
                clickToReturn = true;
            }
        }
    }
    
    public void dispose() {
        backgroundImage = null;
        frontgroundImage = null;
        text = null;
    }
}

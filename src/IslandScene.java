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
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.ImageHelper;
import util.StringHelper;

/**
 *
 * @author Thinh Pham
 */
class IslandScene extends GameScene implements StoryPlayer {
    
//#if ScreenWidth == 400
//#     private static final int[][] TEMPLE_RECTANGLE = new int[][] {
//#         { 189, 144, 54, 34 },   // Cylop
//#         { 197, 177, 70, 46 },   // Flora
//#         { 141, 78, 78, 66 },    // Cupid
//#         { 347, 94, 62, 46 },    // Neptune
//#         { 337, 163, 50, 48},    // Venus
//#         { 230, 67, 42, 40 },    // Bacchus
//#         { 298, 25, 46, 46 },    // Vulcan
//#         { 246, 113, 46, 60 },   // Diana
//#         { 97, 37, 36, 40 },     // Proserpina
//#         { 192, 12, 72, 64 }     // Jupiter
//#     };
//#elif ScreenWidth == 320
    public static final int[][] TEMPLE_RECTANGLE = new int[][] {
        { 150, 138, 50, 34 },   // Cylop
        { 160, 170, 54, 40 },   // Flora
        { 107, 86, 70, 48 },    // Cupid
        { 271, 94, 62, 46 },    // Neptune
        { 266, 158, 50, 48 },   // Venus
        { 186, 76, 34, 38 },    // Bacchus
        { 235, 38, 46, 40 },    // Vulcan
        { 197, 118, 38, 44 },   // Diana
        { 75, 44, 36, 40 },     // Proserpina
        { 151, 26, 68, 56 }     // Jupiter
    };
//#endif
    
    private Image islandImage, lightingImage, templeArrowImage;
    private int[] lightingPosition = new int[2];
    
    // temple management
    private int[] templeSolvedPuzzle, templePerfectPuzzle, templeTotalPuzzle;
    private int perfectWidth, solvedWidth;
    private boolean[] templeCompleted;
    
    private int selectedTemple = -1, totalSolvedPuzzle = 0, templeArrowMargin = 6;
    private int totalOpenedTemple, newTemple;
    private boolean templeArrowClosing = true;
    private Sprite starSprite;
    private Story story;
    
    public IslandScene() {
        super();
        lazyLoad();
        play(100);
    }
    
    protected void load() {
        // load record store
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            totalOpenedTemple = Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_OPENEDTEMPLE)));
            templeTotalPuzzle = new int[totalOpenedTemple];
            templeSolvedPuzzle = new int[totalOpenedTemple];
            templePerfectPuzzle = new int[totalOpenedTemple];
            templeCompleted = new boolean[totalOpenedTemple];
            for (byte i = 0; i < totalOpenedTemple; i++) { // 0 - 9
                templeTotalPuzzle[i] = Puzzle.PUZZLE_FIRSTID[i + 1] - Puzzle.PUZZLE_FIRSTID[i];
                String[] templeData = StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC + i)), "#");
                templeSolvedPuzzle[i] = Integer.parseInt(templeData[0]);
                templePerfectPuzzle[i] = Integer.parseInt(templeData[1]);
                templeCompleted[i] = (templeSolvedPuzzle[i] == templeTotalPuzzle[i]);
                if (i > 0)
                    totalSolvedPuzzle += templeSolvedPuzzle[i];
            }
            newTemple = Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_NEWTEMPLE)));
        } catch (RecordStoreException ex) { }
        // prepare island image
        islandImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        Graphics g = islandImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/islandmap.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        ImageHelper.MEDAL_SPRITE.setFrame(0);
        for (int i = 0; i < totalOpenedTemple; i++) {
            if (templeCompleted[i]) {
                // draw lighted temple
                g.drawImage(ImageHelper.loadImage("/images/map" + Story.CHARACTER_NAMES[i].toLowerCase() + "b.png"), IslandScene.TEMPLE_RECTANGLE[i][0], IslandScene.TEMPLE_RECTANGLE[i][1], Graphics.LEFT | Graphics.TOP);
                // draw medal to perfect
                if (templePerfectPuzzle[i] == templeTotalPuzzle[i]) {
                    // if temple is perfect
                    ImageHelper.MEDAL_SPRITE.setPosition(IslandScene.TEMPLE_RECTANGLE[i][0] + IslandScene.TEMPLE_RECTANGLE[i][2] / 2, IslandScene.TEMPLE_RECTANGLE[i][1] + IslandScene.TEMPLE_RECTANGLE[i][3] - 12);
                    ImageHelper.MEDAL_SPRITE.paint(g);
                }
            }
            else if (i != 0) {
                // draw normal temple
                g.drawImage(ImageHelper.loadImage("/images/map" + Story.CHARACTER_NAMES[i].toLowerCase() + "a.png"), IslandScene.TEMPLE_RECTANGLE[i][0], IslandScene.TEMPLE_RECTANGLE[i][1], Graphics.LEFT | Graphics.TOP);
            }
            System.gc();
        }
        // prepare other images
        lightingImage = ImageHelper.loadImage("/images/lighting.png");
        templeArrowImage = ImageHelper.loadImage("/images/templearrow.png");
        starSprite = new Sprite(ImageHelper.loadImage("/images/star.png"), 100, 16);
        starSprite.setPosition(25, 168);
        if (newTemple == 0)
            story = Story.getStory(Story.STORY_CYLOP_CYLOP, this);
    }
    
    protected void unload() {
        islandImage = null;
        lightingImage = null;
        lightingPosition = null;
        templeArrowImage = null;
    }
    
    protected void update() {
        if (!isLoading) {
            if (story != null) {
                story.update();
            } else if (newTemple > -1) {
                if (templeArrowClosing) {
                    templeArrowMargin -= 2;
                    if (templeArrowMargin < -2)
                        templeArrowClosing = false;
                } else {
                    templeArrowMargin += 2;
                    if (templeArrowMargin > 4)
                        templeArrowClosing = true;
                }
            }
        }
    }
    
    public void paint(Graphics g) {
        if (!repaintLoading(g)) {
            if (story != null) {
                story.paint(g);
            } else {
                // draw whole island
                g.drawImage(islandImage, 0, 0, Graphics.LEFT | Graphics.TOP);

                // draw current temple arrow indicator
                if (newTemple > -1) {
                    g.drawImage(templeArrowImage, TEMPLE_RECTANGLE[newTemple][0] + templeArrowMargin, TEMPLE_RECTANGLE[newTemple][1] + templeArrowMargin, Graphics.RIGHT | Graphics.BOTTOM);
                }
                // draw selected temple
//#if ScreenWidth == 400
//#                 if (selectedTemple > -1) {
//#                     g.drawImage(lightingImage, lightingPosition[0], lightingPosition[1], Graphics.HCENTER | Graphics.VCENTER);
//#                     g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#                     g.drawString("Temple of", 72, 140, Graphics.HCENTER | Graphics.BASELINE);
//#                     if (selectedTemple == TempleScene.TEMPLE_CYLOP) {
//#                         g.drawString("Learn how to play", 72, 186, Graphics.HCENTER | Graphics.BASELINE);
//#                     } else {
//#                         starSprite.setFrame(selectedTemple - 1);
//#                         starSprite.paint(g);
//#                         g.setColor(0xd9802a);
//#                         g.fillRect(22, 189, solvedWidth, 18);
//#                         g.setColor(0x3e8bfd);
//#                         g.fillRect(22, 189, perfectWidth, 18);
//#                         g.setColor(0x000000);
//#                         g.drawRect(22, 189, 100, 18);
//#                         g.drawString(templePerfectPuzzle[selectedTemple] + "/" + templeSolvedPuzzle[selectedTemple] + "/" + templeTotalPuzzle[selectedTemple], 72, 202, Graphics.HCENTER | Graphics.BASELINE);
//#                     }
//#                     g.setColor(255, 0, 0);
//#                     g.drawString("TAP HERE TO VISIT", 72, 222, Graphics.HCENTER | Graphics.BASELINE);
//#                     g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
//#                     g.drawString(Story.CHARACTER_NAMES[selectedTemple].toUpperCase(), 72, 160, Graphics.HCENTER | Graphics.BASELINE);
//#                 } else {
//#                     g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#                     g.drawString("Map of", 72, 140, Graphics.HCENTER | Graphics.BASELINE);
//#                     g.drawString("Tap on a temple", 72, 186, Graphics.HCENTER | Graphics.BASELINE);
//#                     g.drawString("Solved " + totalSolvedPuzzle + "/201", 72, 222, Graphics.HCENTER | Graphics.BASELINE);
//#                     g.setColor(255, 0, 0);
//#                     g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
//#                     g.drawString("PIXELUS", 72, 160, Graphics.HCENTER | Graphics.BASELINE);
//#                 }
//#elif ScreenWidth == 320
                if (selectedTemple > -1) {
                    g.drawImage(lightingImage, lightingPosition[0], lightingPosition[1], Graphics.HCENTER | Graphics.VCENTER);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                    g.drawString("Temple of", 68, 140, Graphics.HCENTER | Graphics.BASELINE);
                    if (selectedTemple == TempleScene.TEMPLE_CYLOP) {
                        g.drawString("Learn how to play", 68, 186, Graphics.HCENTER | Graphics.BASELINE);
                    } else {
                        starSprite.setFrame(selectedTemple - 1);
                        starSprite.paint(g);
                        g.setColor(0xd9802a);
                        g.fillRect(18, 189, solvedWidth, 18);
                        g.setColor(0x3e8bfd);
                        g.fillRect(18, 189, perfectWidth, 18);
                        g.setColor(0x000000);
                        g.drawRect(18, 189, 100, 18);
                        g.drawString(templePerfectPuzzle[selectedTemple] + "/" + templeSolvedPuzzle[selectedTemple] + "/" + templeTotalPuzzle[selectedTemple], 68, 202, Graphics.HCENTER | Graphics.BASELINE);
                    }
                    g.setColor(255, 0, 0);
                    g.drawString("TAP TO VISIT", 68, 222, Graphics.HCENTER | Graphics.BASELINE);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                    g.drawString(Story.CHARACTER_NAMES[selectedTemple].toUpperCase(), 68, 160, Graphics.HCENTER | Graphics.BASELINE);
                } else {
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                    g.drawString("Map of", 68, 140, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString("Tap on a temple", 68, 186, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString("Solved " + totalSolvedPuzzle + "/201", 68, 222, Graphics.HCENTER | Graphics.BASELINE);
                    g.setColor(255, 0, 0);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                    g.drawString("PIXELUS", 68, 160, Graphics.HCENTER | Graphics.BASELINE);
                }
//#endif
            }
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if (isLoading) return;
        
        if (story != null) {
            story.pointerPressed(x, y);
        } else {
            if (x > 0 && x < 80 && y > 0 && y < 60) {
                Main.getInstance().gotoMainMenu();
                return;
            }
            
            if (selectedTemple != -1 && x > 0 && x < 130 && y > 130 && y < Main.SCREEN_HEIGHT) {
                // uncheck new opened temple
                if(selectedTemple == newTemple) {
                    try {
                        RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                        byte[] writer = "-1".getBytes();
                        rs.setRecord(Main.RMS_USER_NEWTEMPLE, writer, 0, writer.length);
                        rs.closeRecordStore();
                    } catch (RecordStoreException ex) {}
                }
                // switch to temple screen
                Main.getInstance().gotoTemple(selectedTemple, false);
                return;
            }

            for (int i = 0; i < totalOpenedTemple; i++) {
                if (x > TEMPLE_RECTANGLE[i][0] && x < TEMPLE_RECTANGLE[i][0] + TEMPLE_RECTANGLE[i][2]) {
                    if (y > TEMPLE_RECTANGLE[i][1] && y < TEMPLE_RECTANGLE[i][1] + TEMPLE_RECTANGLE[i][3]) {
                        lightingPosition[0] = TEMPLE_RECTANGLE[i][0] + TEMPLE_RECTANGLE[i][2]/2;
                        lightingPosition[1] = TEMPLE_RECTANGLE[i][1] + TEMPLE_RECTANGLE[i][3]/2;
                        selectedTemple = i;
                        perfectWidth = (templePerfectPuzzle[i] * 100) / templeTotalPuzzle[i];
                        solvedWidth = (templeSolvedPuzzle[i] * 100) / templeTotalPuzzle[i];
                        return;
                    }
                }
            }
            
            perfectWidth = 0;
            solvedWidth = 0;
            selectedTemple = -1;
        }
    }
    
    
    
    public void closeStory() {
        story.dispose();
        story = null;
    }
    
    public String getPlayerName() { return Main.getInstance().playerName; }
}

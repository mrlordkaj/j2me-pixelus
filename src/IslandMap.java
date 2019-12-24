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
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.StringHelper;

/**
 *
 * @author Thinh Pham
 */
public class IslandMap extends GamePage implements StoryPlayer {
    public static final int[][] templeRectangle = new int[][] {
        {189, 144, 54, 34}, //Cylop
        {197, 177, 70, 46}, //Flora
        {141, 78, 78, 66}, //Cupid
        {347, 94, 62, 46}, //Neptune
        {337, 163, 50, 48}, //Venus
        {230, 67, 42, 40}, //Bacchus
        {298, 25, 46, 46}, //Vulcan
        {246, 113, 46, 60}, //Diana
        {97, 37, 36, 40}, //Proserpina
        {192, 12, 72, 64} //Jupiter
    };
    
    public Image islandImage, lightingImage, templeArrowImage;
    private int[] lightingPosition = new int[2];
    
    //các biến mảng quản lý tất cả temple
    private int[] templeSolvedPuzzle, templePerfectPuzzle, templeTotalPuzzle;
    private int perfectWidth, solvedWidth;
    private boolean[] templeCompleted;
    
    private int selectedTemple = -1, totalSolvedPuzzle = 0, templeArrowMargin = 6;
    public int totalOpenedTemple, newTemple;
    private boolean templeArrowClosing = true;
    public Sprite starSprite;
    public Story story;
    
    private Main parent;
    
    public boolean getTempleCompleted(int i) { return templeCompleted[i]; }
    public boolean templeIsPerfect(int i) { return templePerfectPuzzle[i] == templeTotalPuzzle[i]; }
    
    public IslandMap(Main _parent) {
        super();
        parent = _parent;
        
        prepareResource();
        
        schedule = 100;
        new Thread(this).start();
    }
    
    private void prepareResource() {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            totalOpenedTemple = Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_OPENEDTEMPLE)));
            templeTotalPuzzle = new int[totalOpenedTemple];
            templeSolvedPuzzle = new int[totalOpenedTemple];
            templePerfectPuzzle = new int[totalOpenedTemple];
            templeCompleted = new boolean[totalOpenedTemple];
            for(byte i = 0; i < totalOpenedTemple; i++) { //0 - 9
                templeTotalPuzzle[i] = Puzzle.PUZZLE_FIRSTID[i + 1] - Puzzle.PUZZLE_FIRSTID[i];
                String[] templeData = StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC + i)), "#");
                templeSolvedPuzzle[i] = Integer.parseInt(templeData[0]);
                templePerfectPuzzle[i] = Integer.parseInt(templeData[1]);
                templeCompleted[i] = (templeSolvedPuzzle[i] == templeTotalPuzzle[i]);
                if(i > 0) totalSolvedPuzzle += templeSolvedPuzzle[i];
            }
            newTemple = Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_NEWTEMPLE)));
        } catch (RecordStoreException ex) {}
        
        new Loader(this).start();
    }
    
    protected void update() {
        if(isLoading) return;
        
        if(story != null) {
            story.update();
        } else if(newTemple > -1) {
            if(templeArrowClosing) {
                templeArrowMargin -= 2;
                if(templeArrowMargin < -2) templeArrowClosing = false;
            } else {
                templeArrowMargin += 2;
                if(templeArrowMargin > 4) templeArrowClosing = true;
            }
        }
    }
    
    public void paint(Graphics g) {
        g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        
        if(isLoading) {
            Main.loadingSprite.nextFrame();
            Main.loadingSprite.paint(g);
            g.setColor(255, 255, 255);
            g.drawString("loading...", 184, Main.SCREENSIZE_HEIGHT / 2 + 20, Graphics.LEFT | Graphics.BASELINE);
        } else {
            if(story != null) {
                story.paint(g);
            } else {
                //vẽ toàn bộ đảo
                g.drawImage(islandImage, 0, 0, Graphics.LEFT | Graphics.TOP);

                //vẽ mũi tên chỉ ngôi đền mới mở
                if(newTemple > -1) {
                    g.drawImage(templeArrowImage, templeRectangle[newTemple][0] + templeArrowMargin, templeRectangle[newTemple][1] + templeArrowMargin, Graphics.RIGHT | Graphics.BOTTOM);
                }

                //đền được chọn
                if(selectedTemple > -1) {
                    g.drawImage(lightingImage, lightingPosition[0], lightingPosition[1], Graphics.HCENTER | Graphics.VCENTER);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                    g.drawString("Temple of", 72, 140, Graphics.HCENTER | Graphics.BASELINE);
                    if(selectedTemple == Temple.TEMPLE_CYLOP) {
                        g.drawString("Learn how to play", 72, 186, Graphics.HCENTER | Graphics.BASELINE);
                    } else {
                        starSprite.setFrame(selectedTemple - 1);
                        starSprite.paint(g);
                        g.setColor(0xd9802a);
                        g.fillRect(22, 189, solvedWidth, 18);
                        g.setColor(0x3e8bfd);
                        g.fillRect(22, 189, perfectWidth, 18);
                        g.setColor(0x000000);
                        g.drawRect(22, 189, 100, 18);
                        g.drawString(templePerfectPuzzle[selectedTemple] + "/" + templeSolvedPuzzle[selectedTemple] + "/" + templeTotalPuzzle[selectedTemple], 72, 202, Graphics.HCENTER | Graphics.BASELINE);
                    }
                    g.setColor(255, 0, 0);
                    g.drawString("TAP HERE TO VISIT", 72, 222, Graphics.HCENTER | Graphics.BASELINE);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                    g.drawString(Story.characterName[selectedTemple].toUpperCase(), 72, 160, Graphics.HCENTER | Graphics.BASELINE);
                } else {
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                    g.drawString("Map of", 72, 140, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString("Tap on a temple", 72, 186, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString("Solved " + totalSolvedPuzzle + "/201", 72, 222, Graphics.HCENTER | Graphics.BASELINE);
                    g.setColor(255, 0, 0);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                    g.drawString("PIXELUS", 72, 160, Graphics.HCENTER | Graphics.BASELINE);
                }
            }
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(isLoading) return;
        
        if(story != null) {
            story.pointerPressed(x, y);
        } else {
            if(x > 0 && x < 80 && y > 0 && y < 60) {
                parent.gotoMainMenu();
                return;
            }
            
            if(selectedTemple != -1 && x > 0 && x < 130 && y > 130 && y < Main.SCREENSIZE_HEIGHT) {
                //bỏ đánh dấu temple mới mở
                if(selectedTemple == newTemple) {
                    try {
                        RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                        byte[] writer = "-1".getBytes();
                        rs.setRecord(Main.RMS_USER_NEWTEMPLE, writer, 0, writer.length);
                        rs.closeRecordStore();
                    } catch (RecordStoreException ex) {}
                }
                //chuyển sang trang temple
                parent.gotoTemple(selectedTemple, false);
                return;
            }

            for(int i = 0; i < totalOpenedTemple; i++) {
                if(x > templeRectangle[i][0] && x < templeRectangle[i][0] + templeRectangle[i][2]) {
                    if(y > templeRectangle[i][1] && y < templeRectangle[i][1] + templeRectangle[i][3]) {
                        lightingPosition[0] = templeRectangle[i][0] + templeRectangle[i][2]/2;
                        lightingPosition[1] = templeRectangle[i][1] + templeRectangle[i][3]/2;
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
    
    public void dispose() {
        isLoading = true;
        pageLooping = false;
        islandImage = null;
        lightingImage = null;
        lightingPosition = null;
        templeArrowImage = null;
    }
    
    public void closeStory() {
        story.dispose();
        story = null;
    }
    
    public String getPlayerName() { return parent.playerName; }
}

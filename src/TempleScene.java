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
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.Button;
import util.FileHelper;
import util.StringHelper;

/**
 *
 * @author Thinh Pham
 */
public class TempleScene extends GameScene implements StoryPlayer {
    
    static final int TEMPLE_NONE = -1;
    static final int TEMPLE_CYLOP = 0;
    static final int TEMPLE_FLORA = 1;
    static final int TEMPLE_CUPID = 2;
    static final int TEMPLE_NEPTUNE = 3;
    static final int TEMPLE_VENUS = 4;
    static final int TEMPLE_BACCHUS = 5;
    static final int TEMPLE_VULCAN = 6;
    static final int TEMPLE_DIANA = 7;
    static final int TEMPLE_PROSERPINA = 8;
    static final int TEMPLE_JUPITER = 9;
    static final short[] TEMPLE_REQUIRE = new short[] { 194, 0, 9, 20, 34, 50, 72, 100, 128, 160 };
    
    private Image puzzleViewImage, scrollerImage, backgroundImage, buttonImage, selectedPuzzleImage;
    private Image messageDialogImage, confirmDialogImage;
    private StoryPage story;
    
    // tutorial only
    private boolean firstTimeToPlay;
    
    // not for tutorial
    private boolean lastPuzzleUnlocked;
    private String puzzleLeft;
    
    // user control panel
    private final int templeId;
    private int activePuzzle = 0;
    private int solvedPuzzle, perfectPuzzle, notifyStatus;
    private int[] puzzleViewRectangle;
    
    // scrollbar
    private int marginTop, prevY, marginTopStep = 0, minMarginTop, maxMarginTop, puzzleViewHeight;
    private boolean dragging = false, touching = false;
    private float scrollerStep;
    
    private int[] bestAmountTurn, bestAmountTime, medal;
    
//#if ScreenWidth == 400
//#     private static final int PUZZLE_IMAGE_SIZE = 68;
//#     private static final int IMAGE_PIXEL_SIZE = 4;
//#     
//#     private final Button btnPlayTutorial = new Button(35, 55, 70, 30);
//#     private final Button btnPlayNormal = new Button(19, 55, 70, 30);
//#elif ScreenWidth == 320
    private static final int PUZZLE_IMAGE_SIZE = 53;
    private static final int IMAGE_PIXEL_SIZE = 3;
    
    private final Button btnPlayTutorial = new Button(23, 55, 70, 30);
    private final Button btnPlayNormal = new Button(7, 55, 70, 30);
//#endif
    
    private final Button btnBack = new Button(0, 0, 80, 50);
    
    public TempleScene(int templeId) {
        this(templeId, 0);
    }
    
    public TempleScene(int templeId, int marginTop) {
        super();
        this.templeId = templeId;
        this.marginTop = marginTop;
        lazyLoad();
        play(100);
    }
    
    protected void load() {
        puzzleViewHeight = ((Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId]) / 3) * PUZZLE_IMAGE_SIZE - 4;
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            String[] templeData = StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId)), "#");
            solvedPuzzle = Integer.parseInt(templeData[0]);
            perfectPuzzle = Integer.parseInt(templeData[1]);
            notifyStatus = Integer.parseInt(templeData[2]);
            int totalPuzzle = totalPuzzle(templeId);
            int unsolvedPuzzle = totalPuzzle - solvedPuzzle;
            if (templeId != TEMPLE_CYLOP) {
                bestAmountTurn = new int[totalPuzzle];
                bestAmountTime = new int[totalPuzzle];
                medal = new int[totalPuzzle];
                for (int i = 0; i < totalPuzzle; i++) {
                    String[] puzzleData = StringHelper.split(new String(rs.getRecord(Puzzle.PUZZLE_FIRSTID[templeId] + i - 9)), "#");
                    bestAmountTurn[i] = Integer.parseInt(puzzleData[0]);
                    bestAmountTime[i] = Integer.parseInt(puzzleData[1]);
                    medal[i] = Integer.parseInt(puzzleData[2]);
                }
            }
            puzzleLeft = unsolvedPuzzle + " puzzles left";
            lastPuzzleUnlocked = (unsolvedPuzzle < 2);
            firstTimeToPlay = (Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_OPENEDTEMPLE))) < 2);
            rs.closeRecordStore();
        } catch (RecordStoreException ex) { }
        
//#if ScreenWidth == 400
//#         if (templeId == TEMPLE_CYLOP) {
//#             puzzleViewRectangle = new int[] { 140, 10, 200, 200 };
//#             minMarginTop = puzzleViewRectangle[1];
//#         }
//#         else {
//#             puzzleViewRectangle = new int[] { 142, 46, 200, 166 };
//#             minMarginTop = puzzleViewRectangle[1] + puzzleViewRectangle[3] - puzzleViewHeight + 1;
//#         }
//#         scrollerStep = 100f / (float)(puzzleViewHeight - 166);
//#elif ScreenWidth == 320
        puzzleViewRectangle = new int[] { 115, 47, 155, 155 };
        minMarginTop = (templeId == TempleScene.TEMPLE_CYLOP) ?
                puzzleViewRectangle[1] :
                puzzleViewRectangle[1] + puzzleViewRectangle[3] - puzzleViewHeight + 1;
        scrollerStep = 100f / (float)(puzzleViewHeight - puzzleViewRectangle[3]);
//#endif
        maxMarginTop = puzzleViewRectangle[1];
        if (marginTop == 0)
            marginTop = puzzleViewRectangle[1];
        
        switch (notifyStatus) {
            case 4: // open new temple
                for (int i = 0; i < TEMPLE_REQUIRE.length; i++) {
                    if (solvedPuzzle == TEMPLE_REQUIRE[i]) {
                        String referCode = templeId + "" + i;
                        openStory(Integer.parseInt(referCode));
                        break;
                    }
                }
                break;
                
            case 3: // perfect complete a temple
                String referCode = templeId + "" + templeId;
                openStory(Integer.parseInt(referCode));
                break;
                
            case 2: // complete a temple
                story = new TempleDone(templeId, this);
                break;
                
            case 1:
                messageDialogImage = GameHelper.messageDialog(new String[] {
                    "The secret puzzle",
                    "in this temple",
                    "has been unlocked!"
                });
                clearNotify();
                break;
        }
        
        Image lockImage = FileHelper.loadImage("/images/lockpuzzleoverlay.png");
        Image pixelMask = GameHelper.createPixelMask(IMAGE_PIXEL_SIZE);
        
        // draw puzzle list
        int numPuzzle = Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId];
        int width = PUZZLE_IMAGE_SIZE * 3 - 4;
        int height = puzzleViewHeight;
        puzzleViewImage = Image.createImage(width, height);
        Graphics g = puzzleViewImage.getGraphics();
        if (templeId == TempleScene.TEMPLE_CYLOP) {
            // if tutorial temple
            g.setColor(0x585866);
            g.fillRect(0, 0, width, height);
            for (int i = 1; i <= numPuzzle; i++) {
                int y = ((i - 1) / 3) * PUZZLE_IMAGE_SIZE;
                int x = ((i - 1) % 3) * PUZZLE_IMAGE_SIZE;
                if (i <= solvedPuzzle)
                    GameHelper.drawPuzzleImage(i, x, y, IMAGE_PIXEL_SIZE, g, pixelMask, Puzzle.MEDAL_NONE);
                else
                    GameHelper.drawPuzzleCover(i, x, y, IMAGE_PIXEL_SIZE, g, pixelMask, Puzzle.MEDAL_NONE);
                if (i > solvedPuzzle + 1) {
                    g.drawImage(lockImage, x, y, Graphics.LEFT | Graphics.TOP);
                }
                System.gc();
            }
        }
        else {
            // if normal temples
            g.setColor(0xdda513);
            g.fillRect(0, 0, width, height);
            for (int i = 0; i < numPuzzle; i++) {
                int y = (i / 3) * PUZZLE_IMAGE_SIZE;
                int x = (i % 3) * PUZZLE_IMAGE_SIZE;
                if (bestAmountTurn[i] > 0)
                    GameHelper.drawPuzzleImage(i + Puzzle.PUZZLE_FIRSTID[templeId], x, y, IMAGE_PIXEL_SIZE, g, pixelMask, medal[i]);
                else
                    GameHelper.drawPuzzleCover(i + Puzzle.PUZZLE_FIRSTID[templeId], x, y, IMAGE_PIXEL_SIZE, g, pixelMask, medal[i]);
                System.gc();
            }
            if (!lastPuzzleUnlocked) {
                g.drawImage(lockImage, width, height, Graphics.RIGHT | Graphics.BOTTOM);
            }
        }
        // remaining temples
        backgroundImage = FileHelper.loadImage("/images/temple" + Story.CHARACTER_NAMES[templeId].toLowerCase() + ".png");
        buttonImage = FileHelper.loadImage("/images/buttongold.png");
        scrollerImage = FileHelper.loadImage("/images/scroller.png");
        // change framerate
        play(40);
    }
    
    protected void unload() {
        if (story != null) {
            story.dispose();
            story = null;
        }
        backgroundImage = null;
    }
    
    private void clearNotify() {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            byte[] data = (solvedPuzzle + "#" + perfectPuzzle + "#0").getBytes();
            rs.setRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId, data, 0, data.length);
            rs.closeRecordStore();
        } catch (RecordStoreException ex) { }
    }
    
    protected void update() {
        if (!isLoading()) {
            if (story != null)
                story.update();
        }
    }
    
    public void paint(Graphics g) {
        if (!repaintLoading(g)) {
//#if ScreenWidth == 400
//#             if (story != null) {
//#                 story.paint(g);
//#             } else {
//#                 g.drawImage(puzzleViewImage, puzzleViewRectangle[0], marginTop, Graphics.LEFT | Graphics.TOP);
//#                 if (activePuzzle > 0) {
//#                     int x = ((activePuzzle - 1) % 3) * 68 + puzzleViewRectangle[0];
//#                     int y = ((activePuzzle - Puzzle.PUZZLE_FIRSTID[templeId]) / 3) * 68 + marginTop;
//#                     g.drawImage(selectedPuzzleImage, x, y, Graphics.LEFT | Graphics.TOP);
//#                     g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
//#                     if (templeId == TEMPLE_CYLOP) {
//#                         g.drawImage(buttonImage, 70, 70, Graphics.HCENTER | Graphics.VCENTER);
//#                         g.drawString("PLAY", 70, 76, Graphics.HCENTER | Graphics.BASELINE);
//#                     } else {
//#                         g.drawImage(buttonImage, 54, 70, Graphics.HCENTER | Graphics.VCENTER);
//#                         g.drawString("PLAY", 54, 76, Graphics.HCENTER | Graphics.BASELINE);
//#                     }
//#                 } else {
//#                     g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
//#                 }
//#                 if (templeId != TEMPLE_CYLOP) {
//#                     g.drawImage(scrollerImage, 344, 45 - (int)((marginTop - 46) * scrollerStep), Graphics.LEFT | Graphics.TOP);
//#                     g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#                     g.drawString(puzzleLeft, 242, 38, Graphics.HCENTER | Graphics.BASELINE);
//#                 }
//#                 if (messageDialogImage != null)
//#                     g.drawImage(messageDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
//#                 else if(confirmDialogImage != null)
//#                     g.drawImage(confirmDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
//#             }
//#         }
//#elif ScreenWidth == 320
            if (story != null) {
                story.paint(g);
            } else {
                g.drawImage(puzzleViewImage, puzzleViewRectangle[0], marginTop, Graphics.LEFT | Graphics.TOP);
                if (activePuzzle > 0) {
                    int x = ((activePuzzle - 1) % 3) * 53 + puzzleViewRectangle[0];
                    int y = ((activePuzzle - Puzzle.PUZZLE_FIRSTID[templeId]) / 3) * 53 + marginTop;
                    g.drawImage(selectedPuzzleImage, x, y, Graphics.LEFT | Graphics.TOP);
                    g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                    if (templeId == TempleScene.TEMPLE_CYLOP) {
                        g.drawImage(buttonImage, 58, 70, Graphics.HCENTER | Graphics.VCENTER);
                        g.drawString("PLAY", 58, 76, Graphics.HCENTER | Graphics.BASELINE);
                    } else {
                        g.drawImage(buttonImage, 42, 70, Graphics.HCENTER | Graphics.VCENTER);
                        g.drawString("PLAY", 42, 76, Graphics.HCENTER | Graphics.BASELINE);
                    }
                } else {
                    g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                }
                if (templeId != TempleScene.TEMPLE_CYLOP) {
                    g.drawImage(scrollerImage, 273, 43 - (int)((marginTop - puzzleViewRectangle[1]) * scrollerStep), Graphics.LEFT | Graphics.TOP);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                    g.drawString(puzzleLeft, 192, 38, Graphics.HCENTER | Graphics.BASELINE);
                }
                if (messageDialogImage != null)
                    g.drawImage(messageDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
                else if (confirmDialogImage != null)
                    g.drawImage(confirmDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
            }
        }
//#endif
    }
    
    protected void pointerPressed(int x, int y) {
        if (story != null) {
            story.pointerPressed(x, y);
        } else if (messageDialogImage == null && confirmDialogImage == null) {
            // play button
            if (activePuzzle > 0) {
                if (templeId == TempleScene.TEMPLE_CYLOP) {
                    if (btnPlayTutorial.contains(x, y))
                        Main.getInstance().gotoPlay(activePuzzle, templeId, marginTop);
                } else {
                    if (btnPlayNormal.contains(x, y))
                        Main.getInstance().gotoPlay(activePuzzle, templeId, marginTop);
                }
            }
            // back button
            if (btnBack.contains(x, y)) {
                if (templeId == TempleScene.TEMPLE_CYLOP && solvedPuzzle < 9) {
                    confirmDialogImage = GameHelper.confirmDialog(new String[] {
                        "Are you sure you",
                        "want to skip the",
                        "tutorial?"
                    });
                } else if (templeId == TempleScene.TEMPLE_CYLOP && firstTimeToPlay) {
                    //parent.openTemple(TEMPLE_FLORA);
                    openStory(Story.STORY_CYLOP_FLORA);
                } else {
                    Main.getInstance().gotoIslandMap();
                }
                return;
            }
            // if inside viewport
            if (x > puzzleViewRectangle[0] && x < puzzleViewRectangle[0] + puzzleViewRectangle[2]) {
                if (y > puzzleViewRectangle[1] && y < puzzleViewRectangle[1] + puzzleViewRectangle[3]) {
                    touching = true;
                    marginTopStep = y - marginTop;
                    prevY = y;
                }
            }
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if (story != null) return;
        
        if (touching && !dragging && Math.abs(prevY - y) > 4)
            dragging = true;
             
        if (dragging) {
            marginTop = y - marginTopStep;
            if (marginTop > maxMarginTop)
                marginTop = maxMarginTop;
            else if (marginTop < minMarginTop)
                marginTop = minMarginTop;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if (story != null) return;
        // ok button on message dialog
        if (messageDialogImage != null) {
//#if ScreenWidth == 400
//#             if (x > 165 && x < 235 && y > 149 && y < 189)
//#                 messageDialogImage = null;
//#elif ScreenWidth == 320
            if (x > 130 && x < 200 && y > 149 && y < 189)
                messageDialogImage = null;
//#endif
        } else if (templeId == TempleScene.TEMPLE_CYLOP && confirmDialogImage != null) {
//#if ScreenWidth == 400
//#             if (x > 128 && x < 194 && y > 154 && y < 182) {
//#                 // yes
//#                 if (firstTimeToPlay) {
//#                     //parent.openTemple(TEMPLE_FLORA);
//#                     openStory(Story.STORY_CYLOP_FLORA);
//#                 } else {
//#                     Main.getInstance().gotoIslandMap();
//#                 }
//#             } else if (x > 210 && x < 276 && y > 154 && y < 182) {
//#                 // no
//#                 confirmDialogImage = null;
//#             }
//#elif ScreenWidth == 320
            if (x > 94 && x < 154 && y > 154 && y < 182) {
                // yes
                if (firstTimeToPlay) {
                    //parent.openTemple(TEMPLE_FLORA);
                    openStory(Story.STORY_CYLOP_FLORA);
                } else {
                    Main.getInstance().gotoIslandMap();
                }
            } else if (x > 170 && x < 240 && y > 154 && y < 182) {
                // no
                confirmDialogImage = null;
            }
//#endif
        } else if (!dragging) {
            // select 1 puzzle
            if (x > puzzleViewRectangle[0] && x < puzzleViewRectangle[0] + puzzleViewRectangle[2]) {
                if (y > puzzleViewRectangle[1] && y < puzzleViewRectangle[1] + puzzleViewRectangle[3]) {
                    int col = (x - puzzleViewRectangle[0]) / PUZZLE_IMAGE_SIZE;
                    int row = (y - marginTop) / PUZZLE_IMAGE_SIZE;
                    activePuzzle = Puzzle.PUZZLE_FIRSTID[templeId] + row * 3 + col;
                    selectPuzzle();
                }
            } else {
                activePuzzle = 0;
            }
        }
        touching = false;
        dragging = false;
    }
    
    private void selectPuzzle() {
        if (templeId == TEMPLE_CYLOP && activePuzzle > solvedPuzzle + 1) {
            messageDialogImage = GameHelper.messageDialog(new String[] {
                "Not yet!",
                "One step at a time,",
                "now!"
            });
            activePuzzle = 0;
        } else if (!lastPuzzleUnlocked && activePuzzle == Puzzle.PUZZLE_FIRSTID[templeId + 1] - 1) {
            messageDialogImage = GameHelper.messageDialog(new String[] {
                "Unlock secret puzzle",
                "by solving all other",
                "puzzles in this",
                "temple!"
            });
            activePuzzle = 0;
        }
        if (activePuzzle > 0) {
//#if ScreenWidth == 400
//#             selectedPuzzleImage = Image.createImage(64, 64);
//#             Graphics g = selectedPuzzleImage.getGraphics();
//#             int _medal = (templeId == TEMPLE_CYLOP) ? Puzzle.MEDAL_NONE : this.medal[activePuzzle - Puzzle.PUZZLE_FIRSTID[templeId]];
//#             GameHelper.drawPuzzleCover(activePuzzle, 0, 0, IMAGE_PIXEL_SIZE, g, GameHelper.createPixelMask(4), _medal);
//#             g.setColor(0xffffffff);
//#             g.drawRect(0, 0, 63, 63);
//#             g.drawRect(1, 1, 61, 61);
//#elif ScreenWidth == 320
            selectedPuzzleImage = Image.createImage(48, 48);
            Graphics g = selectedPuzzleImage.getGraphics();
            int _medal = (templeId == TEMPLE_CYLOP) ? Puzzle.MEDAL_NONE : this.medal[activePuzzle - Puzzle.PUZZLE_FIRSTID[templeId]];
            GameHelper.drawPuzzleCover(activePuzzle, 0, 0, IMAGE_PIXEL_SIZE, g, GameHelper.createPixelMask(3), _medal);
            g.setColor(0xffffffff);
            g.drawRect(0, 0, 47, 47);
            g.drawRect(1, 1, 45, 45);
//#endif
        } else {
            selectedPuzzleImage = null;
        }
    }
    
    private void openStory(int storyId) {
        story = Story.getStory(storyId, this);
        play(100);
    }
    
    public void closeStory() {
        story.dispose();
        story = null;
        play(40);
        clearNotify();
        if (templeId != TEMPLE_CYLOP) {
            if (notifyStatus == 2 && perfectPuzzle == totalPuzzle(templeId)) {
                String referCode = templeId + "" + templeId;
                openStory(Integer.parseInt(referCode));
                notifyStatus = 3;
            }
            if (notifyStatus == 2 || notifyStatus == 3) {
                // perfect complete while unlock new temple
                for (int i = 1; i < TEMPLE_REQUIRE.length; i++) {
                    if (solvedPuzzle == TEMPLE_REQUIRE[i]) {
                        String referCode = templeId + "" + i;
                        openStory(Integer.parseInt(referCode));
                        break;
                    }
                }
                notifyStatus = 4;
            }
        } else if (firstTimeToPlay) {
            if (notifyStatus == 2) {
                openStory(Story.STORY_CYLOP_FLORA);
                notifyStatus = 0;
            } else {
                Main.getInstance().openTemple(TEMPLE_FLORA);
                Main.getInstance().gotoIslandMap();
            }
        }
    }
    
    public static int totalPuzzle(int templeId) {
        return Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId];
    }
}

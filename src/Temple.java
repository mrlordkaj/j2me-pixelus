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
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.ImageHelper;
import util.StringHelper;

/**
 *
 * @author Thinh Pham
 */
public class Temple extends GamePage implements StoryPlayer {
    public static final int TEMPLE_NONE = -1;
    public static final int TEMPLE_CYLOP = 0;
    public static final int TEMPLE_FLORA = 1;
    public static final int TEMPLE_CUPID = 2;
    public static final int TEMPLE_NEPTUNE = 3;
    public static final int TEMPLE_VENUS = 4;
    public static final int TEMPLE_BACCHUS = 5;
    public static final int TEMPLE_VULCAN = 6;
    public static final int TEMPLE_DIANA = 7;
    public static final int TEMPLE_PROSERPINA = 8;
    public static final int TEMPLE_JUPITER = 9;
    public static final short[] TEMPLE_REQUIRE = new short[] {194, 0, 9, 20, 34, 50, 72, 100, 128, 160};
    
    public Image puzzleViewImage, scrollerImage, backgroundImage, buttonImage, selectedPuzzleImage;
    public Image messageDialogImage, confirmDialogImage;
    public StoryPage story;
    
    //dành riêng cho tutorial
    private boolean firstTimeToPlay;
    
    //không dành cho tutorial
    private boolean lastPuzzleUnlocked;
    private String puzzleLeft;
    
    //xử lý điều khiển người dùng
    private int activePuzzle = 0;
    
    private int templeId, solvedPuzzle, perfectPuzzle, notifyStatus;
    private int[] puzzleViewRectangle;
    
    //xử lý thanh cuộn
    private int marginTop = 0, prevY, marginTopStep = 0, minMarginTop, maxMarginTop, puzzleViewHeight;
    private boolean dragging = false, touching = false;
    private float scrollerStep;
    
    private int[] bestAmountTurn, bestAmountTime, medal;
    
    private Main parent;
    
    public int getSolvedPuzzle() { return solvedPuzzle; }
    public boolean lastPuzzleIsUnlocked() { return lastPuzzleUnlocked; }
    public int getTempleId() { return templeId; }
    public int getPuzzleViewHeight() { return puzzleViewHeight; }
    public int bestAmountTurn(int puzzle) { return bestAmountTurn[puzzle]; }
    public int bestAmountTime(int puzzle) { return bestAmountTime[puzzle]; }
    public int medal(int relativePuzzleId) { return medal[relativePuzzleId]; }
    
    public Temple(int _templeId, Main _parent) {
        super();
        parent = _parent;
        templeId = _templeId;
        
        prepareResource();
        
        schedule = 100;
        new Thread(this).start();
    }
    
    public Temple(int _templeId, int _marginTop, Main _parent) {
        super();
        parent = _parent;
        templeId = _templeId;
        marginTop = _marginTop;
        
        prepareResource();
        
        schedule = 100;
        new Thread(this).start();
    }
    
    private void prepareResource() {
        puzzleViewHeight = ((Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId]) / 3) * 68 - 4;
        scrollerStep = 100f / (float)(puzzleViewHeight - 166);
        
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            String[] templeData = StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId)), "#");
            solvedPuzzle = Integer.parseInt(templeData[0]);
            perfectPuzzle = Integer.parseInt(templeData[1]);
            notifyStatus = Integer.parseInt(templeData[2]);
            int totalPuzzle = totalPuzzle(templeId);
            int unsolvedPuzzle = totalPuzzle - solvedPuzzle;
            if(templeId != Temple.TEMPLE_CYLOP) {
                bestAmountTurn = new int[totalPuzzle];
                bestAmountTime = new int[totalPuzzle];
                medal = new int[totalPuzzle];
                for(int i = 0; i < totalPuzzle; i++) {
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
        } catch (RecordStoreException ex) {}
        
        if(templeId == Temple.TEMPLE_CYLOP) {
            puzzleViewRectangle = new int[] {140, 10, 200, 200};
            minMarginTop = puzzleViewRectangle[1];
        } else {
            puzzleViewRectangle = new int[] {142, 46, 200, 166};
            minMarginTop = puzzleViewRectangle[1] + puzzleViewRectangle[3] - puzzleViewHeight + 1;
        }
        maxMarginTop = puzzleViewRectangle[1];
        if(marginTop == 0) marginTop = puzzleViewRectangle[1];
        
        if(notifyStatus == 4) { //mở một temple mới
            for(int i = 0; i < TEMPLE_REQUIRE.length; i++) {
                if(solvedPuzzle == TEMPLE_REQUIRE[i]) {
                    String referCode = templeId + "" + i;
                    openStory(Integer.parseInt(referCode));
                    break;
                }
            }
        } else if(notifyStatus == 3) { //hoàn thành hoàn hảo temple
            String referCode = templeId + "" + templeId;
            openStory(Integer.parseInt(referCode));
        } else if(notifyStatus == 2) { //hoàn thành temple
            story = new TempleDone(templeId, this);
        } else if(notifyStatus == 1) { //thông báo unlock puzzle cuối
            messageDialogImage = Loader.messageDialog(new String[] {
                "The secret puzzle",
                "in this temple",
                "has been unlocked!"
            });
            clearNotify();
        }
        
        new Loader(this).start();
    }
    
    
    
    private void clearNotify() {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            byte[] writer = (solvedPuzzle + "#" + perfectPuzzle + "#0").getBytes();
            rs.setRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId, writer, 0, writer.length);
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {}
    }
    
    protected void update() {
        if(!isLoading) {
            if(story != null) {
                story.update();
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
                g.drawImage(puzzleViewImage, puzzleViewRectangle[0], marginTop, Graphics.LEFT | Graphics.TOP);

                if(activePuzzle > 0) {
                    int x = ((activePuzzle - 1) % 3) * 68 + puzzleViewRectangle[0];
                    int y = ((activePuzzle - Puzzle.PUZZLE_FIRSTID[templeId]) / 3) * 68 + marginTop;
                    g.drawImage(selectedPuzzleImage, x, y, Graphics.LEFT | Graphics.TOP);
                    g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                    if(templeId == Temple.TEMPLE_CYLOP) {
                        g.drawImage(buttonImage, 70, 70, Graphics.HCENTER | Graphics.VCENTER);
                        g.drawString("PLAY", 70, 76, Graphics.HCENTER | Graphics.BASELINE);
                    } else {
                        g.drawImage(buttonImage, 54, 70, Graphics.HCENTER | Graphics.VCENTER);
                        g.drawString("PLAY", 54, 76, Graphics.HCENTER | Graphics.BASELINE);
                    }
                } else {
                    g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                }

                if(templeId != Temple.TEMPLE_CYLOP) {
                    g.drawImage(scrollerImage, 344, 45 - (int)((marginTop - 46) * scrollerStep), Graphics.LEFT | Graphics.TOP);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                    g.drawString(puzzleLeft, 242, 38, Graphics.HCENTER | Graphics.BASELINE);
                }
                
                if(messageDialogImage != null) {
                    g.drawImage(messageDialogImage, Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
                } else if(confirmDialogImage != null) {
                    g.drawImage(confirmDialogImage, Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
                }
            }
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(story != null) {
            story.pointerPressed(x, y);
        } else if(messageDialogImage == null && confirmDialogImage == null) {
            //nút play
            if(activePuzzle > 0) {
                if(templeId == Temple.TEMPLE_CYLOP) {
                    if(x > 35 && x < 105 && y > 55 && y < 85) {
                        parent.gotoPlay(activePuzzle, templeId, marginTop);
                    }
                } else {
                    if(x > 19 && x < 89 && y > 55 && y < 85) {
                        parent.gotoPlay(activePuzzle, templeId, marginTop);
                    }
                }
            }
            //nút back
            if(x > 0 && x < 80 && y > 0 && y < 50) {
                if(templeId == Temple.TEMPLE_CYLOP && solvedPuzzle < 9) {
                    confirmDialogImage = Loader.confirmDialog(new String[] {
                        "Are you sure you",
                        "want to skip the",
                        "tutorial?"
                    });
                } else if(templeId == Temple.TEMPLE_CYLOP && firstTimeToPlay) {
                    //parent.openTemple(TEMPLE_FLORA);
                    openStory(Story.STORY_CYLOP_FLORA);
                } else {
                    parent.gotoIslandMap();
                }
                return;
            }

            //nếu nằm trong viewpot
            if(x > puzzleViewRectangle[0] && x < puzzleViewRectangle[0] + puzzleViewRectangle[2]) {
                if(y > puzzleViewRectangle[1] && y < puzzleViewRectangle[1] + puzzleViewRectangle[3]) {
                    touching = true;
                    marginTopStep = y - marginTop;
                    prevY = y;
                }
            }
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if(story != null) return;
        
        if(touching && !dragging && Math.abs(prevY - y) > 4) dragging = true;
             
        if(dragging) {
            marginTop = y - marginTopStep;
            if(marginTop > maxMarginTop) marginTop = maxMarginTop;
            else if(marginTop < minMarginTop) marginTop = minMarginTop;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(story != null) return;
        
        //nút ok của hộp thoại message
        if(messageDialogImage != null) {
            if(x > 165 && x < 235 && y > 149 && y < 189) {
                messageDialogImage = null;
            }
        } else if(templeId == Temple.TEMPLE_CYLOP && confirmDialogImage != null) {
            if(x > 128 && x < 194 && y > 154 && y < 182) {
                //yes
                if(firstTimeToPlay) {
                    //parent.openTemple(TEMPLE_FLORA);
                    openStory(Story.STORY_CYLOP_FLORA);
                } else {
                    parent.gotoIslandMap();
                }
            } else if(x > 210 && x < 276 && y > 154 && y < 182) {
                //no
                confirmDialogImage = null;
            }
        } else if(!dragging) {
            //chọn 1 puzzle
            if(x > puzzleViewRectangle[0] && x < puzzleViewRectangle[0] + puzzleViewRectangle[2]) {
                if(y > puzzleViewRectangle[1] && y < puzzleViewRectangle[1] + puzzleViewRectangle[3]) {
                    int col = (x - puzzleViewRectangle[0]) / 68;
                    int row = (y - marginTop) / 68;
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
    
    public void dispose() {
        isLoading = true;
        pageLooping = false;
        if(story != null) {
            story.dispose();
            story = null;
        }
        backgroundImage = null;
    }
    
    private void selectPuzzle() {
        if(templeId == TEMPLE_CYLOP && activePuzzle > solvedPuzzle + 1) {
            messageDialogImage = Loader.messageDialog(new String[] {
                "Not yet!",
                "One step at a time,",
                "now!"
            });
            activePuzzle = 0;
        } else if (!lastPuzzleUnlocked && activePuzzle == Puzzle.PUZZLE_FIRSTID[templeId + 1] - 1) {
            messageDialogImage = Loader.messageDialog(new String[] {
                "Unlock secret puzzle",
                "by solving all other",
                "puzzles in this",
                "temple!"
            });
            activePuzzle = 0;
        }
        
        if(activePuzzle > 0) {
            selectedPuzzleImage = Image.createImage(64, 64);
            Graphics g = selectedPuzzleImage.getGraphics();
            int medal = (templeId == TEMPLE_CYLOP) ? Puzzle.MEDAL_NONE : this.medal[activePuzzle - Puzzle.PUZZLE_FIRSTID[templeId]];
            Loader.drawPuzzleCover(activePuzzle, 0, 0, g, ImageHelper.createPixelMask(4), medal);
            g.setColor(0xffffffff);
            g.drawRect(0, 0, 63, 63);
            g.drawRect(1, 1, 61, 61);
        } else {
            selectedPuzzleImage = null;
        }
    }
    
    public String getPlayerName() { return parent.playerName; }
    
    private void openStory(int storyId) {
        story = Story.getStory(storyId, this);
        schedule = 100;
    }
    
    public void closeStory() {
        story.dispose();
        story = null;
        schedule = 40;
        clearNotify();
        if(templeId != TEMPLE_CYLOP) {
            if(notifyStatus == 2 && perfectPuzzle == totalPuzzle(templeId)) {
                String referCode = templeId + "" + templeId;
                openStory(Integer.parseInt(referCode));
                notifyStatus = 3;
            }
            if(notifyStatus == 2 || notifyStatus == 3) { //nếu vừa hoàn thành (hoàn hảo), vừa unlock temple mới
                for(int i = 1; i < TEMPLE_REQUIRE.length; i++) {
                    if(solvedPuzzle == TEMPLE_REQUIRE[i]) {
                        String referCode = templeId + "" + i;
                        openStory(Integer.parseInt(referCode));
                        break;
                    }
                }
                notifyStatus = 4;
            }
        } else if(firstTimeToPlay) {
            if(notifyStatus == 2) {
                openStory(Story.STORY_CYLOP_FLORA);
                notifyStatus = 0;
            } else {
                parent.openTemple(TEMPLE_FLORA);
                parent.gotoIslandMap();
            }
        }
    }
    
    public static int totalPuzzle(int templeId) {
        return Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId];
    }
}

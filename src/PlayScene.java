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
import util.DataHelper;
import util.ButtonSprite;
import util.ImageHelper;
import util.StringHelper;

/**
 *
 * @author Thinh Pham
 */
public class PlayScene extends GameScene {
    
    static final byte TILE_NONE = 0;
    static final byte TILE_WANT = 1;
    static final byte TILE_STICKY = 2;
    static final byte TILE_RED = 3;
    static final byte TILE_BLUE = 4;
    static final byte TILE_GREEN = 5;
    static final byte TILE_DARKBLUE = 6;
    static final byte COMMAND_NONE = -1;
    static final byte COMMAND_UP = 0;
    static final byte COMMAND_RIGHT = 1;
    static final byte COMMAND_DOWN = 2;
    static final byte COMMAND_LEFT = 3;
    static final byte COMMAND_FIRE = 4;
    static final byte COMMAND_BACK = 5;
    static final byte COMMAND_RESET = 6;
    static final byte CURTAIN_NONE = -1;
    static final byte CURTAIN_FINISH = 0;
    static final byte CURTAIN_HINT = 1;
    
    private Image viewpotImage, sidebarImage, navImage, possibleMask, imposibleMask, confirmDialogImage;
    private Image slidingTile, stackImage, cellMask, curtainImage, puzzleCompleteImage, quickMenuImage, tutorialImage;
    private Image[] aimImage;
    private Sprite characterSprite, tileSprite, shruggingSprite, celebratingSprite;
    private int[] rgb = new int[16*16];
    private short slidingPositionX, slidingPositionY, slidingTargetX, slidingTargetY;
    private byte slidingDeltaX, slidingDeltaY;
    private boolean isSliding = false, slidingDone = false;
    
    private byte curtainType = CURTAIN_NONE;
    private int tutorialBallonX, tutorialBallonY;
    private int[] tutorialCell = new int[] { -1, -1 };
    
    private Graphics viewpotGraphic, slidingTileGraphic;
    
//#if ScreenWidth == 400
//#     private short autoCloseMenu = 0;
//#     private boolean quickMenu = false, quickMenuOpening = false, quickMenuClosing = false;
//#     private int quickMenuY = -120;
//#endif
    
    private int[] curtainX = new int[] { 400, 400, 400, 400 };
    
    private final int puzzleId, templeId;
    private int bestTime, bestMove, bestMedal;
    private boolean hintUnlocked = false;
    private byte templeSolvedPuzzle, templePerfectPuzzle;
    
    public String puzzleTitle;
    public byte[][] cell = new byte[16][16], defaultData = new byte[16][16];
    //public byte tileRemain;
    private int tileStackY;
    private short stackTimeline = -1, tileStackTarget;
    private byte[] cursor = new byte[] {1, 1};
    private short cursorX = 36, cursorY = 36;
    private boolean moved = false; // make sure each click have at least 1 cell moved
    private boolean navbarTouching = false, viewpotTouching = false;
    private byte throwingTimeline = -1, shruggingTimeline = -1, curtainTimeline = -1;
    private boolean isPossible = false;
    private byte posibleDirection = COMMAND_UP, aimDirection;
    private byte aimDistance = 0;
    private boolean aimClosing = false;
    private byte activeCommand = COMMAND_NONE;
    private byte frameTick;
    private short second = 0;
    private StringBuffer undoCell = new StringBuffer(), undoDirection = new StringBuffer();
    
    private Hint hint;
    private Tutorial tutorial;
    private String hintData;
    private ButtonSprite[] buttons;
    
    private final Main main;
    
    public PlayScene(Main main, int templeId, int puzzleId) {
        super();
        this.main = main;
        this.puzzleId = puzzleId;
        this.templeId = templeId;
        lazyLoad();
        begin(100);
    }
    
    void load() {
        if (templeId != TempleScene.TEMPLE_CYLOP) {
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                String[] puzzleData = StringHelper.split(new String(rs.getRecord(puzzleId - 9)), "#");
                bestMove = Integer.parseInt(puzzleData[0]);
                bestTime = Integer.parseInt(puzzleData[1]);
                bestMedal = Integer.parseInt(puzzleData[2]);
                hintUnlocked = puzzleData[3].equals("1");
                String[] templeData = StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId)), "#");
                templeSolvedPuzzle = Byte.parseByte(templeData[0]);
                templePerfectPuzzle = Byte.parseByte(templeData[1]);
                //templeNotifyLastPuzzle = templeData[2].equals("1");
                rs.closeRecordStore();
            } catch (RecordStoreException ex) { }
        } else {
            tutorial = Tutorial.getTutorial(puzzleId);
        }
        slidingTile = Image.createImage(12, 12);
        slidingTileGraphic = slidingTile.getGraphics();
        
        tileSprite = new Sprite(ImageHelper.loadImage("/images/tile.png"), 12, 12);
        viewpotImage = Image.createImage(252, 240);
        viewpotGraphic = viewpotImage.getGraphics();
        viewpotGraphic.drawImage(ImageHelper.loadImage("/images/playbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        Puzzle myPuzzle = Puzzle.getPuzzle(puzzleId);
        puzzleTitle = myPuzzle.getTitle();
        String data = myPuzzle.getData();
        String name = myPuzzle.getName();
        String title = myPuzzle.getTitle();
        int row, col;
        byte value, tileRemain = 0, totalTile = 0;
        for (short i = 0; i < 16*16; i++) {
            row = i / 16;
            col = i % 16;
            value = (byte) data.charAt(i);
            tileSprite.setFrame(value);
            tileSprite.setPosition(col * 12 + 24, row * 12 + 24);
            tileSprite.paint(viewpotGraphic);
            cell[row][col] = defaultData[row][col] = value;
            switch (value) {
                case PlayScene.TILE_WANT:
                case PlayScene.TILE_STICKY:
                    tileRemain++;
                    totalTile++;
                    break;
                    
                case PlayScene.TILE_BLUE:
                    totalTile++;
                    break;
                    
                case PlayScene.TILE_RED:
                    tileRemain--;
                    break;
            }
        }
        //if(((Play)parent).getTempleId() != Temple.TEMPLE_CYLOP) ((Play)parent).worldRecord = IOHelper.getFileSize("/data/hints/" + name + ".dat");
        if (templeId != TempleScene.TEMPLE_CYLOP)
            hintData = DataHelper.readFile("/data/hints/" + name + ".dat");
        
        ImageHelper.loadImage("/data/images/" + name + ".gif")
                .getRGB(rgb, 0, 16, 0, 0, 16, 16);
        
        int stackHeight = 12 * totalTile;
        stackImage = Image.createImage(12, stackHeight);
        Graphics g = stackImage.getGraphics();
        tileSprite.setFrame(PlayScene.TILE_BLUE);
        for (int i = stackHeight - 12; i >= 0; i -= 12) {
            tileSprite.setPosition(0, i);
            tileSprite.paint(g);
        }
        tileStackY = 240 - 12 * tileRemain;
        
        int[] _rgb = new int[12*12];
        for (int i = 0; i < _rgb.length; ++i) {
            _rgb[i] = 0x44ffffff;
        }
        possibleMask = Image.createRGBImage(_rgb, 12, 12, true);
        for (int i = 0; i < _rgb.length; ++i) {
            _rgb[i] = 0x22ff0000;
        }
        imposibleMask = Image.createRGBImage(_rgb, 12, 12, true);
        
//#if ScreenWidth == 400
//#         puzzleCompleteImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
//#         g = puzzleCompleteImage.getGraphics();
//#         g.drawImage(ImageHelper.loadImage("/images/puzzlecompleted.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
//#         LazyLoad.drawPuzzleImage(puzzleId, 161, 50, 5, g, ImageHelper.createPixelMask(5), 3);
//#         g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#         g.setColor(0x000000);
//#         g.drawString(title, Main.SCREEN_WIDTH / 2 + 1, 148 + 1, Graphics.HCENTER | Graphics.BASELINE);
//#         g.setColor(0xffd800);
//#         g.drawString(title, Main.SCREEN_WIDTH / 2, 148, Graphics.HCENTER | Graphics.BASELINE);
//#         
//#         Image gamepadImage = ImageHelper.loadImage("/images/navbutton.png");
//#         buttons = new ButtonSprite[] {
//#             new ButtonSprite(gamepadImage, PlayScene.COMMAND_UP, 308, 131, 40, 30),
//#             new ButtonSprite(gamepadImage, PlayScene.COMMAND_RIGHT, 350, 164, 40, 30),
//#             new ButtonSprite(gamepadImage, PlayScene.COMMAND_DOWN, 308, 197, 40, 30),
//#             new ButtonSprite(gamepadImage, PlayScene.COMMAND_LEFT, 266, 164, 40, 30),
//#             new ButtonSprite(gamepadImage, PlayScene.COMMAND_FIRE, 308, 164, 40, 30)
//#         };
//#         
//#         sidebarImage = Image.createImage(148, 240);
//#         g = sidebarImage.getGraphics();
//#         g.drawImage(ImageHelper.loadImage("/images/sidebarbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
//#         if (templeId == TempleScene.TEMPLE_CYLOP) {
//#             g.drawImage(ImageHelper.loadImage("/images/tutorialsidebar.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
//#             g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#             String[] description = Tutorial.getDescription(puzzleId);
//#             for (int i = 0; i < description.length; i++) {
//#                 g.drawString(description[i], 75, 14*i + 24, Graphics.HCENTER | Graphics.BASELINE);
//#             }
//#         } else {
//#             g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#             g.setColor(0xff0000);
//#             g.drawString(Integer.toString(hintData.length()), 126, 82, Graphics.RIGHT | Graphics.BASELINE);
//#         }
//#elif ScreenWidth == 320
        puzzleCompleteImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        g = puzzleCompleteImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/puzzlecompleted.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        LazyLoad.drawPuzzleImage(puzzleId, 128, 48, 4, g, ImageHelper.createPixelMask(4), 3);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.setColor(0x000000);
        g.drawString(title, Main.SCREEN_WIDTH / 2 + 1, 130 + 1, Graphics.HCENTER | Graphics.BASELINE);
        g.setColor(0xffd800);
        g.drawString(title, Main.SCREEN_WIDTH / 2, 130, Graphics.HCENTER | Graphics.BASELINE);
        
        sidebarImage = Image.createImage(68, 240);
        g = sidebarImage.getGraphics();
        if (templeId == TempleScene.TEMPLE_CYLOP) {
            g.drawImage(ImageHelper.loadImage("/images/tutorialsidebar.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            String[] description = Tutorial.getDescription(puzzleId);
            for (int i = 0; i < description.length; i++) {
                g.drawString(description[i], 34, 14*i + 18, Graphics.HCENTER | Graphics.BASELINE);
            }
        } else {
            g.drawImage(ImageHelper.loadImage("/images/sidebarbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0x00ff00);
            g.drawString(Integer.toString(hintData.length()), 32, 94, Graphics.HCENTER | Graphics.BASELINE);
        }
//#endif
        
        characterSprite = new Sprite(ImageHelper.loadImage("/images/tilemaster.png"), 20, 26);
        characterSprite.setPosition(20, -2);
        
        aimImage = new Image[] {
            ImageHelper.loadImage("/images/aimup.png"),
            ImageHelper.loadImage("/images/aimright.png"),
            ImageHelper.loadImage("/images/aimdown.png"),
            ImageHelper.loadImage("/images/aimleft.png")
        };
        navImage = ImageHelper.loadImage("/images/navigator.png");
        shruggingSprite = new Sprite(ImageHelper.loadImage("/images/shrugging.png"), 20, 26);
        celebratingSprite = new Sprite(ImageHelper.loadImage("/images/celebrating.png"), 20, 35);
        cellMask = ImageHelper.loadImage("/images/cellmask.png");
        curtainImage = ImageHelper.loadImage("/images/curtain.png");
        quickMenuImage = ImageHelper.loadImage("/images/quickmenu.png");
        calcPosible();
        updateCharacterSprite();
        
        if (templeId == TempleScene.TEMPLE_CYLOP)
            prepareTutorialStep();
    }
    
    void unload() {
        viewpotImage = null;
        sidebarImage = null;
        navImage = null;
        possibleMask = null;
        imposibleMask = null;
        confirmDialogImage = null;
        slidingTile = null;
        stackImage = null;
        cellMask = null;
        curtainImage = null;
        puzzleCompleteImage = null;
        quickMenuImage = null;
        tutorialImage = null;
        aimImage = null;
        characterSprite = null;
        tileSprite = null;
        shruggingSprite = null;
        celebratingSprite = null;
        rgb = null;
        tutorialCell = null;
        viewpotGraphic = null;
        slidingTileGraphic = null;
        curtainX = null;
        puzzleTitle = null;
        cell = null;
        defaultData = null;
        cursor = null;
        undoCell = null;
        undoDirection = null;
        hint = null;
        tutorial = null;
        hintData = null;
        buttons = null;
    }
    
    public void paint(Graphics g) {
        if (curtainTimeline > 0) {
            if (curtainTimeline < 93) {
                if (curtainType == CURTAIN_FINISH) {
                    g.drawImage(viewpotImage, 0, 0, Graphics.TOP | Graphics.LEFT);
                    celebratingSprite.paint(g);
                }
            } else {
                if (curtainType == CURTAIN_FINISH)
                    g.drawImage(puzzleCompleteImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                else if (curtainType == CURTAIN_HINT)
                    hint.paint(g);
            }
            for (int i = 0; i < 4; i++) {
                g.drawImage(curtainImage, curtainX[i], 0, Graphics.LEFT | Graphics.TOP);
            }
            return;
        }
        
        if (hint != null) {
            hint.paint(g);
            return;
        }
        
        // while sliding state, draw board only
        if (isSliding) {
            g.drawImage(viewpotImage, 0, 0, Graphics.TOP | Graphics.LEFT);
            if (!slidingDone)
                g.drawImage(slidingTile, slidingPositionX, slidingPositionY, Graphics.LEFT | Graphics.TOP);
            characterSprite.paint(g);
            g.drawImage(stackImage, 240, tileStackY, Graphics.LEFT | Graphics.TOP);
            return;
        }
        
        if (!isLoading(g)) {
            g.drawImage(viewpotImage, 0, 0, Graphics.TOP | Graphics.LEFT);
            g.drawImage(stackImage, 240, tileStackY, Graphics.TOP | Graphics.LEFT);
            g.drawImage(sidebarImage, 252, 0, Graphics.TOP | Graphics.LEFT);
            // draw cursor
            if (isPossible) {
                int aimArrowY, aimArrowX;
                switch (posibleDirection) {
                    case COMMAND_UP:
                        aimArrowY = cursorY - aimDistance;
                        while (aimArrowY > 24) {
                            g.drawImage(aimImage[aimDirection], cursorX, aimArrowY, Graphics.BOTTOM | Graphics.LEFT);
                            aimArrowY -= 24;
                        }
                        break;

                    case COMMAND_DOWN:
                        aimArrowY = cursorY + aimDistance + 12;
                        while (aimArrowY < 216) {
                            g.drawImage(aimImage[aimDirection], cursorX, aimArrowY, Graphics.TOP | Graphics.LEFT);
                            aimArrowY += 24;
                        }
                        break;

                    case COMMAND_LEFT:
                        aimArrowX = cursorX - aimDistance;
                        while (aimArrowX > 24) {
                            g.drawImage(aimImage[aimDirection], aimArrowX, cursorY, Graphics.TOP | Graphics.RIGHT);
                            aimArrowX -= 24;
                        }
                        break;
                        
                    case COMMAND_RIGHT:
                        aimArrowX = cursorX + aimDistance + 12;
                        while (aimArrowX < 216) {
                            g.drawImage(aimImage[aimDirection], aimArrowX, cursorY, Graphics.TOP | Graphics.LEFT);
                            aimArrowX += 24;
                        }
                        break;
                }
                g.setColor(0xffffff);
            } else {
                g.setColor(0xff0000);
            }
            g.drawRect(cursorX, cursorY, 11, 11);
            g.drawRect(cursorX-1, cursorY-1, 13, 13);

            // draw character
            if (shruggingTimeline >= 0)
                shruggingSprite.paint(g);
            else
                characterSprite.paint(g);

            // highlight selected row and column by click on board directly
            if (viewpotTouching) {
                int x = 24;
                while (x < 216) {
                    if (isPossible) {
                        g.drawImage(possibleMask, x, cursorY, Graphics.LEFT | Graphics.TOP);
                        g.drawImage(possibleMask, cursorX, x, Graphics.LEFT | Graphics.TOP);
                    } else {
                        g.drawImage(imposibleMask, x, cursorY, Graphics.LEFT | Graphics.TOP);
                        g.drawImage(imposibleMask, cursorX, x, Graphics.LEFT | Graphics.TOP);
                    }
                    x += 12;
                }
            }
            
//#if ScreenWidth == 400
//#             for (int i = 0; i < 5; i++) {
//#                 buttons[i].paint(g);
//#             }
//#             g.drawImage(navImage, 266, 131, Graphics.LEFT | Graphics.TOP);
//#             
//#             if (templeId != TempleScene.TEMPLE_CYLOP) {
//#                 g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#                 g.setColor(0xff0000);
//#                 g.drawString(Integer.toString(undoCell.length()), 362, 64, Graphics.RIGHT | Graphics.BASELINE);
//#                 //g.drawString(Integer.toString(hintData.length()), 378, 82, Graphics.RIGHT | Graphics.BASELINE);
//#                 g.drawString(Integer.toString(second), 362, 100, Graphics.RIGHT | Graphics.BASELINE);
//#                 //g.drawString(strMoves, 328, 98, Graphics.HCENTER | Graphics.BASELINE);
//#                 if (quickMenu || quickMenuOpening || quickMenuClosing)
//#                     g.drawImage(quickMenuImage, 252, quickMenuY, Graphics.LEFT | Graphics.TOP);
//#             } else if (tutorialImage != null && curtainTimeline == -1) {
//#                 g.drawImage(tutorialImage, tutorialBallonX, tutorialBallonY, Graphics.LEFT | Graphics.TOP);
//#             }
//#elif ScreenWidth == 320
            if (templeId != TempleScene.TEMPLE_CYLOP) {
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.setColor(0xffffff);
                g.drawString(Integer.toString(undoCell.length()), 284, 70, Graphics.HCENTER | Graphics.BASELINE);
                //g.drawString(Integer.toString(hintData.length()), 378, 82, Graphics.RIGHT | Graphics.BASELINE);
                g.setColor(0xffff00);
                g.drawString(Integer.toString(second), 284, 120, Graphics.HCENTER | Graphics.BASELINE);
                //g.drawString(strMoves, 328, 98, Graphics.HCENTER | Graphics.BASELINE);
            } else if (tutorialImage != null && curtainTimeline == -1) {
                g.drawImage(tutorialImage, tutorialBallonX, tutorialBallonY, Graphics.LEFT | Graphics.TOP);
            }
//#endif
            
            if (confirmDialogImage != null)
                g.drawImage(confirmDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
        }
    }
    
    protected void update() {
        if (curtainTimeline >= 0) {
            updateCurtain();
            if (hint != null)
                hint.update();
            return;
        }
        
        // while sliding, do not take any action
        if (isSliding) {
            updateSliding();
            return;
        }
        
        if (templeId != TempleScene.TEMPLE_CYLOP) {
//#if ScreenWidth == 400
//#             // position of quick menu
//#             if (quickMenuOpening) {
//#                 if (quickMenuY < 0)
//#                     quickMenuY += 10;
//#                 else {
//#                     quickMenu = true;
//#                     quickMenuOpening = false;
//#                     framePeriod = 100;
//#                     autoCloseMenu = 60;
//#                 }
//#                 return;
//#             } else if (quickMenuClosing) {
//#                 if (quickMenuY > -120)
//#                     quickMenuY -= 10;
//#                 else {
//#                     quickMenu = false;
//#                     quickMenuClosing = false;
//#                     framePeriod = 100;
//#                 }
//#                 return;
//#             }
//#             // auto close menu
//#             if (quickMenu && autoCloseMenu > 0) {
//#                 if (--autoCloseMenu == 0) {
//#                     quickMenuClosing = true;
//#                     framePeriod = 40;
//#                 }
//#             }
//#endif
            // compute seconds
            if (++frameTick == 10) {
                second++;
                frameTick = 0;
            }
        }
        
        // if clicking on buttons
        //if(navbarTouching && activeCommand != COMMAND_NONE) move();
        if (activeCommand != COMMAND_NONE)
            move();
        
        // head of aim arrows
        if (isPossible) {
            //if(aimClosing) aimDistance--; else aimDistance++;
            aimDistance += aimClosing ? -1 : 1;
            if (aimDistance == 0 || aimDistance == 4)
                aimClosing = !aimClosing;
        }
        
        if (shruggingTimeline >= 0) {
            shruggingSprite.setFrame(shruggingTimeline);
            if (++shruggingTimeline > 7)
                shruggingTimeline = -1;
        } else if (throwingTimeline >= 0) {
            characterSprite.nextFrame();
            if (++throwingTimeline > 1) {
                if (cell[cursor[0]][cursor[1]] <= 2) {
                    if (tileStackY >= 240) {
                        shruggingTimeline = 0;
                        throwingTimeline = -1;
                        return;
                    }
                    pushTile();
                }
                else removeTile();
                
                boolean needUpdate = true;
                // if same cell with last undo, just remove that undo data
                int dataIndex = undoCell.length();
                if (dataIndex > 0) {
                    dataIndex--;
                    int cellIndex = (int) undoCell.charAt(dataIndex);
                    if (cursor[0] == (byte)(cellIndex / 16) && cursor[1] == (byte)(cellIndex % 16)) {
                        undoCell.deleteCharAt(dataIndex);
                        undoDirection.deleteCharAt(dataIndex);
                        needUpdate = false;
                    }
                }
                if (needUpdate) {
                    // add cellIndex into undoCell list
                    undoCell.append((char)(cursor[0] * 16 + cursor[1]));
                    // add aimDirection into undoDirection list
                    undoDirection.append((char)posibleDirection);
                }
                // start play
                characterSprite.setFrame(characterSprite.getFrame() - 2);
                isSliding = true;
                slidingDone = false;
                throwingTimeline = -1;
                stackTimeline = 0;
                framePeriod = 20;
            }
        }
    }
    
    private void updateSliding() {
        if (stackTimeline >= 0) {
            if (++stackTimeline < 7) {
                tileStackY += (tileStackY < tileStackTarget) ? 2 : -2;
            } else {
                stackTimeline = -1;
                if (slidingDone) {
                    slidingDone = false;
                    isSliding = false;
                    framePeriod = 100;
                    if (checkEndGame())
                        return;
                }
            }
        }
      if (!slidingDone) {
            slidingPositionX += slidingDeltaX;
            slidingPositionY += slidingDeltaY;
            if (slidingPositionX == slidingTargetX && slidingPositionY == slidingTargetY)
                finishSliding();
        }
    }
    
    private void updateCurtain() {
        if (curtainTimeline < 127) {
            curtainTimeline++;
            if (curtainTimeline < 3) {
                celebratingSprite.nextFrame();
            } else if (curtainTimeline == 3) {
                framePeriod = 25;
            } else if (curtainTimeline < 50) {
                int j, left, top;
                for (byte i = 0; i < 16; framePeriod = i++) {
                    j = curtainTimeline - i * 2 - 4;
                    if (j < 0) break;
                    else if (j < 16) {
                        left = j * 12 + 24;
                        top = i * 12 + 24;
                        viewpotGraphic.setColor(rgb[i * 16 + j]);
                        viewpotGraphic.fillRect(left, top, 12, 12);
                        viewpotGraphic.drawImage(cellMask, left, top, Graphics.LEFT | Graphics.TOP);
                    }
                }
            } else if (curtainTimeline == 50) {
                framePeriod = 50;
            } else if (curtainTimeline > 70 && curtainTimeline < 93) {
                for (byte i = 0; i < 4; i++) {
                    if (curtainX[i] > i * 100 - 20)
                        curtainX[i] -= 20;
                }
            } else if (curtainTimeline == 93) {
                if (curtainType == CURTAIN_FINISH)
                    fillPuzzleComplete();
                else if (curtainType == CURTAIN_HINT)
                    prepareHint();
            } else if (curtainTimeline < 116) {
                int j;
                for (byte i = 0; i < 4; i++) {
                    //j = 96 - (3 - i) * 5 - 1;
                    j = 94 + 5 * i;
                    if (curtainTimeline > j)
                        curtainX[i] += 20;
                }
            } else {
                if (curtainType == CURTAIN_FINISH)
                    framePeriod = 32767;
                else if(curtainType == CURTAIN_HINT)
                    framePeriod = 20;
            }
        }
    }
    
    private void prepareHint() {
        hint = new Hint(hintData, this);
//#if ScreenWidth == 400
//#         quickMenu = false;
//#         quickMenuY = -120;
//#endif
    }
    
    private void fillPuzzleComplete() {
        Graphics g = puzzleCompleteImage.getGraphics();
        if (templeId == TempleScene.TEMPLE_CYLOP) {
            // tutorial
            // update rms
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                // number of solved tutorial
                byte tutPassed = Byte.parseByte(StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC)), "#")[0]);
                StringBuffer tutData = new StringBuffer();
                tutData.append(Integer.toString((puzzleId > tutPassed)?puzzleId:tutPassed));
                tutData.append("#0#");
                tutData.append((puzzleId > tutPassed && tutPassed == 8)?"2":"0");
                byte[] data = tutData.toString().getBytes();
                rs.setRecord(Main.RMS_USER_TEMPLESTATISTIC, data, 0, data.length);
                rs.closeRecordStore();
            } catch (RecordStoreException ex) {}
//#if ScreenWidth == 400
//#             g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#             g.drawString("Learn more tricks", Main.SCREEN_WIDTH / 2, 168, Graphics.HCENTER | Graphics.BASELINE);
//#             g.drawString("from the other puzzles", Main.SCREEN_WIDTH / 2, 182, Graphics.HCENTER | Graphics.BASELINE);
//#             g.drawString("in the Cylop's cave!", Main.SCREEN_WIDTH / 2, 196, Graphics.HCENTER | Graphics.BASELINE);
//#             g.drawString("not for", 66, 216, Graphics.HCENTER | Graphics.BASELINE);
//#             g.drawString("tutorial", 66, 232, Graphics.HCENTER | Graphics.BASELINE);
//#elif ScreenWidth == 320
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Learn more tricks", Main.SCREEN_WIDTH / 2, 148, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("from the other puzzles", Main.SCREEN_WIDTH / 2, 160, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("in the Cylop's cave!", Main.SCREEN_WIDTH / 2, 172, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("not for", 52, 184, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("tutorial", 52, 196, Graphics.HCENTER | Graphics.BASELINE);
//#endif
            
        } else {
            int numMoves = undoCell.length();
            byte myMedal = 3;
            if (numMoves == hintData.length())
                myMedal = 0;
            else if (numMoves <= hintData.length() + 5)
                myMedal = 1;
            else if (numMoves <= hintData.length() + 10)
                myMedal = 2;
            // update rms
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                String notifyStatus = "0";
                byte[] data;
                // number of solved puzzles
                int numSolvedPuzzles = Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_SOLVEDPUZZLE)));
                if (bestMove == 0) { // if unsolved puzzle before
                    numSolvedPuzzles++;
                    data = Integer.toString(numSolvedPuzzles).getBytes();
                    rs.setRecord(Main.RMS_USER_SOLVEDPUZZLE, data, 0, data.length);
                    templeSolvedPuzzle++;
                    // record message
                    // open last puzzle
                    if (templeSolvedPuzzle == TempleScene.totalPuzzle(templeId) - 1)
                        notifyStatus = "1";
                    // complete temple
                    else if (templeSolvedPuzzle == TempleScene.totalPuzzle(templeId))
                        notifyStatus = "2";
                    // open new temple
                    else {
                        for (byte i = 0; i < TempleScene.TEMPLE_REQUIRE.length; i++) {
                            if (numSolvedPuzzles == TempleScene.TEMPLE_REQUIRE[i]) {
                                notifyStatus = "4";
                                if (i != TempleScene.TEMPLE_CYLOP)
                                    main.openTemple(i);
                                break;
                            }
                        }
                    }
                } else if (bestMedal > 0 && myMedal == 0 && templePerfectPuzzle == TempleScene.totalPuzzle(templeId) - 1) {
                    // perfect temple
                    notifyStatus = "3";
                }
                // temple
                StringBuffer newData = new StringBuffer();
                // number of completed puzzles
                newData.append(Integer.toString(templeSolvedPuzzle));
                newData.append("#");
                // number of perfect puzzles
                newData.append(Integer.toString((bestMedal > 0 && myMedal == 0)?++templePerfectPuzzle:templePerfectPuzzle));
                newData.append("#");
                // message state
                newData.append(notifyStatus);
                data = newData.toString().getBytes();
                rs.setRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId, data, 0, data.length);
                // puzzle
                newData = new StringBuffer();
                newData.append((numMoves < bestMove || bestMove == 0)?numMoves:bestMove);
                newData.append("#");
                newData.append((second < bestTime || bestTime == 0)?second:bestTime);
                newData.append("#");
                newData.append(Integer.toString((myMedal < bestMedal)?myMedal:bestMedal));
                newData.append("#");
                newData.append(hintUnlocked?"1":"0");
                data = newData.toString().getBytes();
                rs.setRecord(puzzleId - 9, data, 0, data.length);
                // done
                rs.closeRecordStore();
            } catch (RecordStoreException ex) { }
            
            // draw image
//#if ScreenWidth == 400
//#             g.drawString(numMoves + " moves", Main.SCREEN_WIDTH / 2, 168, Graphics.HCENTER | Graphics.BASELINE);
//#elif ScreenWidth == 320
            g.drawString(numMoves + " moves", Main.SCREEN_WIDTH / 2, 146, Graphics.HCENTER | Graphics.BASELINE);
//#endif
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            String[] desc;
            switch (myMedal) {
                case 0:
                    desc = new String[] { "This is the world record!" };
                    break;
                    
                case 1:
                    desc = new String[] {
                        "Within 5 moves of",
                        "The world record!"
                    };
                    break;
                    
                case 2:
                    desc = new String[] {
                        "Within 10 moves of",
                        "The world record!"
                    };
                    break;
                    
                default:
                    desc = new String[] {};
                    break;
            }
            int secs = (second < bestTime || bestTime == 0) ? second : bestTime;
//#if ScreenWidth == 400
//#             int y = 184;
//#             for (byte i = 0; i < desc.length; i++) {
//#                 y += i * 16;
//#                 g.drawString(desc[i], Main.SCREEN_WIDTH / 2, y, Graphics.HCENTER | Graphics.BASELINE);
//#             }
//#             ImageHelper.MEDAL_SPRITE.setFrame(myMedal);
//#             ImageHelper.MEDAL_SPRITE.setPosition(238, 148);
//#             ImageHelper.MEDAL_SPRITE.paint(g);
//#             g.drawString(secs + " secs", 66, 218, Graphics.HCENTER | Graphics.BASELINE);
//#elif ScreenWidth == 320
            int y = 160;
            for (byte i = 0; i < desc.length; i++) {
                y += i * 14;
                g.drawString(desc[i], Main.SCREEN_WIDTH / 2, y, Graphics.HCENTER | Graphics.BASELINE);
            }
            if (myMedal < Puzzle.MEDAL_NONE) {
                ImageHelper.MEDAL_SPRITE.setFrame(myMedal);
                ImageHelper.MEDAL_SPRITE.setPosition(190, 128);
                ImageHelper.MEDAL_SPRITE.paint(g);
            }
            g.drawString(secs + " secs", 52, 190, Graphics.HCENTER | Graphics.BASELINE);
//#endif
        }
    }
    
    private void pushTile() {
        tileStackTarget = (short)(tileStackY + 12);
        // prepare slidingTile image
        tileSprite.setPosition(0, 0);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]] + 3);
        //tileSprite.paint(slidingTile.getGraphics());
        tileSprite.paint(slidingTileGraphic);
        
        // prepare start position, move direction
        switch (aimDirection) {
            case COMMAND_UP:
                slidingPositionX = cursorX;
                slidingPositionY = 216;
                slidingDeltaX = 0;
                slidingDeltaY = -12;
                break;
                
            case COMMAND_DOWN:
                slidingPositionX = cursorX;
                slidingPositionY = 12;
                slidingDeltaX = 0;
                slidingDeltaY = 12;
                break;
                
            case COMMAND_LEFT:
                slidingPositionX = 216;
                slidingPositionY = cursorY;
                slidingDeltaX = -12;
                slidingDeltaY = 0;
                break;
                
            case COMMAND_RIGHT:
                slidingPositionX = 12;
                slidingPositionY = cursorY;
                slidingDeltaX = 12;
                slidingDeltaY = 0;
                break;
        }
        
        // prepare destination
        slidingTargetX = cursorX;
        slidingTargetY = cursorY;
    }
    
    private void removeTile() {
        tileStackTarget = (short)(tileStackY - 12);
        // update background image
        tileSprite.setPosition(cursorX, cursorY);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]] - 3);
        tileSprite.paint(viewpotGraphic);
        
        // prepare slidingTile image
        tileSprite.setPosition(0, 0);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]]);
        //tileSprite.paint(slidingTile.getGraphics());
        tileSprite.paint(slidingTileGraphic);
        
        // prepare start position
        slidingPositionX = cursorX;
        slidingPositionY = cursorY;
        
        // prepare destination, move direction
        switch (aimDirection) {
            case COMMAND_UP:
                slidingTargetX = cursorX;
                slidingTargetY = 12;
                slidingDeltaX = 0;
                slidingDeltaY = -12;
                break;
                
            case COMMAND_DOWN:
                slidingTargetX = cursorX;
                slidingTargetY = 216;
                slidingDeltaX = 0;
                slidingDeltaY = 12;
                break;

            case COMMAND_LEFT:
                slidingTargetX = 12;
                slidingTargetY = cursorY;
                slidingDeltaX = -12;
                slidingDeltaY = 0;
                break;

            case COMMAND_RIGHT:
                slidingTargetX = 216;
                slidingTargetY = cursorY;
                slidingDeltaX = 12;
                slidingDeltaY = 0;
                break;
        }
    }
    
    private void finishSliding() {
        slidingDone = true;
        if (stackTimeline == -1) {
            isSliding = false;
            framePeriod = 100;
        }
        if (cell[cursor[0]][cursor[1]] <= 2) {
            // push in
            viewpotGraphic.drawImage(slidingTile, slidingPositionX, slidingPositionY, Graphics.LEFT | Graphics.TOP);
            cell[cursor[0]][cursor[1]] += 3;
            if(checkEndGame()) return;
        } else {
            // pull out
            cell[cursor[0]][cursor[1]] -= 3;
        }
        // check finish
        calcPosible();
        updateCharacterSprite();
        if (templeId == TempleScene.TEMPLE_CYLOP)
            prepareTutorialStep();
    }
    
    private boolean checkEndGame() {
        if (tileStackY >= 240) {
            for (short i = 0; i < 16; i++) {
                for (short j = 0; j < 16; j++) {
                    if (cell[i][j] == TILE_WANT || cell[i][j] == TILE_STICKY)
                        return false;
                }
            }
            celebratingSprite.setFrame(0);
            celebratingSprite.setPosition(characterSprite.getX(), characterSprite.getY() - 9);
            isPossible = false;
            curtainType = CURTAIN_FINISH;
            curtainTimeline = 0;
            return true;
        }
        return false;
    }
    
    private void undoExec() {
        int dataIndex = undoCell.length();
        if (dataIndex > 0) {
            dataIndex--;
            // take cell data
            int cellIndex = (int)undoCell.charAt(dataIndex);
            cursor[0] = (byte)(cellIndex / 16);
            cursor[1] = (byte)(cellIndex % 16);
            cursorX = (short)(cursor[1] * 12 + 24);
            cursorY = (short)(cursor[0] * 12 + 24);
            // take direction data
            posibleDirection = (byte)undoDirection.charAt(dataIndex);
            updateCharacterSprite();
            if (cell[cursor[0]][cursor[1]] == TILE_GREEN)
                aimDirection = posibleDirection;
            // execute undo
            if (cell[cursor[0]][cursor[1]] <= 2)
                pushTile();
            else
                removeTile();
            isSliding = true;
            slidingDone = false;
            stackTimeline = 0;
            framePeriod = 20;
            // remove undo data
            undoCell.deleteCharAt(dataIndex);
            undoDirection.deleteCharAt(dataIndex);
        }
    }
    
    public void prepareTutorialStep() {
        int step = undoCell.length();
        tutorialImage = tutorial.getBallon(step);
        int cellIndex = tutorial.getCellIndex(step);
        int row = tutorialCell[0] = cellIndex / 16;
        int col = tutorialCell[1] = cellIndex % 16;
        if (row <= 7 && col <= 7) { // upper-left corner
            tutorialBallonX = col * 12 + 30;
            tutorialBallonY = row * 12 + 30;
        }
        else if (row <= 7 && col >= 8) { // upper-right corner
            tutorialBallonX = col * 12 - 97;
            tutorialBallonY = row * 12 + 30;
        }
        else if (row >= 8 && col <= 7) { // lower-left corner
            tutorialBallonX = col * 12 + 30;
            tutorialBallonY = row * 12 - 80;
        }
        else { // lower-right corner
            tutorialBallonX = col * 12 - 97;
            tutorialBallonY = row * 12 - 80;
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if (hint != null) {
            hint.pointerPressed(x, y);
            return;
        }
        else if (isSliding || curtainTimeline >= 0 || confirmDialogImage != null) {
            return;
        }
        
//#if ScreenWidth == 400
//#         if (templeId != TempleScene.TEMPLE_CYLOP && !quickMenu && x > 272 && x < 380 && y > 12 && y < 38) {
//#             framePeriod = 40;
//#             quickMenuOpening = true;
//#             return;
//#         }
//#         else if (quickMenu && x > 272 && x < 380 && y > 78 && y <104) {
//#             framePeriod = 40;
//#             quickMenuClosing = true;
//#             return;
//#         }
//#         if (quickMenu && !quickMenuOpening && !quickMenuClosing) {
//#             if (x > 266 && x < 326 && y > 12 && y < 42) {
//#                 // Hint button
//#                 framePeriod = 50;
//#                 curtainType = CURTAIN_HINT;
//#                 curtainTimeline = 71;
//#                 autoCloseMenu = 60;
//#                 return;
//#             } else if (x > 328 && x < 388 && y > 12 && y < 42) {
//#                 // Undo button
//#                 undoExec();
//#                 autoCloseMenu = 60;
//#                 return;
//#             } else if (x > 266 && x < 326 && y > 44 && y < 74) {
//#                 // Back button
//#                 confirmDialogImage = LazyLoad.confirmDialog(new String[] {
//#                     "Do you want to come",
//#                     "back to the temple?",
//#                     "Your puzzle process",
//#                     "will be lost!"
//#                 });
//#                 activeCommand = COMMAND_BACK;
//#                 autoCloseMenu = 60;
//#                 return;
//#             } else if(x > 328 && x < 388 && y > 44 && y < 74) {
//#                 // Reset button
//#                 confirmDialogImage = LazyLoad.confirmDialog(new String[] {
//#                     "Are you sure you",
//#                     "want to reset",
//#                     "this puzzle process?"
//#                 });
//#                 activeCommand = COMMAND_RESET;
//#                 autoCloseMenu = 60;
//#                 return;
//#             }
//#         }
//#         if (x > 240 & x < Main.SCREEN_WIDTH) {
//#             navbarTouching = true;
//#             moved = false;
//#             setActiveButton(x, y);
//#         }
//#elif ScreenWidth == 320
        if (templeId != TempleScene.TEMPLE_CYLOP) {
            if (x > 258 && x < 312 && y > 180 && y < 206) {
                // Hint button
                framePeriod = 50;
                curtainType = CURTAIN_HINT;
                curtainTimeline = 71;
                return;
            }
            else if (x > 258 && x < 312 && y > 128 && y < 154) {
                // Undo button
                undoExec();
                return;
            }
            else if (x > 258 && x < 312 && y > 154 && y < 180) {
                // Reset button
                confirmDialogImage = LazyLoad.confirmDialog(new String[] {
                    "Are you sure you",
                    "want to reset",
                    "this puzzle process?"
                });
                activeCommand = COMMAND_RESET;
                return;
            }
        }
        if (x > 256 && x < 314 && y > 218 && y < 236) {
            // Back button
            confirmDialogImage = LazyLoad.confirmDialog(new String[] {
                "Do you want to come",
                "back to the temple?",
                "Your puzzle process",
                "will be lost!"
            });
            activeCommand = COMMAND_BACK;
            return;
        }
//#endif
        if (x > 24 & x < 216 & y > 24 & y < 216) {
            viewpotTouching = true;
            setActiveCell(x, y);
        }
    }
    
    private void reset() {
        viewpotGraphic.drawImage(ImageHelper.loadImage("/images/playbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        byte tileRemain = 0;
        for (byte i = 0; i < 16; i++) {
            for (byte j = 0; j < 16; j++) {
                tileSprite.setFrame(cell[i][j] = defaultData[i][j]);
                tileSprite.setPosition(j * 12 + 24, i * 12 + 24);
                tileSprite.paint(viewpotGraphic);
                switch (cell[i][j]) {
                    case PlayScene.TILE_WANT:
                    case PlayScene.TILE_STICKY:
                        tileRemain++;
                        break;

                    case PlayScene.TILE_RED:
                        tileRemain--;
                        break;
                }
            }
        }
        tileStackY = 240 - 12 * tileRemain;
        undoCell = new StringBuffer();
        undoDirection = new StringBuffer();
        second = 0;
        calcPosible();
        updateCharacterSprite();
        confirmDialogImage = null;
    }
    
    private void confirmCommand() {
        switch (activeCommand) {
            case COMMAND_BACK:
                main.gotoTemple(templeId, true);
                break;
                
            case COMMAND_RESET:
                reset();
                break;
        }
        activeCommand = COMMAND_NONE;
    }
    
    protected void pointerDragged(int x, int y) {
        if (isSliding || curtainTimeline >= 0 || confirmDialogImage != null || hint != null)
            return;
        if (navbarTouching)
            setActiveButton(x, y);
        else if (viewpotTouching)
            setActiveCell(x, y);
    }
    
    protected void pointerReleased(int x, int y) {
//#if ScreenWidth == 400
//#         if (curtainTimeline >= 116 && x > 316 && y < 38)
//#             main.gotoTemple(templeId, true);
//#elif ScreenWidth == 320
        if (curtainTimeline >= 116 && x > 116 && x < 204 && y > 220)
            main.gotoTemple(templeId, true);
//#endif
        
        if (isSliding || curtainTimeline >= 0 || hint != null)
            return;
        
        if (confirmDialogImage != null) {
//#if ScreenWidth == 400
//#             if (x > 128 && x < 194 && y > 154 && y < 182) {
//#                 // yes
//#                 confirmCommand();
//#             } else if(x > 210 && x < 276 && y > 154 && y < 182) {
//#                 // no
//#                 confirmDialogImage = null;
//#                 activeCommand = COMMAND_NONE;
//#             }
//#elif ScreenWidth == 320
            if (x > 94 && x < 154 && y > 154 && y < 182) {
                // yes
                confirmCommand();
            } else if (x > 170 && x < 240 && y > 154 && y < 182) {
                // no
                confirmDialogImage = null;
                activeCommand = COMMAND_NONE;
            }
//#endif
            return;
        }
        if (templeId != TempleScene.TEMPLE_CYLOP || (cursor[0] == tutorialCell[0] && cursor[1] == tutorialCell[1])) {
            if (viewpotTouching) {
                if (isPossible)
                    throwingTimeline = 0;
                else
                    shruggingTimeline = 0;
//#if ScreenWidth == 320
                characterSprite.setFrame(posibleDirection * 3);
//#endif
                //int cellIndex = cursor[0] * 16 + cursor[1];
                //System.out.print(cellIndex + ",");
            }
//#if ScreenWidth == 400
//#             else if (navbarTouching) {
//#                 for (int i = 0; i < 5; i++) {
//#                     buttons[i].active = false;
//#                 }
//#                 if (activeCommand == COMMAND_FIRE) {
//#                     if (isPossible)
//#                         throwingTimeline = 0;
//#                     else
//#                         shruggingTimeline = 0;
//#                 }
//#                 else if (!moved) {
//#                     move();
//#                 }
//#             }
//#endif
        }
        activeCommand = COMMAND_NONE;
        navbarTouching = false;
        viewpotTouching = false;
    }
    
    /**
     * Sets activated cell by corresponding location in screen.
     */
    private void setActiveCell(int x, int y) {
        if (x >= 24 && x < 216 && y >= 24 && y < 216) {
            byte row = (byte)((y - 24) / 12);
            byte col = (byte)(((x - 24) % 192) / 12);
            if (row != cursor[0] || col != cursor[1]) {
                cursor[0] = row;
                cursorY = (short)(row * 12 + 24);
                cursor[1] = col;
                cursorX = (short)(col * 12 + 24);
                calcPosible();
                updateCharacterSprite();
                shruggingTimeline = -1;
            }
        }
    }
    
    private void move() {
        switch (activeCommand) {
            case COMMAND_UP:
                if (cursor[0] > 0) {
                    cursor[0]--;
                    cursorY -= 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;

            case COMMAND_RIGHT:
                if (cursor[1] < 15) {
                    cursor[1]++;
                    cursorX += 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;

            case COMMAND_DOWN:
                if (cursor[0] < 15) {
                    cursor[0]++;
                    cursorY += 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;

            case COMMAND_LEFT:
                if (cursor[1] > 0) {
                    cursor[1]--;
                    cursorX -= 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;
                
            default:
                return;
        }
        moved = true;
        shruggingTimeline = -1;
    }
    
    public void calcPosible() {
        if (cell[cursor[0]][cursor[1]] <= TILE_BLUE) {
            byte checked = 0, prevPosibleDirection = posibleDirection;
            while (checked < 4) {
                if (checkPosible(posibleDirection)) {
                    isPossible = true;
                    return;
                }
                if (++posibleDirection > COMMAND_LEFT)
                    posibleDirection = COMMAND_UP;
                checked++;
            }
            posibleDirection = prevPosibleDirection;
        }
        // if current cell have value greater than 4, or there is no possible direction
        isPossible = false;
    }
    
    private boolean checkPosible(byte direction) {
        short thisCell = cell[cursor[0]][cursor[1]];
        int i, stopCell;
        
        //== Thuật toán chung ==
        //nếu ô hiện hoạt trống
        //  nếu ô hiên hoạt = 15 thì kết luận luôn là không được
        //  còn nếu ô hiện hoạt < 15
        //      nếu ô phía sau trống thì kết luận luôn là không được
        //      còn nếu ô phía sau không trống
        //          bắt đầu kiểm tra xem có vướng không
        //hoặc (ô hiện hoạt không trống hoặc = 15) và có thể rút được
        //  bắt đầu kiểm tra xem có vướng không
        //================> rút gọn lại
        //tính ô phía sau (ngoại lệ nếu ô phía sau > 15 thì coi như nó trống
        //nếu (ô hiện hoạt trống và ô hiện hoạt < 15 và ô phía sau không trống) hoặc ô hiện hoạt có thể rút được
        //  bắt đầu kiểm tra xem có vướng không
        switch (direction) {
            case COMMAND_UP:
                stopCell = (cursor[0] < 15) ? cell[cursor[0]+1][cursor[1]] : 0;
                if ((cursor[0] < 15 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[0] - 1;
                    while (i > -1) {
                        if (cell[i][cursor[1]] > 2)
                            break;
                        i--;
                    }
                    if (i == -1)
                        return true;
                }
                break;
                
            case COMMAND_RIGHT:
                stopCell = (cursor[1] > 0) ? cell[cursor[0]][cursor[1]-1] : 0;
                if ((cursor[1] > 0 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[1] + 1;
                    while (i < 16) {
                        if (cell[cursor[0]][i] > 2)
                            break;
                        i++;
                    }
                    if (i == 16)
                        return true;
                }
                break;
                
            case COMMAND_DOWN:
                stopCell = (cursor[0] > 0) ? cell[cursor[0]-1][cursor[1]] : 0;
                if ((cursor[0] > 0 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[0] + 1;
                    while (i < 16) {
                        if (cell[i][cursor[1]] > 2)
                            break;
                        i++;
                    }
                    if (i == 16)
                        return true;
                }
                break;
                
            case COMMAND_LEFT:
                stopCell = (cursor[1] < 15) ? cell[cursor[0]][cursor[1]+1] : 0;
                if ((cursor[1] < 15 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[1] - 1;
                    while (i > -1) {
                        if (cell[cursor[0]][i] > 2)
                            break;
                        i--;
                    }
                    if (i == -1)
                        return true;
                }
                break;
        }
        return false;
    }
    
    public void updateCharacterSprite() {
        characterSprite.setFrame(posibleDirection * 3);
        switch (posibleDirection) {
            case COMMAND_UP:
                characterSprite.setPosition(cursorX-4, -2);
                shruggingSprite.setPosition(cursorX-4, -2);
                break;
                
            case COMMAND_DOWN:
                characterSprite.setPosition(cursorX-4, 216);
                shruggingSprite.setPosition(cursorX-4, 216);
                break;
                
            case COMMAND_LEFT:
                characterSprite.setPosition(4, cursorY-14);
                shruggingSprite.setPosition(4, cursorY-14);
                break;
                
            case COMMAND_RIGHT:
                characterSprite.setPosition(216, cursorY-14);
                shruggingSprite.setPosition(216, cursorY-14);
                break;
            
            default:
                return;
        }
        // update aim direction
        if (cell[cursor[0]][cursor[1]] <= 4) {
            aimDirection = posibleDirection;
            if (cell[cursor[0]][cursor[1]] <= 2)
                aimDirection += (posibleDirection >= 2) ? -2 : 2;
        }
    }
    
    private void setActiveButton(int x, int y) {
        for (int i = 0; i < 5; i++) {
            buttons[i].active = false;
        }
        for (int i = 0; i < 5; i++) {
            if (buttons[i].contains(x, y)) {
                buttons[i].active = true;
                activeCommand = buttons[i].getCommand();
                return;
            }
        }
        activeCommand = COMMAND_NONE;
    }
    
    public void closeHint() {
        curtainTimeline = -1;
        curtainType = CURTAIN_NONE;
        hint.dispose();
        hint = null;
        framePeriod = 100;
    }
}

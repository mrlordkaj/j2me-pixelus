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
import util.GraphicButton;
import util.ImageHelper;
import util.StringHelper;

/**
 *
 * @author Thinh Pham
 */
public class Play extends GamePage {
    public static final byte TILE_NONE = 0;
    public static final byte TILE_WANT = 1;
    public static final byte TILE_STICKY = 2;
    public static final byte TILE_RED = 3;
    public static final byte TILE_BLUE = 4;
    public static final byte TILE_GREEN = 5;
    public static final byte TILE_DARKBLUE = 6;
    public static final byte COMMAND_NONE = -1;
    public static final byte COMMAND_UP = 0;
    public static final byte COMMAND_RIGHT = 1;
    public static final byte COMMAND_DOWN = 2;
    public static final byte COMMAND_LEFT = 3;
    public static final byte COMMAND_FIRE = 4;
    public static final byte COMMAND_BACK = 5;
    public static final byte COMMAND_RESET = 6;
    public static final byte CURTAIN_NONE = -1;
    public static final byte CURTAIN_FINISH = 0;
    public static final byte CURTAIN_HINT = 1;
    
    public Image viewpotImage, sidebarImage, navImage, posibleMask, imposibleMask, confirmDialogImage;
    public Image slidingTile, stackImage, cellMask, curtainImage, puzzleCompleteImage, quickMenuImage, tutorialImage;
    public Image[] aimImage;
    public Sprite characterSprite, tileSprite, shruggingSprite, celebratingSprite;
    public int[] rgb = new int[16*16];
    private short slidingPositionX, slidingPositionY, slidingTargetX, slidingTargetY;
    private byte slidingDeltaX, slidingDeltaY;
    private boolean isSliding = false, slidingDone = false;
    private short autoCloseMenu = 0;
    
    private byte curtainType = CURTAIN_NONE;
    private int tutorialBallonX, tutorialBallonY;
    private int[] tutorialCell = new int[] { -1, -1 };
    
    public Graphics viewpotGraphic, slidingTileGraphic;
    
    private boolean isPaused = false, quickMenu = false, quickMenuOpening = false, quickMenuClosing = false;
    private int quickMenuY = -120;
    
    private int[] curtainX = new int[] {400, 400, 400, 400};
    
    private int puzzleId, templeId, bestTime, bestMove, bestMedal;
    private boolean hintUnlocked = false;
    private byte templeSolvedPuzzle, templePerfectPuzzle;
    
    public String puzzleTitle;
    public byte[][] cell = new byte[16][16], defaultData = new byte[16][16];
    //public byte tileRemain;
    public int tileStackY;
    public short stackTimeline = -1, tileStackTarget;
    private byte[] cursor = new byte[] {1, 1};
    private short cursorX = 36, cursorY = 36;
    //moved là để đảm bảo mỗi lần bấm sẽ di chuyển ít nhất 1 ô
    private boolean navbarTouching = false, viewpotTouching = false, moved = false;
    private byte throwingTimeline = -1, shruggingTimeline = -1, curtainTimeline = -1;
    private boolean isPosible = false;
    private byte posibleDirection = COMMAND_UP, aimDirection;
    private byte aimDistance = 0;
    private boolean aimClosing = false;
    private byte activeCommand = COMMAND_NONE;
    private byte frameTicker;
    private short second = 0;
    private StringBuffer undoCell = new StringBuffer(), undoDirection = new StringBuffer();
    
    private Main parent;
    private Hint hint;
    private Tutorial tutorial;
    public String hintData;
    public GraphicButton[] button;
    
    public int getPuzzleId() { return puzzleId; }
    public int getTempleId() { return templeId; }
    
    public Play(int _puzzleId, int _templeId, Main _parent) {
        super();
        parent = _parent;
        puzzleId = _puzzleId;
        templeId = _templeId;
        
        prepareResource();
        
        schedule = 100;
        new Thread(this).start();
    }
    
    protected void hideNotify() {
        isPaused = true;
    }
    
    protected void showNotify() {
        isPaused = false;
    }
    
    public void paint(Graphics g) {
        if(curtainTimeline > 0) {
            if(curtainTimeline < 93) {
                if(curtainType == CURTAIN_FINISH) {
                    g.drawImage(viewpotImage, 0, 0, Graphics.TOP | Graphics.LEFT);
                    celebratingSprite.paint(g);
                }
            } else {
                if(curtainType == CURTAIN_FINISH)
                    g.drawImage(puzzleCompleteImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                else if(curtainType == CURTAIN_HINT)
                    hint.paint(g);
            }
            for(int i = 0; i < 4; i++) {
                g.drawImage(curtainImage, curtainX[i], 0, Graphics.LEFT | Graphics.TOP);
            }
            return;
        }
        
        if(hint != null) {
            hint.paint(g);
            return;
        }
        
        //nếu đang ở trạng thái sliding thì chỉ vẽ khung chơi
        if(isSliding) {
            g.drawImage(viewpotImage, 0, 0, Graphics.TOP | Graphics.LEFT);
            if(!slidingDone) g.drawImage(slidingTile, slidingPositionX, slidingPositionY, Graphics.LEFT | Graphics.TOP);
            characterSprite.paint(g);
            g.drawImage(stackImage, 240, tileStackY, Graphics.LEFT | Graphics.TOP);
            return;
        }
        
        if(isLoading) {
            g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
            Main.loadingSprite.nextFrame();
            Main.loadingSprite.paint(g);
            g.setColor(255, 255, 255);
            g.drawString("loading...", 184, Main.SCREENSIZE_HEIGHT / 2 + 20, Graphics.LEFT | Graphics.BASELINE);
        } else {
            g.drawImage(viewpotImage, 0, 0, Graphics.TOP | Graphics.LEFT);
            g.drawImage(sidebarImage, 252, 0, Graphics.TOP | Graphics.LEFT);
            
            g.drawImage(stackImage, 240, tileStackY, Graphics.LEFT | Graphics.TOP);
            
            //vẽ con trỏ
            if(isPosible) {
                int aimArrowY, aimArrowX;
                switch(posibleDirection) {
                    case COMMAND_UP:
                        aimArrowY = cursorY - aimDistance;
                        while(aimArrowY > 24) {
                            g.drawImage(aimImage[aimDirection], cursorX, aimArrowY, Graphics.BOTTOM | Graphics.LEFT);
                            aimArrowY -= 24;
                        }
                        break;

                    case COMMAND_RIGHT:
                        aimArrowX = cursorX + aimDistance + 12;
                        while(aimArrowX < 216) {
                            g.drawImage(aimImage[aimDirection], aimArrowX, cursorY, Graphics.TOP | Graphics.LEFT);
                            aimArrowX += 24;
                        }
                        break;

                    case COMMAND_DOWN:
                        aimArrowY = cursorY + aimDistance + 12;
                        while(aimArrowY < 216) {
                            g.drawImage(aimImage[aimDirection], cursorX, aimArrowY, Graphics.TOP | Graphics.LEFT);
                            aimArrowY += 24;
                        }
                        break;

                    case COMMAND_LEFT:
                        aimArrowX = cursorX - aimDistance;
                        while(aimArrowX > 24) {
                            g.drawImage(aimImage[aimDirection], aimArrowX, cursorY, Graphics.TOP | Graphics.RIGHT);
                            aimArrowX -= 24;
                        }
                        break;
                }

                g.setColor(0xffffff);
            } else {
                g.setColor(0xff0000);
            }
            g.drawRect(cursorX, cursorY, 11, 11);
            g.drawRect(cursorX-1, cursorY-1, 13, 13);

            //vẽ nhân vật
            if(shruggingTimeline >= 0) shruggingSprite.paint(g);
            else characterSprite.paint(g);

            //làm nổi bật dòng và cột được chọn bằng cách bấm trực tiếp khung chơi
            if(viewpotTouching) {
                int x = 24;
                while(x < 216) {
                    if(isPosible) {
                        g.drawImage(posibleMask, x, cursorY, Graphics.LEFT | Graphics.TOP);
                        g.drawImage(posibleMask, cursorX, x, Graphics.LEFT | Graphics.TOP);
                    } else {
                        g.drawImage(imposibleMask, x, cursorY, Graphics.LEFT | Graphics.TOP);
                        g.drawImage(imposibleMask, cursorX, x, Graphics.LEFT | Graphics.TOP);
                    }
                    x += 12;
                }
            }
            
            for(int i = 0; i < 5; i++) {
                button[i].paint(g);
            }
            g.drawImage(navImage, 266, 131, Graphics.LEFT | Graphics.TOP);
            
            if(templeId != Temple.TEMPLE_CYLOP) {
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.setColor(0xff0000);
                g.drawString(Integer.toString(undoCell.length()), 362, 64, Graphics.RIGHT | Graphics.BASELINE);
                //g.drawString(Integer.toString(hintData.length()), 378, 82, Graphics.RIGHT | Graphics.BASELINE);
                g.drawString(Integer.toString(second), 362, 100, Graphics.RIGHT | Graphics.BASELINE);
                //g.drawString(strMoves, 328, 98, Graphics.HCENTER | Graphics.BASELINE);
                if(quickMenu || quickMenuOpening || quickMenuClosing) g.drawImage(quickMenuImage, 252, quickMenuY, Graphics.LEFT | Graphics.TOP);
            } else if(tutorialImage != null && curtainTimeline == -1) {
                g.drawImage(tutorialImage, tutorialBallonX, tutorialBallonY, Graphics.LEFT | Graphics.TOP);
            }
            
            if(confirmDialogImage != null) g.drawImage(confirmDialogImage, Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
        }
    }
    
    protected void update() {
        if(curtainTimeline >= 0) {
            updateCurtain();
            if(hint != null) hint.update();
            return;
        }
        
        //nếu đang trong trạng thái sliding thì không làm những việc khác
        if(isSliding) {
            updateSliding();
            return;
        }
        
        if(templeId != Temple.TEMPLE_CYLOP) {
            //vị trí quick menu
            if(quickMenuOpening) {
                if(quickMenuY < 0) quickMenuY += 10;
                else {
                    quickMenu = true;
                    quickMenuOpening = false;
                    schedule = 100;
                    autoCloseMenu = 60;
                }
                return;
            } else if(quickMenuClosing) {
                if(quickMenuY > -120) quickMenuY -= 10;
                else {
                    quickMenu = false;
                    quickMenuClosing = false;
                    schedule = 100;
                }
                return;
            }

            //tự động đóng menu
            if(quickMenu && autoCloseMenu > 0) {
                if(--autoCloseMenu == 0) {
                    quickMenuClosing = true;
                    schedule = 40;
                }
            }

            //tính số giây
            if(++frameTicker == 10) {
                second++;
                frameTicker = 0;
            }
        }
        
        //nếu đang bấm vào các nút
        //if(navbarTouching && activeCommand != COMMAND_NONE) move();
        if(activeCommand != COMMAND_NONE) move();
        
        //mũi vị trí các mũi tên ngắm
        if(isPosible) {
            //if(aimClosing) aimDistance--; else aimDistance++;
            aimDistance += aimClosing ? -1 : 1;
            if(aimDistance == 0 || aimDistance == 4) aimClosing = !aimClosing;
        }
        
        if(shruggingTimeline >= 0) {
            shruggingSprite.setFrame(shruggingTimeline);
            if(++shruggingTimeline > 7) shruggingTimeline = -1;
        } else if(throwingTimeline >= 0) {
            characterSprite.nextFrame();
            if(++throwingTimeline > 1) {
                if(cell[cursor[0]][cursor[1]] <= 2) {
                    if(tileStackY >= 240) {
                        shruggingTimeline = 0;
                        throwingTimeline = -1;
                        return;
                    }
                    pushTile();
                }
                else removeTile();
                
                boolean needUpdate = true;
                //nếu như trùng với dữ liệu cuối cùng của undo thì bỏ dữ liệu undo đó
                int dataIndex = undoCell.length();
                if(dataIndex > 0) {
                    dataIndex--;
                    int cellIndex = (int)undoCell.charAt(dataIndex);
                    if(cursor[0] == (byte)(cellIndex / 16) && cursor[1] == (byte)(cellIndex % 16)) {
                        undoCell.deleteCharAt(dataIndex);
                        undoDirection.deleteCharAt(dataIndex);
                        needUpdate = false;
                    }
                }
                if(needUpdate) {
                    //thêm cellIndex vào danh sách undo
                    undoCell.append((char)(cursor[0] * 16 + cursor[1]));
                    //thêm aimDirection vào danh sách undoDirection
                    undoDirection.append((char)posibleDirection);
                }
                
                //bắt đầu thực hiện
                characterSprite.setFrame(characterSprite.getFrame() - 2);
                isSliding = true;
                slidingDone = false;
                throwingTimeline = -1;
                stackTimeline = 0;
                schedule = 20;
            }
        }
    }
    
    private void updateSliding() {
        if(stackTimeline >= 0) {
            if(++stackTimeline < 7) {
                tileStackY += (tileStackY < tileStackTarget) ? 2 : -2;
            } else {
                stackTimeline = -1;
                if(slidingDone) {
                    slidingDone = false;
                    isSliding = false;
                    schedule = 100;
                    if(checkEndGame()) return;
                }
            }
        }
        if(!slidingDone) {
            slidingPositionX += slidingDeltaX;
            slidingPositionY += slidingDeltaY;
            if(slidingPositionX == slidingTargetX && slidingPositionY == slidingTargetY) finishSliding();
        }
    }
    
    private void updateCurtain() {
        if(curtainTimeline < 127) curtainTimeline++;
        else return;
        if(curtainTimeline < 3) {
            celebratingSprite.nextFrame();
        } else if(curtainTimeline == 3) {
            schedule = 25;
        } else if(curtainTimeline < 50) {
            int j, left, top;
            for(byte i = 0; i < 16; i++) {
                j = curtainTimeline - i * 2 - 4;
                if(j < 0) break;
                else if(j < 16) {
                    left = j * 12 + 24;
                    top = i * 12 + 24;
                    viewpotGraphic.setColor(rgb[i * 16 + j]);
                    viewpotGraphic.fillRect(left, top, 12, 12);
                    viewpotGraphic.drawImage(cellMask, left, top, Graphics.LEFT | Graphics.TOP);
                }
            }
        } else if(curtainTimeline == 50) {
            schedule = 50;
        } else if(curtainTimeline > 70 && curtainTimeline < 93) {
            for(byte i = 0; i < 4; i++) {
                if(curtainX[i] > i * 100 - 20) curtainX[i] -= 20;
            }
        } else if(curtainTimeline == 93) {
            if(curtainType == CURTAIN_FINISH) fillPuzzleComplete();
            else if(curtainType == CURTAIN_HINT) prepareHint();
        } else if(curtainTimeline < 116) {
            int j;
            for(byte i = 0; i < 4; i++) {
                //j = 96 - (3 - i) * 5 - 1;
                j = 94 + 5 * i;
                if(curtainTimeline > j) curtainX[i] += 20;
            }
        } else {
            if(curtainType == CURTAIN_FINISH) schedule = 32767;
            else if(curtainType == CURTAIN_HINT) schedule = 20;
        }
    }
    
    private void prepareHint() {
        hint = new Hint(hintData, this);
        quickMenu = false;
        quickMenuY = -120;
    }
    
    private void fillPuzzleComplete() {
        Graphics g = puzzleCompleteImage.getGraphics();
        if(templeId == Temple.TEMPLE_CYLOP) {
            //tutorial
            //cập nhật rms
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                //số tutorial đã giải
                byte tutorialPassed = Byte.parseByte(StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC)), "#")[0]);
                StringBuffer tutorialData = new StringBuffer();
                tutorialData.append(Integer.toString((puzzleId > tutorialPassed)?puzzleId:tutorialPassed));
                tutorialData.append("#0#");
                tutorialData.append((puzzleId > tutorialPassed && tutorialPassed == 8)?"2":"0");
                byte[] writer = tutorialData.toString().getBytes();
                rs.setRecord(Main.RMS_USER_TEMPLESTATISTIC, writer, 0, writer.length);
                rs.closeRecordStore();
            } catch (RecordStoreException ex) {}
            
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Learn more tricks", Main.SCREENSIZE_WIDTH / 2, 168, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("from the other puzzles", Main.SCREENSIZE_WIDTH / 2, 182, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("in the Cylop's cave!", Main.SCREENSIZE_WIDTH / 2, 196, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("not for", 66, 216, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("tutorial", 66, 232, Graphics.HCENTER | Graphics.BASELINE);
        } else {
            int numMoves = undoCell.length();
            byte myMedal = 3;
            if(numMoves == hintData.length()) myMedal = 0;
            else if(numMoves <= hintData.length() + 5) myMedal = 1;
            else if(numMoves <= hintData.length() + 10) myMedal = 2;

            //cập nhật rms
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                String notifyStatus = "0";
                byte[] writer;
                //số puzzle đã giải
                int solvedPuzzle = Integer.parseInt(new String(rs.getRecord(Main.RMS_USER_SOLVEDPUZZLE)));
                if(bestMove == 0) { //nếu là puzzle mới chưa chơi lần nào
                    solvedPuzzle++;
                    writer = Integer.toString(solvedPuzzle).getBytes();
                    rs.setRecord(Main.RMS_USER_SOLVEDPUZZLE, writer, 0, writer.length);
                    templeSolvedPuzzle++;
                    
                    //ghi thông báo
                    //mở puzzle cuối
                    if(templeSolvedPuzzle == Temple.totalPuzzle(templeId) - 1) notifyStatus = "1";
                    //temple hoàn thành
                    else if(templeSolvedPuzzle == Temple.totalPuzzle(templeId)) notifyStatus = "2";
                    //mở temple mới
                    else {
                        for(byte i = 0; i < Temple.TEMPLE_REQUIRE.length; i++) {
                            if(solvedPuzzle == Temple.TEMPLE_REQUIRE[i]) {
                                notifyStatus = "4";
                                if(i != Temple.TEMPLE_CYLOP) parent.openTemple(i);
                                break;
                            }
                        }
                    }
                } else if(bestMedal > 0 && myMedal == 0 && templePerfectPuzzle == Temple.totalPuzzle(templeId) - 1) { //temple hoàn thành hoàn hảo
                    notifyStatus = "3";
                }

                //temple
                StringBuffer newData = new StringBuffer();
                //số puzzle đã hoàn thành
                newData.append(Integer.toString(templeSolvedPuzzle));
                newData.append("#");
                //số puzzle hoàn hảo
                newData.append(Integer.toString((bestMedal > 0 && myMedal == 0)?++templePerfectPuzzle:templePerfectPuzzle));
                newData.append("#");
                //tình trạng thông báo
                newData.append(notifyStatus);
                writer = newData.toString().getBytes();
                rs.setRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId, writer, 0, writer.length);

                //puzzle
                newData = new StringBuffer();
                newData.append((numMoves < bestMove || bestMove == 0)?numMoves:bestMove);
                newData.append("#");
                newData.append((second < bestTime || bestTime == 0)?second:bestTime);
                newData.append("#");
                newData.append(Integer.toString((myMedal < bestMedal)?myMedal:bestMedal));
                newData.append("#");
                newData.append(hintUnlocked?"1":"0");
                writer = newData.toString().getBytes();
                rs.setRecord(puzzleId - 9, writer, 0, writer.length);

                rs.closeRecordStore();
            } catch (RecordStoreException ex) {}
            
            //vẽ hình
            g.drawString(numMoves + " moves", Main.SCREENSIZE_WIDTH / 2, 168, Graphics.HCENTER | Graphics.BASELINE);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            String[] description;
            switch(myMedal) {
                case 0:
                    description = new String[] { "This is the world record!" };
                    break;
                    
                case 1:
                    description = new String[] {
                        "Within 5 moves of",
                        "The world record!"
                    };
                    break;
                    
                case 2:
                    description = new String[] {
                        "Within 10 moves of",
                        "The world record!"
                    };
                    break;
                    
                default:
                    description = new String[] {};
                    break;
            }
            for(byte i = 0; i < description.length; i++) {
                g.drawString(description[i], Main.SCREENSIZE_WIDTH / 2, i*16 + 184, Graphics.HCENTER | Graphics.BASELINE);
            }
            ImageHelper.medalSprite.setFrame(myMedal);
            ImageHelper.medalSprite.setPosition(238, 148);
            ImageHelper.medalSprite.paint(g);
            description = null;
            g.drawString(((second < bestTime || bestTime == 0)?second:bestTime) + " secs", 66, 218, Graphics.HCENTER | Graphics.BASELINE);
        }
    }
    
    private void pushTile() {
        tileStackTarget = (short)(tileStackY + 12);
        //chuẩn bị hình ảnh slidingTile
        tileSprite.setPosition(0, 0);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]] + 3);
        //tileSprite.paint(slidingTile.getGraphics());
        tileSprite.paint(slidingTileGraphic);
        
        //chuẩn bị thông tin vị trí xuất phát, hướng di chuyển
        switch(aimDirection) {
            case COMMAND_UP:
                slidingPositionX = cursorX;
                slidingPositionY = 216;
                slidingDeltaX = 0;
                slidingDeltaY = -12;
                break;

            case COMMAND_RIGHT:
                slidingPositionX = 12;
                slidingPositionY = cursorY;
                slidingDeltaX = 12;
                slidingDeltaY = 0;
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
        }
        
        //chuẩn bị thông tin về đích đến
        slidingTargetX = cursorX;
        slidingTargetY = cursorY;
    }
    
    private void removeTile() {
        tileStackTarget = (short)(tileStackY - 12);
        //cập nhật lại hình ảnh background
        tileSprite.setPosition(cursorX, cursorY);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]] - 3);
        tileSprite.paint(viewpotGraphic);
        
        //chuẩn bị hình ảnh slidingTile
        tileSprite.setPosition(0, 0);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]]);
        //tileSprite.paint(slidingTile.getGraphics());
        tileSprite.paint(slidingTileGraphic);
        
        //chuẩn bị thông tin vị trí xuất phát
        slidingPositionX = cursorX;
        slidingPositionY = cursorY;
        
        //chuẩn bị thông tin đích đến, hướng di chuyển
        switch(aimDirection) {
            case COMMAND_UP:
                slidingTargetX = cursorX;
                slidingTargetY = 12;
                slidingDeltaX = 0;
                slidingDeltaY = -12;
                break;

            case COMMAND_RIGHT:
                slidingTargetX = 216;
                slidingTargetY = cursorY;
                slidingDeltaX = 12;
                slidingDeltaY = 0;
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
        }
    }
    
    private void finishSliding() {
        slidingDone = true;
        if(stackTimeline == -1) {
            isSliding = false;
            schedule = 100;
        }
        if(cell[cursor[0]][cursor[1]] <= 2) {
            //đưa vào
            viewpotGraphic.drawImage(slidingTile, slidingPositionX, slidingPositionY, Graphics.LEFT | Graphics.TOP);
            cell[cursor[0]][cursor[1]] += 3;
            if(checkEndGame()) return;
        } else {
            //lấy ra
            cell[cursor[0]][cursor[1]] -= 3;
        }
        
        //kiểm tra kết thúc
        calcPosible();
        updateCharacterSprite();
        
        if(templeId == Temple.TEMPLE_CYLOP) prepareTutorialStep();
    }
    
    private boolean checkEndGame() {
        if(tileStackY >= 240) {
            for(short i = 0; i < 16; i++) {
                for(short j = 0; j < 16; j++) {
                    if(cell[i][j] == TILE_WANT || cell[i][j] == TILE_STICKY) return false;
                }
            }
            celebratingSprite.setFrame(0);
            celebratingSprite.setPosition(characterSprite.getX(), characterSprite.getY() - 9);
            isPosible = false;
            curtainType = CURTAIN_FINISH;
            curtainTimeline = 0;
            return true;
        }
        return false;
    }
    
    private void undoExec() {
        int dataIndex = undoCell.length();
        
        if(dataIndex == 0) return;
        
        dataIndex--;
        
        //lấy dữ liệu ô
        int cellIndex = (int)undoCell.charAt(dataIndex);
        cursor[0] = (byte)(cellIndex / 16);
        cursor[1] = (byte)(cellIndex % 16);
        cursorX = (short)(cursor[1] * 12 + 24);
        cursorY = (short)(cursor[0] * 12 + 24);
        
        //lấy dữ liệu hướng
        posibleDirection = (byte)undoDirection.charAt(dataIndex);
        updateCharacterSprite();
        if(cell[cursor[0]][cursor[1]] == TILE_GREEN) aimDirection = posibleDirection;
        
        //thực hiện undo
        if(cell[cursor[0]][cursor[1]] <= 2) pushTile();
        else removeTile();
        isSliding = true;
        slidingDone = false;
        stackTimeline = 0;
        schedule = 20;
        
        //xóa dữ liệu undo
        undoCell.deleteCharAt(dataIndex);
        undoDirection.deleteCharAt(dataIndex);
    }
    
    private void prepareResource() {
        if(templeId != Temple.TEMPLE_CYLOP) {
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
                String[] puzzleData = StringHelper.split(new String(rs.getRecord(puzzleId - 9)), "#");
                bestMove = Integer.parseInt(puzzleData[0]);
                bestTime = Integer.parseInt(puzzleData[1]);
                bestMedal = Integer.parseInt(puzzleData[2]);
                hintUnlocked = puzzleData[3].equals("1");
                puzzleData = null;
                String[] templeData = StringHelper.split(new String(rs.getRecord(Main.RMS_USER_TEMPLESTATISTIC + templeId)), "#");
                templeSolvedPuzzle = Byte.parseByte(templeData[0]);
                templePerfectPuzzle = Byte.parseByte(templeData[1]);
                //templeNotifyLastPuzzle = templeData[2].equals("1");
                rs.closeRecordStore();
            } catch (RecordStoreException ex) {}
        } else {
            tutorial = Tutorial.getTutorial(puzzleId);
            
        }
        slidingTile = Image.createImage(12, 12);
        slidingTileGraphic = slidingTile.getGraphics();
        new Loader(this).start();
    }
    
    public void prepareTutorialStep() {
        int step = undoCell.length();
        tutorialImage = tutorial.getBallon(step);
        int cellIndex = tutorial.getCellIndex(step);
        int row = tutorialCell[0] = cellIndex / 16;
        int col = tutorialCell[1] = cellIndex % 16;
        if(row <= 7 && col <= 7) { //góc trên bên trái
            tutorialBallonX = col * 12 + 30;
            tutorialBallonY = row * 12 + 30;
        } else if(row <= 7 && col >= 8) { //góc trên bên phải
            tutorialBallonX = col * 12 - 97;
            tutorialBallonY = row * 12 + 30;
        } else if(row >= 8 && col <= 7) { //góc dưới bên trái
            tutorialBallonX = col * 12 + 30;
            tutorialBallonY = row * 12 - 80;
        } else {
            tutorialBallonX = col * 12 - 97;
            tutorialBallonY = row * 12 - 80;
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(hint != null) {
            hint.pointerPressed(x, y);
            return;
        }
        
        if(isSliding || curtainTimeline >= 0 || confirmDialogImage != null) return;
        
        if(templeId != Temple.TEMPLE_CYLOP && !quickMenu && x > 272 && x < 380 && y > 12 && y < 38) {
            schedule = 40;
            quickMenuOpening = true;
            return;
        } else if(quickMenu && x > 272 && x < 380 && y > 78 && y <104) {
            schedule = 40;
            quickMenuClosing = true;
            return;
        }
        
        if(quickMenu && !quickMenuOpening && !quickMenuClosing) {
            if(x > 266 && x < 326 && y > 12 && y < 42) {
                //Nút Hint
                schedule = 50;
                curtainType = CURTAIN_HINT;
                curtainTimeline = 71;
                autoCloseMenu = 60;
                return;
            } else if(x > 328 && x < 388 && y > 12 && y < 42) {
                //Nút Undo
                undoExec();
                autoCloseMenu = 60;
                return;
            } else if(x > 266 && x < 326 && y > 44 && y < 74) {
                //Nút Back
                confirmDialogImage = Loader.confirmDialog(new String[] {
                    "Do you want to come",
                    "back to the temple?",
                    "Your puzzle process",
                    "will be lost!"
                });
                activeCommand = COMMAND_BACK;
                autoCloseMenu = 60;
                return;
            } else if(x > 328 && x < 388 && y > 44 && y < 74) {
                //Nút Reset
                confirmDialogImage = Loader.confirmDialog(new String[] {
                    "Are you sure you",
                    "want to reset",
                    "this puzzle process?"
                });
                activeCommand = COMMAND_RESET;
                autoCloseMenu = 60;
                return;
            }
        }
        
        if(x > 240 & x < Main.SCREENSIZE_WIDTH) {
            navbarTouching = true;
            moved = false;
            setActiveButton(x, y);
        } else if(x > 24 & x < 216 & y > 24 & y < 216) {
            viewpotTouching = true;
            setActiveCell(x, y);
        }
    }
    
    private void reset() {
        viewpotGraphic.drawImage(ImageHelper.loadImage("/images/playbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        byte tileRemain = 0;
        for(byte i = 0; i < 16; i++) {
            for(byte j = 0; j < 16; j++) {
                tileSprite.setFrame(cell[i][j] = defaultData[i][j]);
                tileSprite.setPosition(j * 12 + 24, i * 12 + 24);
                tileSprite.paint(viewpotGraphic);
                switch(cell[i][j]) {
                    case Play.TILE_WANT:
                    case Play.TILE_STICKY:
                        tileRemain++;
                        break;

                    case Play.TILE_RED:
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
        switch(activeCommand) {
            case COMMAND_BACK:
                parent.gotoTemple(templeId, true);
                break;
                
            case COMMAND_RESET:
                reset();
                break;
        }
        activeCommand = COMMAND_NONE;
    }
    
    protected void pointerDragged(int x, int y) {
        if(isSliding || curtainTimeline >= 0 || confirmDialogImage != null || hint != null) return;
        
        if(navbarTouching) {
            setActiveButton(x, y);
        } else if(viewpotTouching) {
            setActiveCell(x, y);
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(curtainTimeline >= 116 && x > 316 && y < 38) parent.gotoTemple(templeId, true);
        
        if(isSliding || curtainTimeline >= 0 || hint != null) return;
        
        if(confirmDialogImage != null) {
            if(x > 128 && x < 194 && y > 154 && y < 182) {
                //yes
                confirmCommand();
            } else if(x > 210 && x < 276 && y > 154 && y < 182) {
                //no
                confirmDialogImage = null;
                activeCommand = COMMAND_NONE;
            }
            return;
        }
        
        if(templeId != Temple.TEMPLE_CYLOP || (cursor[0] == tutorialCell[0] && cursor[1] == tutorialCell[1])) {
            if(navbarTouching) {
                for(int i = 0; i < 5; i++) {
                    button[i].active = 0;
                }
                if(activeCommand == COMMAND_FIRE) {
                    if(isPosible) throwingTimeline = 0;
                    else shruggingTimeline = 0;
                } else if(!moved) move();
            } else if(viewpotTouching) {
                if(isPosible) throwingTimeline = 0;
                else shruggingTimeline = 0;
                //int cellIndex = cursor[0] * 16 + cursor[1];
                //System.out.print(cellIndex + ",");
            }
        }
        activeCommand = COMMAND_NONE;
        navbarTouching = false;
        viewpotTouching = false;
    }
    
    private void setActiveCell(int x, int y) {
        if(x > 24 && x < 216 && y > 24 && y < 216) {
            byte row = (byte)((y - 24) / 12);
            byte col = (byte)(((x - 24) % 192) / 12);
            if(row == cursor[0] && col == cursor[1]) return;

            cursor[0] = row;
            cursorY = (short)(row * 12 + 24);
            cursor[1] = col;
            cursorX = (short)(col * 12 + 24);
            calcPosible();
            updateCharacterSprite();
            shruggingTimeline = -1;
        }
    }
    
    private void move() {
        switch(activeCommand) {
            case COMMAND_UP:
                if(cursor[0] > 0) {
                    cursor[0]--;
                    cursorY -= 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;

            case COMMAND_RIGHT:
                if(cursor[1] < 15) {
                    cursor[1]++;
                    cursorX += 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;

            case COMMAND_DOWN:
                if(cursor[0] < 15) {
                    cursor[0]++;
                    cursorY += 12;
                    calcPosible();
                    updateCharacterSprite();
                }
                break;

            case COMMAND_LEFT:
                if(cursor[1] > 0) {
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
        if(cell[cursor[0]][cursor[1]] <= TILE_BLUE) {
            byte checked = 0, prevPosibleDirection = posibleDirection;
            while (checked < 4) {
                if(checkPosible(posibleDirection)) {
                    isPosible = true;
                    return;
                }
                if(++posibleDirection > COMMAND_LEFT) posibleDirection = COMMAND_UP;
                checked++;
            }
            
            posibleDirection = prevPosibleDirection;
        }
        
        //nếu ô hiện tại lớn hơn giá trị 4 hoặc không có hướng nào khả thi
        isPosible = false;
    }
    
    private boolean checkPosible(byte direction) {
        short thisCell = cell[cursor[0]][cursor[1]];
        int i, stopCell;
        
        //== Thuật toán chung ==
        //nếu ô hiện hoạt trống
        //  nếu ô hiên hoạt = 15 thì kết luận luôn là không được
        //  còn nếu ô hiện hoạt < 15
        //      nếu ô phía sau trống thì kế luận luôn là không được
        //      còn nếu ô phía sau không trống
        //          bắt đầu kiểm tra xem có vướng không
        //hoặc (ô hiện hoạt không trống hoặc = 15) và có thể rút được
        //  bắt đầu kiểm tra xem có vướng không
        //================> rút gọn lại
        //tính ô phía sau (ngoại lệ nếu ô phía sau > 15 thì coi như nó trống
        //nếu (ô hiện hoạt trống và ô hiện hoạt < 15 và ô phía sau không trống) hoặc ô hiện hoạt có thể rút được
        //  bắt đầu kiểm tra xem có vướng không
        switch(direction) {
            case COMMAND_UP:
                if(cursor[0] < 15) stopCell = cell[cursor[0]+1][cursor[1]];
                else stopCell = 0;
                if((cursor[0] < 15 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[0] - 1;
                    while(i > -1) {
                        if(cell[i][cursor[1]] > 2) break;
                        i--;
                    }
                    if(i == -1) return true;
                }
                break;
                
            case COMMAND_RIGHT:
                if(cursor[1] > 0) stopCell = cell[cursor[0]][cursor[1]-1];
                else stopCell = 0;
                if((cursor[1] > 0 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[1] + 1;
                    while(i < 16) {
                        if(cell[cursor[0]][i] > 2) break;
                        i++;
                    }
                    if(i == 16) return true;
                }
                break;
                
            case COMMAND_DOWN:
                if(cursor[0] > 0) stopCell = cell[cursor[0]-1][cursor[1]];
                else stopCell = 0;
                if((cursor[0] > 0 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[0] + 1;
                    while(i < 16) {
                        if(cell[i][cursor[1]] > 2) break;
                        i++;
                    }
                    if(i == 16) return true;
                }
                break;
                
            case COMMAND_LEFT:
                if(cursor[1] < 15) stopCell = cell[cursor[0]][cursor[1]+1];
                else stopCell = 0;
                if((cursor[1] < 15 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
                    i = cursor[1] - 1;
                    while(i > -1) {
                        if(cell[cursor[0]][i] > 2) break;
                        i--;
                    }
                    if(i == -1) return true;
                }
                break;
        }
        
        return false;
    }
    
    public void updateCharacterSprite() {
        characterSprite.setFrame(posibleDirection * 3);
        switch(posibleDirection) {
            case COMMAND_UP:
                characterSprite.setPosition(cursorX-4, -2);
                shruggingSprite.setPosition(cursorX-4, -2);
                break;
                
            case COMMAND_RIGHT:
                characterSprite.setPosition(216, cursorY-14);
                shruggingSprite.setPosition(216, cursorY-14);
                break;
                
            case COMMAND_DOWN:
                characterSprite.setPosition(cursorX-4, 216);
                shruggingSprite.setPosition(cursorX-4, 216);
                break;
                
            case COMMAND_LEFT:
                characterSprite.setPosition(4, cursorY-14);
                shruggingSprite.setPosition(4, cursorY-14);
                break;
                
            default:
                return;
        }
        
        //cập nhật hướng ngắm
        if(cell[cursor[0]][cursor[1]] <= 4) {
            aimDirection = posibleDirection;
            if(cell[cursor[0]][cursor[1]] <= 2) {
                aimDirection += (posibleDirection >= 2) ? -2 : 2;
            }
        }
    }
    
    private void setActiveButton(int x, int y) {
        for(int i = 0; i < 5; i++) {
            button[i].active = 0;
        }
        
        for(int i = 0; i < 5; i++) {
            if(button[i].contains(x, y)) {
                button[i].active = 1;
                activeCommand = button[i].getCommand();
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
        schedule = 100;
    }
    
    public void dispose() {
        isLoading = true;
        pageLooping = false;
        viewpotImage = null;
        sidebarImage = null;
        navImage = null;
        posibleMask = null;
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
        button = null;
    }
}

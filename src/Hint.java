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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import util.Button;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class Hint {
    
    private static final int TILE_BOARD_X = 104;
    
    private Image backgroundImage, tileImage;
    private Sprite tileSprite;
    private Graphics backgroundGraphic, tileGraphic;
    private byte[][] cell = new byte[16][16];
    private int[] cursor = new int[] {1, 1};
    private byte aimDirection = PlayScene.COMMAND_NONE;
    private int slidingPositionX, slidingPositionY, slidingDeltaX, slidingDeltaY, slidingTargetX, slidingTargetY;
    private boolean isSliding = false, isAuto = false, isLoading = true;
    private byte nextTurn = 0;
    private final PlayScene parent;
    private String data;
    private StringBuffer processData;
    
    private final Button btnClose = new Button(0, 0, 80, 50);
//#if ScreenWidth == 400
//#     private final Button btnOne = new Button(330, 128, 50, 50);
//#     private final Button btnAll = new Button(330, 184, 50, 50);
//#elif ScreenWidth == 320
    private final Button btnOne = new Button(4, 48, 40, 40);
    private final Button btnAll = new Button(54, 48, 40, 40);
//#endif
    
    public Hint(String data, PlayScene parent) {
        this.parent = parent;
        this.data = data;
        backgroundImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        backgroundGraphic = backgroundImage.getGraphics();
        backgroundGraphic.drawImage(ImageHelper.loadImage("/images/hintbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        tileSprite = new Sprite(ImageHelper.loadImage("/images/tile.png"), 12, 12);
        reset();
        tileImage = Image.createImage(12, 12);
        tileGraphic = tileImage.getGraphics();
        isLoading = false;
    }
    
    private void reset() {
        processData = new StringBuffer(data);
        for (byte i = 0; i < 16; i++) {
            for (byte j = 0; j < 16; j++) {
                tileSprite.setFrame(cell[i][j] = parent.defaultData[i][j]);
                tileSprite.setPosition(j * 12 + TILE_BOARD_X, i * 12 + 24);
                tileSprite.paint(backgroundGraphic);
            }
        }
    }
    
    public void update() {
        if (!isLoading) {
            if (isSliding) {
                slidingPositionX += slidingDeltaX;
                slidingPositionY += slidingDeltaY;
                if (slidingPositionX == slidingTargetX && slidingPositionY == slidingTargetY)
                    finishSliding();
            }
            else if (isAuto) {
                if (--nextTurn <= 0) {
                    if (processData.length() > 0)
                        doTurn();
                    else
                        isAuto = false;
                }
            }
        }
    }
    
    public void paint(Graphics g) {
        if (!isLoading) {
            g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
            if (isSliding)
                g.drawImage(tileImage, slidingPositionX, slidingPositionY, Graphics.LEFT | Graphics.TOP);
        }
    }
    
    public void pointerPressed(int x, int y) {
        if (btnClose.contains(x, y)) {
            parent.closeHint();
        }
        if (isSliding || isAuto) {
            return;
        }
        if (btnOne.contains(x, y)) {
            // one move button
            if (processData.length() > 0)
                doTurn();
            else
                reset();
        } else if (btnAll.contains(x, y)) {
            // all moves button
            isAuto = true;
            reset();
        }
    }
    
    private void doTurn() {
        short cellIndex = (short) processData.charAt(0);
        cursor[0] = cellIndex / 16;
        cursor[1] = cellIndex % 16;
        if (cell[cursor[0]][cursor[1]] <= 2)
            pushTile();
        else
            removeTile();
        isSliding = true;
        processData.deleteCharAt(0);
    }
    
    private void pushTile() {
        // prepare slidingTile image
        tileSprite.setPosition(0, 0);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]] + 3);
        //tileSprite.paint(slidingTile.getGraphics());
        tileSprite.paint(tileGraphic);
        
        calcDirection();
        
        // prepare start position, move direction
        switch (aimDirection) {
            case PlayScene.COMMAND_UP:
                slidingPositionX = cursor[1] * 12 + TILE_BOARD_X;
                slidingPositionY = 216;
                slidingDeltaX = 0;
                slidingDeltaY = -12;
                break;

            case PlayScene.COMMAND_RIGHT:
                slidingPositionX = TILE_BOARD_X - 12;
                slidingPositionY = cursor[0] * 12 + 24;
                slidingDeltaX = 12;
                slidingDeltaY = 0;
                break;

            case PlayScene.COMMAND_DOWN:
                slidingPositionX = cursor[1] * 12 + TILE_BOARD_X;
                slidingPositionY = 12;
                slidingDeltaX = 0;
                slidingDeltaY = 12;
                break;

            case PlayScene.COMMAND_LEFT:
                slidingPositionX = 192 + TILE_BOARD_X;
                slidingPositionY = cursor[0] * 12 + 24;
                slidingDeltaX = -12;
                slidingDeltaY = 0;
                break;
        }
        
        // prepare destination
        slidingTargetX = cursor[1] * 12 + 104;
        slidingTargetY = cursor[0] * 12 + 24;
    }
    
    private void removeTile() {
        // update background image
        tileSprite.setPosition(cursor[1] * 12 + 104, cursor[0] * 12 + 24);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]] - 3);
        tileSprite.paint(backgroundGraphic);
        
        // prepare slidingTile image
        tileSprite.setPosition(0, 0);
        tileSprite.setFrame(cell[cursor[0]][cursor[1]]);
        //tileSprite.paint(slidingTile.getGraphics());
        tileSprite.paint(tileGraphic);
        
        calcDirection();
        
        // prepare start position
        slidingPositionX = cursor[1] * 12 + TILE_BOARD_X;
        slidingPositionY = cursor[0] * 12 + 24;
        
        // prepare destination position, move direction
        switch (aimDirection) {
            case PlayScene.COMMAND_UP:
                slidingTargetX = cursor[1] * 12 + TILE_BOARD_X;
                slidingTargetY = 12;
                slidingDeltaX = 0;
                slidingDeltaY = -12;
                break;

            case PlayScene.COMMAND_RIGHT:
                slidingTargetX = 192 + TILE_BOARD_X;
                slidingTargetY = cursor[0] * 12 + 24;
                slidingDeltaX = 12;
                slidingDeltaY = 0;
                break;

            case PlayScene.COMMAND_DOWN:
                slidingTargetX = cursor[1] * 12 + TILE_BOARD_X;
                slidingTargetY = 216;
                slidingDeltaX = 0;
                slidingDeltaY = 12;
                break;

            case PlayScene.COMMAND_LEFT:
                slidingTargetX = TILE_BOARD_X - 12;
                slidingTargetY = cursor[0] * 12 + 24;
                slidingDeltaX = -12;
                slidingDeltaY = 0;
                break;
        }
    }
    
    private void finishSliding() {
        if (cell[cursor[0]][cursor[1]] <= 2) {
            // push in
            backgroundGraphic.drawImage(tileImage, slidingPositionX, slidingPositionY, Graphics.LEFT | Graphics.TOP);
            cell[cursor[0]][cursor[1]] += 3;
        } else {
            // pull out
            cell[cursor[0]][cursor[1]] -= 3;
        }
        isSliding = false;
        if (isAuto)
            nextTurn = 10;
    }
    
    private void calcDirection() {
        byte thisCell = cell[cursor[0]][cursor[1]];
        int i, stopCell;
        
        // check up direction
        stopCell = (cursor[0] < 15) ? cell[cursor[0]+1][cursor[1]] : 0;
        if ((cursor[0] < 15 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
            i = cursor[0] - 1;
            while (i > -1) {
                if (cell[i][cursor[1]] > 2)
                    break;
                i--;
            }
            if (i == -1) {
                aimDirection = (thisCell <= 2) ? PlayScene.COMMAND_DOWN : PlayScene.COMMAND_UP;
                return;
            }
        }
        
        // check right direction
        stopCell = (cursor[1] > 0) ? cell[cursor[0]][cursor[1]-1] : 0;
        if ((cursor[1] > 0 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
            i = cursor[1] + 1;
            while (i < 16) {
                if(cell[cursor[0]][i] > 2) break;
                i++;
            }
            if (i == 16) {
                aimDirection = (thisCell <= 2) ? PlayScene.COMMAND_LEFT : PlayScene.COMMAND_RIGHT;
                return;
            }
        }
        
        // check down direction
        stopCell = (cursor[0] > 0) ? cell[cursor[0]-1][cursor[1]] : 0;
        if ((cursor[0] > 0 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
            i = cursor[0] + 1;
            while (i < 16) {
                if (cell[i][cursor[1]] > 2)
                    break;
                i++;
            }
            if (i == 16) {
                aimDirection = (thisCell <= 2) ? PlayScene.COMMAND_UP : PlayScene.COMMAND_DOWN;
                return;
            }
        }
        
        // check left direction
        stopCell = (cursor[1] < 15) ? cell[cursor[0]][cursor[1]+1] : 0;
        if ((cursor[1] < 15 && thisCell <= 2 && stopCell > 2) || (thisCell == 3 || thisCell == 4)) {
            i = cursor[1] - 1;
            while (i > -1) {
                if (cell[cursor[0]][i] > 2)
                    break;
                i--;
            }
            if (i == -1) {
                aimDirection = (thisCell <= 2) ? PlayScene.COMMAND_RIGHT : PlayScene.COMMAND_LEFT;
            }
        }
    }
    
    public void dispose() {
        isLoading = true;
        backgroundGraphic = null;
        tileGraphic = null;
        backgroundImage = null;
        tileImage = null;
        cell = null;
        cursor = null;
        data = null;
        processData = null;
        tileSprite = null;
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import util.FileHelper;

/**
 *
 * @author Thinh Pham
 */
abstract class GameHelper {
    
    static final Sprite MEDAL_SPRITE = new Sprite(FileHelper.loadImage("/images/medal.png"), 24, 24);
    
//#if ScreenWidth == 400
//#     private static final int MEDAL_SPRITE_TOP = 40;
//#elif ScreenWidth == 320
    private static final int MEDAL_SPRITE_TOP = 24;
//#endif
    
    static void drawPuzzleCover(int puzzleId, int x, int y, int pixelSize, Graphics g, Image pixelMask, int medal) {
        Puzzle puzzle = Puzzle.getPuzzle(puzzleId);
        String data = puzzle.getData();
        for (short i = 0; i < 16*16; i++) {
            int row = i / 16;
            int col = i % 16;
            switch (data.charAt(i)) {
                case PlayScene.TILE_NONE: //0
                    g.setColor(0xeabc6e);
                    break;
                    
                case PlayScene.TILE_WANT: //?
                    g.setColor(0xffe0b1);
                    break;
                    
                case PlayScene.TILE_STICKY: //S
                    g.setColor(0x69db76);
                    break;
                    
                case PlayScene.TILE_BLUE: //+
                    g.setColor(0x5df0e8);
                    break;
                    
                case PlayScene.TILE_RED: //-
                    g.setColor(0xf95d5d);
                    break;
                    
                case PlayScene.TILE_DARKBLUE: //X
                    g.setColor(0x228aa2);
                    break;
                    
                default:
                    g.setColor(0x000000);
                    break;
            }
            g.fillRect(col * pixelSize + x, row * pixelSize + y, pixelSize, pixelSize);
        }
        g.drawImage(pixelMask, x, y, Graphics.LEFT | Graphics.TOP);
        if (medal < Puzzle.MEDAL_NONE) {
            GameHelper.MEDAL_SPRITE.setFrame(medal);
            GameHelper.MEDAL_SPRITE.setPosition(x, y + MEDAL_SPRITE_TOP);
            GameHelper.MEDAL_SPRITE.paint(g);
        }
    }
    
    static void drawPuzzleImage(int puzzleId, int x, int y, int pixelSize, Graphics g, Image pixelMask, int medal) {
        String name = Puzzle.getPuzzle(puzzleId).getName();
        Image img = FileHelper.loadImage("/data/images/" + name + ".gif");
        int[] rgb = new int[16*16];
        img.getRGB(rgb, 0, 16, 0, 0, 16, 16);
        for (int i = 0; i < rgb.length; ++i) {
            int row = i / 16;
            int col = i % 16;
            g.setColor(rgb[i]);
            g.fillRect(col * pixelSize + x, row * pixelSize + y, pixelSize, pixelSize);
        }
        g.drawImage(pixelMask, x, y, Graphics.LEFT | Graphics.TOP);
        if (medal < Puzzle.MEDAL_NONE) {
            GameHelper.MEDAL_SPRITE.setFrame(medal);
            GameHelper.MEDAL_SPRITE.setPosition(x, y + MEDAL_SPRITE_TOP);
            GameHelper.MEDAL_SPRITE.paint(g);
        }
    }
    
    static Image createPixelMask(int pixelSize) {
        int imgSize = pixelSize*16;
        Image mask = Image.createImage(imgSize, imgSize);
        Graphics g = mask.getGraphics();
        g.setColor(0, 0, 0);
        for (int i = 1; i < 16; i++) {
            // draw horizontal
            g.drawLine(0, i*pixelSize, imgSize, i*pixelSize);
            // draw vertical
            g.drawLine(i*pixelSize, 0, i*pixelSize, imgSize);
        }
        int[] rgb = new int[imgSize*imgSize];
        mask.getRGB(rgb, 0, imgSize, 0, 0, imgSize, imgSize);
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] &= (rgb[i] == 0xffffffff) ? 0x00ffffff : 0x22000000;
        }
        return Image.createRGBImage(rgb, imgSize, imgSize, true);
    }
    
    static String[] readPuzzleData(int puzzleId) throws RuntimeException {
        try {
            String[] rs = new String[3];
            InputStream is = InputStream.class.getResourceAsStream("/data/puzzles.dat");
            StringBuffer sb = new StringBuffer();
            int c;
            int curLine = 1;
            while (((c = is.read()) != -1)) {
                if (c == '\n') {
                    ++curLine;
                    c = is.read();
                }
                if (curLine == puzzleId) {
                    if (c == '#') {
                        rs[0] = sb.toString();
                        sb = new StringBuffer();
                    } else {
                        sb.append((char) c);
                    }
                } else if(curLine > puzzleId) {
                    rs[1] = sb.toString();
                    rs[2] = FileHelper.readFile("/data/" + rs[0] + ".dat");
                    return rs;
                }
            }
            rs[1] = sb.toString();
            rs[2] = FileHelper.readFile("/data/" + rs[0] + ".dat");
            return rs;
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    private static final int DIALOG_WIDTH = 252;
    private static final int DIALOG_HEIGHT = 182;
    
    static Image confirmDialog(String[] message){
        Image dialog = Image.createImage(DIALOG_WIDTH, DIALOG_HEIGHT);
        Graphics g = dialog.getGraphics();
        // draw dialog
        g.drawImage(FileHelper.loadImage("/images/dialog.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        // draw content
        g.setColor(0, 0, 0);
        for (int i = 0; i < message.length; i++) {
            g.drawString(message[i], DIALOG_WIDTH / 2, 57 + i * 20, Graphics.HCENTER | Graphics.BASELINE);
        }
        // draw okie button
        g.drawImage(FileHelper.loadImage("/images/buttongold.png"), 84, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Okie", 84, 146, Graphics.HCENTER | Graphics.BASELINE);
        // draw cancel button
        g.drawImage(FileHelper.loadImage("/images/buttonsilver.png"), DIALOG_WIDTH - 84, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Cancel", DIALOG_WIDTH - 84, 146, Graphics.HCENTER | Graphics.BASELINE);
        // erase background
        int[] rgb = new int[DIALOG_WIDTH * DIALOG_HEIGHT];
        dialog.getRGB(rgb, 0, DIALOG_WIDTH, 0, 0, DIALOG_WIDTH, DIALOG_HEIGHT);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffff00ff)
                rgb[i] &= 0x00ffffff;
        }
        return Image.createRGBImage(rgb, DIALOG_WIDTH, DIALOG_HEIGHT, true);
    }
    
    static Image messageDialog(String[] message){
        Image dialog = Image.createImage(DIALOG_WIDTH, DIALOG_HEIGHT);
        Graphics g = dialog.getGraphics();
        // draw dialog
        g.drawImage(FileHelper.loadImage("/images/dialog.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        // draw content
        g.setColor(0, 0, 0);
        for (int i = 0; i < message.length; i++) {
            g.drawString(message[i], DIALOG_WIDTH / 2, 57 + i * 20, Graphics.HCENTER | Graphics.BASELINE);
        }
        // draw okie button
        g.drawImage(FileHelper.loadImage("/images/buttongold.png"), DIALOG_WIDTH / 2, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Okie", DIALOG_WIDTH / 2, 146, Graphics.HCENTER | Graphics.BASELINE);
        // erase background
        int[] rgb = new int[DIALOG_WIDTH * DIALOG_HEIGHT];
        dialog.getRGB(rgb, 0, DIALOG_WIDTH, 0, 0, DIALOG_WIDTH, DIALOG_HEIGHT);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffff00ff)
                rgb[i] &= 0x00ffffff;
        }
        return Image.createRGBImage(rgb, DIALOG_WIDTH, DIALOG_HEIGHT, true);
    }
}

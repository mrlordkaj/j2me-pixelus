/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ImageHelper;

/**
 *
 * @author Thinh
 */
public abstract class GameHelper {
    
//#if ScreenWidth == 400
    private static final int MEDAL_SPRITE_TOP = 40;
//#elif ScreenWidth == 320
//#     private static final int MEDAL_SPRITE_TOP = 24;
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
            ImageHelper.MEDAL_SPRITE.setFrame(medal);
            ImageHelper.MEDAL_SPRITE.setPosition(x, y + MEDAL_SPRITE_TOP);
            ImageHelper.MEDAL_SPRITE.paint(g);
        }
    }
    
    static void drawPuzzleImage(int puzzleId, int x, int y, int pixelSize, Graphics g, Image pixelMask, int medal) {
        String name = Puzzle.getPuzzle(puzzleId).getName();
        Image img = ImageHelper.loadImage("/data/images/" + name + ".gif");
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
            ImageHelper.MEDAL_SPRITE.setFrame(medal);
            ImageHelper.MEDAL_SPRITE.setPosition(x, y + MEDAL_SPRITE_TOP);
            ImageHelper.MEDAL_SPRITE.paint(g);
        }
    }
    
    private static final int DIALOG_WIDTH = 252;
    private static final int DIALOG_HEIGHT = 182;
    
    public static Image confirmDialog(String[] message){
        Image dialog = Image.createImage(DIALOG_WIDTH, DIALOG_HEIGHT);
        Graphics g = dialog.getGraphics();
        // draw dialog
        g.drawImage(ImageHelper.loadImage("/images/dialog.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        // draw content
        g.setColor(0, 0, 0);
        for (int i = 0; i < message.length; i++) {
            g.drawString(message[i], DIALOG_WIDTH / 2, 57 + i * 20, Graphics.HCENTER | Graphics.BASELINE);
        }
        // draw okie button
        g.drawImage(ImageHelper.loadImage("/images/buttongold.png"), 84, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Okie", 84, 146, Graphics.HCENTER | Graphics.BASELINE);
        // draw cancel button
        g.drawImage(ImageHelper.loadImage("/images/buttonsilver.png"), DIALOG_WIDTH - 84, 140, Graphics.HCENTER | Graphics.VCENTER);
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
    
    public static Image messageDialog(String[] message){
        Image dialog = Image.createImage(DIALOG_WIDTH, DIALOG_HEIGHT);
        Graphics g = dialog.getGraphics();
        // draw dialog
        g.drawImage(ImageHelper.loadImage("/images/dialog.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        // draw content
        g.setColor(0, 0, 0);
        for (int i = 0; i < message.length; i++) {
            g.drawString(message[i], DIALOG_WIDTH / 2, 57 + i * 20, Graphics.HCENTER | Graphics.BASELINE);
        }
        // draw okie button
        g.drawImage(ImageHelper.loadImage("/images/buttongold.png"), DIALOG_WIDTH / 2, 140, Graphics.HCENTER | Graphics.VCENTER);
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

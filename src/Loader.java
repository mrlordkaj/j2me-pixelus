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
//#if ScreenWidth == 400
//# import util.GraphicButton;
//#endif
import util.DataHelper;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class Loader extends Thread {
    
    private final GameScene parent;
    
    public Loader(GameScene parent) {
        this.parent = parent;
    }
    
    public void run() {
        if (parent instanceof IslandScene) {
            loadIslandMapResource((IslandScene) parent);
        }
        else if (parent instanceof TempleScene) {
            loadTempleResource((TempleScene) parent);
        }
        else if (parent instanceof PlayScene) {
            loadPlayResource((PlayScene) parent);
        }
        try {
            Thread.sleep(1000);
            System.gc();
        } catch (InterruptedException ex) { }
        parent.isLoading = false;
    }
    
    private void loadIslandMapResource(IslandScene map) {
        map.islandImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        Graphics g = map.islandImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/islandmap.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        ImageHelper.MEDAL_SPRITE.setFrame(0);
        for (int i = 0; i < map.totalOpenedTemple; i++) {
            if (map.getTempleCompleted(i)) {
                // draw lighted temple
                g.drawImage(ImageHelper.loadImage("/images/map" + Story.CHARACTER_NAMES[i].toLowerCase() + "b.png"), IslandScene.TEMPLE_RECTANGLE[i][0], IslandScene.TEMPLE_RECTANGLE[i][1], Graphics.LEFT | Graphics.TOP);
                // draw medal to perfect
                if (map.templeIsPerfect(i)) {
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
        
        map.lightingImage = ImageHelper.loadImage("/images/lighting.png");
        map.templeArrowImage = ImageHelper.loadImage("/images/templearrow.png");
        map.starSprite = new Sprite(ImageHelper.loadImage("/images/star.png"), 100, 16);
        map.starSprite.setPosition(25, 168);
        if (map.newTemple == 0) {
            map.story = Story.getStory(Story.STORY_CYLOP_CYLOP, map);
        }
    }
    
//#if ScreenWidth == 400
//#     private static final int PUZZLE_IMAGE_SIZE = 68;
//#     private static final int IMAGE_PIXEL_SIZE = 4;
//#elif ScreenWidth == 320
    private static final int PUZZLE_IMAGE_SIZE = 53;
    private static final int IMAGE_PIXEL_SIZE = 3;
//#endif
    
    private void loadTempleResource(TempleScene temple) {
        Image lockImage = ImageHelper.loadImage("/images/lockpuzzleoverlay.png");
        Image pixelMask = ImageHelper.createPixelMask(IMAGE_PIXEL_SIZE);
        
        // draw puzzle list
        int templeId = temple.getTempleId();
        int numPuzzle = Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId];
        int width = PUZZLE_IMAGE_SIZE * 3 - 4;
        int height = temple.getPuzzleViewHeight();
        temple.puzzleViewImage = Image.createImage(width, height);
        Graphics g = temple.puzzleViewImage.getGraphics();
        if (temple.getTempleId() == TempleScene.TEMPLE_CYLOP) {
            // if tutorial temple
            g.setColor(0x585866);
            g.fillRect(0, 0, width, height);
            for (int i = 1; i <= numPuzzle; i++) {
                int y = ((i - 1) / 3) * PUZZLE_IMAGE_SIZE;
                int x = ((i - 1) % 3) * PUZZLE_IMAGE_SIZE;
                if (i <= temple.getSolvedPuzzle()) {
                    drawPuzzleImage(i, x, y, IMAGE_PIXEL_SIZE, g, pixelMask, Puzzle.MEDAL_NONE);
                }
                else {
                    drawPuzzleCover(i, x, y, IMAGE_PIXEL_SIZE, g, pixelMask, Puzzle.MEDAL_NONE);
                }
                if (i > temple.getSolvedPuzzle() + 1) {
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
                if (temple.bestAmountTurn(i) > 0)
                    drawPuzzleImage(i + Puzzle.PUZZLE_FIRSTID[templeId], x, y, IMAGE_PIXEL_SIZE, g, pixelMask, temple.medal(i));
                else
                    drawPuzzleCover(i + Puzzle.PUZZLE_FIRSTID[templeId], x, y, IMAGE_PIXEL_SIZE, g, pixelMask, temple.medal(i));
                System.gc();
            }
            if (!temple.lastPuzzleIsUnlocked()) {
                g.drawImage(lockImage, width, height, Graphics.RIGHT | Graphics.BOTTOM);
            }
        }
        // remaining temples
        temple.backgroundImage = ImageHelper.loadImage("/images/temple" + Story.CHARACTER_NAMES[temple.getTempleId()].toLowerCase() + ".png");
        temple.buttonImage = ImageHelper.loadImage("/images/buttongold.png");
        temple.scrollerImage = ImageHelper.loadImage("/images/scroller.png");
        // change framerate
        temple.framePeriod = 40;
    }
    
    private void loadPlayResource(PlayScene play) {
        play.tileSprite = new Sprite(ImageHelper.loadImage("/images/tile.png"), 12, 12);
        play.viewpotImage = Image.createImage(252, 240);
        play.viewpotGraphic = play.viewpotImage.getGraphics();
        play.viewpotGraphic.drawImage(ImageHelper.loadImage("/images/playbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        int puzzleId = play.getPuzzleId();
        Puzzle myPuzzle = Puzzle.getPuzzle(puzzleId);
        play.puzzleTitle = myPuzzle.getTitle();
        String data = myPuzzle.getData();
        String name = myPuzzle.getName();
        String title = myPuzzle.getTitle();
        int row, col;
        byte value, tileRemain = 0, totalTile = 0;
        for (short i = 0; i < 16*16; i++) {
            row = i / 16;
            col = i % 16;
            value = (byte) data.charAt(i);
            play.tileSprite.setFrame(value);
            play.tileSprite.setPosition(col * 12 + 24, row * 12 + 24);
            play.tileSprite.paint(play.viewpotGraphic);
            play.cell[row][col] = play.defaultData[row][col] = value;
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
        if (play.getTempleId() != TempleScene.TEMPLE_CYLOP)
            play.hintData = DataHelper.readFile("/data/hints/" + name + ".dat");
        
        ImageHelper.loadImage("/data/images/" + name + ".gif")
                .getRGB(play.rgb, 0, 16, 0, 0, 16, 16);
        
        int stackHeight = 12 * totalTile;
        play.stackImage = Image.createImage(12, stackHeight);
        Graphics g = play.stackImage.getGraphics();
        play.tileSprite.setFrame(PlayScene.TILE_BLUE);
        for (int i = stackHeight - 12; i >= 0; i -= 12) {
            play.tileSprite.setPosition(0, i);
            play.tileSprite.paint(g);
        }
        play.tileStackY = 240 - 12 * tileRemain;
        
        int[] rgb = new int[12*12];
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] = 0x44ffffff;
        }
        play.possibleMask = Image.createRGBImage(rgb, 12, 12, true);
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] = 0x22ff0000;
        }
        play.imposibleMask = Image.createRGBImage(rgb, 12, 12, true);
        
//#if ScreenWidth == 400
//#         play.puzzleCompleteImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
//#         g = play.puzzleCompleteImage.getGraphics();
//#         g.drawImage(ImageHelper.loadImage("/images/puzzlecompleted.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
//#         drawPuzzleImage(puzzleId, 161, 50, 5, g, ImageHelper.createPixelMask(5), 3);
//#         g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#         g.setColor(0x000000);
//#         g.drawString(title, Main.SCREEN_WIDTH / 2 + 1, 148 + 1, Graphics.HCENTER | Graphics.BASELINE);
//#         g.setColor(0xffd800);
//#         g.drawString(title, Main.SCREEN_WIDTH / 2, 148, Graphics.HCENTER | Graphics.BASELINE);
//#         
//#         Image gamepadImage = ImageHelper.loadImage("/images/navbutton.png");
//#         play.button = new GraphicButton[] {
//#             new GraphicButton(gamepadImage, PlayScene.COMMAND_UP, 308, 131, 40, 30),
//#             new GraphicButton(gamepadImage, PlayScene.COMMAND_RIGHT, 350, 164, 40, 30),
//#             new GraphicButton(gamepadImage, PlayScene.COMMAND_DOWN, 308, 197, 40, 30),
//#             new GraphicButton(gamepadImage, PlayScene.COMMAND_LEFT, 266, 164, 40, 30),
//#             new GraphicButton(gamepadImage, PlayScene.COMMAND_FIRE, 308, 164, 40, 30)
//#         };
//#         
//#         play.sidebarImage = Image.createImage(148, 240);
//#         g = play.sidebarImage.getGraphics();
//#         g.drawImage(ImageHelper.loadImage("/images/sidebarbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
//#         if (play.getTempleId() == TempleScene.TEMPLE_CYLOP) {
//#             g.drawImage(ImageHelper.loadImage("/images/tutorialsidebar.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
//#             g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#             String[] description = Tutorial.getDescription(play.getPuzzleId());
//#             for (int i = 0; i < description.length; i++) {
//#                 g.drawString(description[i], 75, 14*i + 24, Graphics.HCENTER | Graphics.BASELINE);
//#             }
//#         } else {
//#             g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//#             g.setColor(0xff0000);
//#             g.drawString(Integer.toString(play.hintData.length()), 126, 82, Graphics.RIGHT | Graphics.BASELINE);
//#         }
//#elif ScreenWidth == 320
        play.puzzleCompleteImage = Image.createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        g = play.puzzleCompleteImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/puzzlecompleted.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        drawPuzzleImage(puzzleId, 128, 48, 4, g, ImageHelper.createPixelMask(4), 3);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.setColor(0x000000);
        g.drawString(title, Main.SCREEN_WIDTH / 2 + 1, 130 + 1, Graphics.HCENTER | Graphics.BASELINE);
        g.setColor(0xffd800);
        g.drawString(title, Main.SCREEN_WIDTH / 2, 130, Graphics.HCENTER | Graphics.BASELINE);
        
        play.sidebarImage = Image.createImage(68, 240);
        g = play.sidebarImage.getGraphics();
        if (play.getTempleId() == TempleScene.TEMPLE_CYLOP) {
            g.drawImage(ImageHelper.loadImage("/images/tutorialsidebar.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            String[] description = Tutorial.getDescription(play.getPuzzleId());
            for (int i = 0; i < description.length; i++) {
                g.drawString(description[i], 34, 14*i + 18, Graphics.HCENTER | Graphics.BASELINE);
            }
        } else {
            g.drawImage(ImageHelper.loadImage("/images/sidebarbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0x00ff00);
            g.drawString(Integer.toString(play.hintData.length()), 32, 94, Graphics.HCENTER | Graphics.BASELINE);
        }
//#endif
        
        play.characterSprite = new Sprite(ImageHelper.loadImage("/images/tilemaster.png"), 20, 26);
        play.characterSprite.setPosition(20, -2);
        
        play.aimImage = new Image[] {
            ImageHelper.loadImage("/images/aimup.png"),
            ImageHelper.loadImage("/images/aimright.png"),
            ImageHelper.loadImage("/images/aimdown.png"),
            ImageHelper.loadImage("/images/aimleft.png")
        };
        play.navImage = ImageHelper.loadImage("/images/navigator.png");
        play.shruggingSprite = new Sprite(ImageHelper.loadImage("/images/shrugging.png"), 20, 26);
        play.celebratingSprite = new Sprite(ImageHelper.loadImage("/images/celebrating.png"), 20, 35);
        play.cellMask = ImageHelper.loadImage("/images/cellmask.png");
        play.curtainImage = ImageHelper.loadImage("/images/curtain.png");
        play.quickMenuImage = ImageHelper.loadImage("/images/quickmenu.png");
        play.calcPosible();
        play.updateCharacterSprite();
        
        if (play.getTempleId() == TempleScene.TEMPLE_CYLOP) {
            play.prepareTutorialStep();
        }
    }
    
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
